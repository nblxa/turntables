package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Utils;
import io.github.nblxa.turntables.exception.StructureException;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class NamedColAsserter implements ColAsserter {
  private final OrderedColAsserter colAsserter = new OrderedColAsserter();

  private static List<Tab.Col> orderedByName(Iterable<Tab.Col> cols) {
    return Utils.stream(cols)
        .sorted(Comparator.comparing(Tab.Col::name))
        .collect(Collectors.toList());
  }

  private static void checkForDuplicates(List<Tab.Col> names, String tab) {
    List<String> duplicates = names.stream()
        .map(Tab.Col::name)
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
    List<Tab.Col> expOrdered = orderedByName(expected);
    List<Tab.Col> actOrdered = orderedByName(actual);
    checkForDuplicates(expOrdered, "expected");
    checkForDuplicates(actOrdered, "actual");
    if (!colAsserter.match(expOrdered, actOrdered)) {
      return false;
    }
    for (int i = 0; i < expOrdered.size(); i++) {
      String expName = expOrdered.get(i).name();
      String actName = actOrdered.get(i).name();
      if (!expName.equals(actName)) {
        return false;
      }
    }
    return true;
  }
}
