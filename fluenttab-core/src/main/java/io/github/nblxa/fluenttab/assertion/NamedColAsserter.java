package io.github.nblxa.fluenttab.assertion;

import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.Utils;
import io.github.nblxa.fluenttab.exception.StructureException;
import io.github.nblxa.fluenttab.exception.UnnamedColException;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class NamedColAsserter implements ColAsserter {
  private final OrderedColAsserter colAsserter = new OrderedColAsserter();

  private static List<Tab.Col> orderedByName(Iterable<Tab.Col> cols) {
    return Utils.stream(cols)
        .sorted(Comparator.comparing(c -> ((Tab.Named) c).name()))
        .collect(Collectors.toList());
  }

  private static void checkForDuplicates(List<Tab.Col> names, String tab) {
    List<String> duplicates = names.stream()
        .map(c -> ((Tab.Named) c).name())
        .collect(Collectors.groupingBy(Function.identity()))
        .values()
        .stream()
        .filter(l -> l.size() > 1)
        .flatMap(List::stream)
        .distinct()
        .sorted()
        .collect(Collectors.toList());
    if (!duplicates.isEmpty()) {
      throw new StructureException("Duplicate column names in " + tab + ": "
          + String.join(", ", duplicates));
    }
  }

  @Override
  public boolean match(@NonNull Iterable<Tab.Col> expected, @NonNull Iterable<Tab.Col> actual) {
    if (Utils.stream(expected).allMatch(c -> c instanceof Tab.Named)
        && Utils.stream(actual).allMatch(c -> c instanceof Tab.Named)) {
      List<Tab.Col> expOrdered = orderedByName(expected);
      List<Tab.Col> actOrdered = orderedByName(actual);
      checkForDuplicates(expOrdered, "expected");
      checkForDuplicates(actOrdered, "actual");
      if (!colAsserter.match(expOrdered, actOrdered)) {
        return false;
      }
      for (int i = 0; i < expOrdered.size(); i++) {
        String expName = ((Tab.Named) expOrdered.get(i)).name();
        String actName = ((Tab.Named) actOrdered.get(i)).name();
        if (!expName.equals(actName)) {
          return false;
        }
      }
      return true;
    } else {
      throw new UnnamedColException();
    }
  }
}
