package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.exception.StructureException;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class NamedColAsserter implements ColAsserter {
  @NonNull
  private final OrderedColAsserter colAsserter = new OrderedColAsserter();

  @NonNull
  private static List<Tab.Col> orderedByName(@NonNull List<Tab.Col> cols) {
    return cols.stream()
        .sorted(Comparator.comparing(Tab.Col::name))
        .collect(Collectors.toList());
  }

  @Override
  public boolean match(@NonNull List<Tab.Col> expected, @NonNull List<Tab.Col> actual) {
    List<Tab.Col> expOrdered = orderedByName(expected);
    List<Tab.Col> actOrdered = orderedByName(actual);
    if (expected.size() != actual.size()) {
      return false;
    }
    checkForDuplicates(expOrdered, "expected");
    checkForDuplicates(actOrdered, "actual");
    for (int i = 0; i < expOrdered.size(); i++) {
      if (!matchCols(expOrdered.get(i), actOrdered.get(i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean checkKey(@NonNull List<Tab.Col> expected, @NonNull List<Tab.Col> actual) {
    List<String> expKeyNames = names(expected);
    List<String> actKeyNames = names(actual);
    return namesAreEqual(expKeyNames, actKeyNames);
  }

  @Override
  public boolean matchCols(@NonNull Tab.Col expected, @NonNull Tab.Col actual) {
    return colAsserter.matchCols(expected, actual) && matchNames(expected.name(), actual.name());
  }

  @NonNull
  private static List<String> names(List<Tab.Col> namedCols) {
    return namedCols.stream()
        .filter(Tab.Col::isKey)
        .map(Tab.Col::name)
        .collect(Collectors.toList());
  }

  private static void checkForDuplicates(@NonNull List<Tab.Col> cols, @NonNull String tab) {
    List<String> duplicates = cols.stream()
        .map(Tab.Col::name)
        .map(NamedColAsserter::handleCase)
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

  private boolean matchNames(String expected, String actual) {
    if (Turntables.getSettings().nameMode == Settings.NameMode.CASE_INSENSITIVE) {
      return expected.equalsIgnoreCase(actual);
    } else {
      return expected.equals(actual);
    }
  }

  @NonNull
  private static String handleCase(@NonNull String name) {
    if (Turntables.getSettings().nameMode == Settings.NameMode.CASE_INSENSITIVE) {
      return name.toLowerCase();
    } else {
      return name;
    }
  }

  private boolean namesAreEqual(@NonNull List<String> expNames, @NonNull List<String> actNames) {
    Iterator<String> expIter = expNames.iterator();
    Iterator<String> actIter = actNames.iterator();
    while (expIter.hasNext() && actIter.hasNext()) {
      String exp = expIter.next();
      String act = actIter.next();
      if (!handleCase(exp).equals(handleCase(act))) {
        return false;
      }
    }
    return !expIter.hasNext() && !actIter.hasNext();
  }
}
