package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.exception.StructureException;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class NamedColAsserter implements ColAsserter {
  @NonNull
  private final OrderedColAsserter colAsserter = new OrderedColAsserter();

  @NonNull
  private static List<Tab.NamedCol> orderedByName(@NonNull List<Tab.NamedCol> cols) {
    return cols.stream()
        .sorted(Comparator.comparing(Tab.NamedCol::name))
        .collect(Collectors.toList());
  }

  private static void checkForDuplicates(@NonNull List<Tab.NamedCol> names, @NonNull String tab) {
    List<String> duplicates = names.stream()
        .map(Tab.NamedCol::name)
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

  @NonNull
  private static List<Tab.NamedCol> namedColList(@NonNull List<Tab.Col> unnamed) {
    List<Tab.NamedCol> res = new ArrayList<>();
    for (Tab.Col c: unnamed) {
      res.add(namedCol(c));
    }
    return Collections.unmodifiableList(res);
  }

  @NonNull
  private static Tab.NamedCol namedCol(@NonNull Tab.Col col) {
    if (col instanceof Tab.NamedCol) {
      return (Tab.NamedCol) col;
    } else {
      throw new IllegalArgumentException("Cannot match unnamed cols by name!");
    }
  }

  @Override
  public boolean match(@NonNull List<Tab.Col> expected, @NonNull List<Tab.Col> actual) {
    return matchNamed(namedColList(expected), namedColList(actual));
  }

  @Override
  public boolean checkKey(@NonNull List<Tab.Col> expected, @NonNull List<Tab.Col> actual) {
    List<String> expKeyNames = names(expected);
    List<String> actKeyNames = names(actual);
    return expKeyNames.equals(actKeyNames);
  }

  private static List<String> names(List<Tab.Col> namedCols) {
    return namedCols.stream()
        .map(NamedColAsserter::namedCol)
        .filter(Tab.Col::isKey)
        .map(Tab.NamedCol::name)
        .collect(Collectors.toList());
  }

  private boolean matchNamed(@NonNull List<Tab.NamedCol> expected,
                             @NonNull List<Tab.NamedCol> actual) {
    List<Tab.NamedCol> expOrdered = orderedByName(expected);
    List<Tab.NamedCol> actOrdered = orderedByName(actual);
    checkForDuplicates(expOrdered, "expected");
    checkForDuplicates(actOrdered, "actual");
    if (!colAsserter.match(new ArrayList<>(expOrdered), new ArrayList<>(actOrdered))) {
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
