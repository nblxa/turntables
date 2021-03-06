package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Utils;
import io.github.nblxa.turntables.exception.TooManyPermutationsException;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class UnorderedRowAsserter extends AbstractRowAsserter {
  private final long rowPermutationLimit;
  private long permutationCount = 0L;

  private List<Tab.Row> expected;
  private List<Tab.Row> actual;

  private List<Optional<Tab.Row>> expOrdered;
  private List<Optional<Tab.Row>> actOrdered;

  UnorderedRowAsserter(@NonNull List<Tab.Row> expected, @NonNull List<Tab.Row> actual,
                       long rowPermutationLimit, @NonNull ValAsserter valAsserter) {
    super(valAsserter);
    this.rowPermutationLimit = rowPermutationLimit;
    this.expected = expected;
    this.actual = actual;

    ImmutableMatchList matchList = createMatchList();
    expOrdered = orderExpected(this.expected, matchList, this.actual.size());
    actOrdered = orderActual(this.actual, matchList, this.expected.size());
  }

  @NonNull
  private static Comparator<Tab.Row> rowComparator() {
    return (iter1, iter2) -> {
      List<Tab.Val> list1 = iter1.vals();
      List<Tab.Val> list2 = iter2.vals();
      for (int i = 0; i < list1.size(); i++) {
        String val1 = list1.get(i).toString();
        String val2 = list2.get(i).toString();
        int compare = val1.compareTo(val2);
        if (compare != 0) {
          return compare;
        }
      }
      return 0;
    };
  }

  @NonNull
  private static Map<Integer, ImmutableBitSet> withLeastCardinalityFirstElement(
      @NonNull final Map<Integer, ImmutableBitSet> map1,
      @NonNull final Map<Integer, ImmutableBitSet> map2
  ) {
    if (map1.isEmpty()) {
      return map1;
    }
    if (map2.isEmpty()) {
      return map2;
    }

    ImmutableBitSet bitSet1 = map1.values().iterator().next();
    ImmutableBitSet bitSet2 = map2.values().iterator().next();

    if (bitSet1.cardinality() <= bitSet2.cardinality()) {
      return map1;
    } else {
      return map2;
    }
  }

  @NonNull
  private static ImmutableBitSet expectedsMatchingActual(
      final int actualIndex,
      @NonNull final Map<Integer, ImmutableBitSet> expectedToMatchingActuals
  ) {
    BitSet bitSet = new BitSet(expectedToMatchingActuals.size());
    for (Map.Entry<Integer, ImmutableBitSet> e : expectedToMatchingActuals.entrySet()) {
      int i = e.getKey();
      ImmutableBitSet matchSet = e.getValue();
      if (matchSet.get(actualIndex)) {
        bitSet.set(i);
      }
    }
    return new ImmutableBitSet(bitSet);
  }

  @NonNull
  private static Map<Integer, ImmutableBitSet> sortByCardinality(
      @NonNull final Map<Integer, ImmutableBitSet> matchMatrixAB) {
    return matchMatrixAB.entrySet().stream()
        .sorted(Comparator.comparing(e -> e.getValue().cardinality()))
        .collect(LinkedHashMap::new,
            (map, e) -> map.put(e.getKey(), e.getValue()),
            (m1, m2) -> {
            });
  }

  @NonNull
  private static Map<Integer, ImmutableBitSet> filterPriorMatchesAndSort(
      @NonNull final Map<Integer, ImmutableBitSet> matchMatrixAB,
      @NonNull final ImmutableBitSet matchedA,
      @NonNull final ImmutableBitSet matchedB
  ) {
    return matchMatrixAB.entrySet().stream()
        .filter(a -> !matchedA.get(a.getKey()))
        .map(a -> Utils.entry(a.getKey(), a.getValue().andNot(matchedB)))
        .sorted(Comparator.comparing(e -> e.getValue().cardinality()))
        .collect(LinkedHashMap::new,
            (map, e) -> map.put(e.getKey(), e.getValue()),
            (m1, m2) -> {
            });
  }

  private static <T> List<Optional<T>> orderExpected(List<T> expected,
                                                     ImmutableMatchList matchList,
                                                     int actualsSize) {
    final List<Optional<T>> result = new ArrayList<>();
    for (T item : expected) {
      result.add(Optional.of(item));
    }
    for (int i = 0; i < actualsSize; i++) {
      if (!matchList.getExpected(i).isPresent()) {
        result.add(Optional.empty());
      }
    }
    return result;
  }

  private static <T> List<Optional<T>> orderActual(List<T> actual, ImmutableMatchList matchList,
                                                   int expectedSize) {
    final List<Optional<T>> result = new ArrayList<>();
    for (int i = 0; i < expectedSize; i++) {
      Optional<T> opt = matchList.getActual(i).map(actual::get);
      result.add(opt);
    }
    for (int i = 0; i < actual.size(); i++) {
      if (!matchList.actual().contains(i)) {
        result.add(Optional.of(actual.get(i)));
      }
    }
    return result;
  }

  @Override
  public boolean match() {
    for (boolean matched : Utils.paired(expOrdered, actOrdered, Utils::areBothPresent)) {
      if (!matched) {
        return false;
      }
    }
    return true;
  }

  @NonNull
  @Override
  public List<Map.Entry<Optional<Tab.Row>, Optional<Tab.Row>>> getRowPairs() {
    return Utils.paired(expOrdered, actOrdered, Utils::entry);
  }

  private ImmutableMatchList createMatchList() {
    List<Map.Entry<Integer, Tab.Row>> expectedValueRows = new ArrayList<>();
    List<Map.Entry<Integer, Tab.Row>> expectedAssertionRows = new ArrayList<>();
    for (int i = 0; i < expected.size(); i++) {
      Tab.Row r = expected.get(i);
      boolean hasMatcher = r.vals().stream()
          .anyMatch(v -> v instanceof TableUtils.AssertionVal);
      if (hasMatcher) {
        expectedAssertionRows.add(Utils.entry(i, r));
      } else {
        expectedValueRows.add(Utils.entry(i, r));
      }
    }
    List<Map.Entry<Integer, Tab.Row>> allActualRows = IntStream.range(0, actual.size())
        .mapToObj(i -> Utils.entry(i, actual.get(i)))
        .collect(Collectors.toList());

    Comparator<Map.Entry<Integer, Tab.Row>> entryComp = Map.Entry.comparingByValue(rowComparator());
    expectedValueRows.sort(entryComp);
    allActualRows.sort(entryComp);

    ImmutableMatchList valueRowMatches = valueRowMatches(expectedValueRows, allActualRows);
    Set<Integer> matchedActualSet = valueRowMatches.actual();
    List<Map.Entry<Integer, Tab.Row>> unmatchedActualRows = allActualRows.stream()
        .filter(e -> !matchedActualSet.contains(e.getKey()))
        .collect(Collectors.toList());

    ImmutableMatchList assertionRowMatches = assertionRowMatches(expectedAssertionRows, unmatchedActualRows);
    return valueRowMatches.concat(assertionRowMatches);
  }

  @NonNull
  private ImmutableMatchList valueRowMatches(
      @NonNull List<Map.Entry<Integer, Tab.Row>> sortedExpectedValueRows,
      @NonNull List<Map.Entry<Integer, Tab.Row>> sortedAllActualRows
  ) {
    List<Map.Entry<Integer, Tab.Row>> expRows = new ArrayList<>(sortedExpectedValueRows);
    List<Map.Entry<Integer, Tab.Row>> actRows = new ArrayList<>(sortedAllActualRows);
    ImmutableMatchList matchList = ImmutableMatchList.EMPTY;
    Iterator<Map.Entry<Integer, Tab.Row>> expIter = expRows.listIterator();
    while (expIter.hasNext()) {
      Map.Entry<Integer, Tab.Row> exp = expIter.next();
      boolean matchedExp = false;
      Iterator<Map.Entry<Integer, Tab.Row>> actIter = actRows.listIterator();
      while (actIter.hasNext() && !matchedExp) {
        Map.Entry<Integer, Tab.Row> act = actIter.next();
        matchedExp = matchRows(exp.getValue(), act.getValue());
        if (matchedExp) {
          matchList = matchList.add(exp.getKey(), act.getKey());
          expIter.remove();
          actIter.remove();
        }
      }
    }
    return matchList;
  }

  @NonNull
  private ImmutableMatchList assertionRowMatches(
      @NonNull final List<Map.Entry<Integer, Tab.Row>> expectedAssertionRows,
      @NonNull final List<Map.Entry<Integer, Tab.Row>> unmatchedActualRows
  ) {
    int numExpected = expectedAssertionRows.size();
    int numActual = unmatchedActualRows.size();

    if (numExpected != numActual) {
      return ImmutableMatchList.EMPTY;
    }

    Map<Integer, ImmutableBitSet> expectedToMatchingActuals = new LinkedHashMap<>(numExpected);
    for (Map.Entry<Integer, Tab.Row> expEntry : expectedAssertionRows) {
      Tab.Row expectedRow = expEntry.getValue();
      ImmutableBitSet bitSet = actualsMatchingExpected(
          expectedRow, unmatchedActualRows);
      expectedToMatchingActuals.put(expEntry.getKey(), bitSet);
    }
    expectedToMatchingActuals = sortByCardinality(expectedToMatchingActuals);

    Map<Integer, ImmutableBitSet> actualToMatchingExpecteds = new LinkedHashMap<>(numActual);
    for (Map.Entry<Integer, Tab.Row> unmatchedActualRow : unmatchedActualRows) {
      int actualKey = unmatchedActualRow.getKey();
      ImmutableBitSet bitSet = expectedsMatchingActual(actualKey, expectedToMatchingActuals);
      actualToMatchingExpecteds.put(actualKey, bitSet);
    }
    actualToMatchingExpecteds = sortByCardinality(actualToMatchingExpecteds);

    Map<Integer, ImmutableBitSet> withLeastCardinalityFirstElement =
        withLeastCardinalityFirstElement(expectedToMatchingActuals, actualToMatchingExpecteds);
    boolean matchByExpected = withLeastCardinalityFirstElement == expectedToMatchingActuals;

    MatchDetail matchDetail = matchByBitSets(withLeastCardinalityFirstElement,
        ImmutableBitSet.EMPTY, ImmutableBitSet.EMPTY, withLeastCardinalityFirstElement.size(),
        ImmutableMatchList.EMPTY);

    if (!matchDetail.isMatched) {
      return ImmutableMatchList.EMPTY;
    }
    if (matchByExpected) {
      return matchDetail.matchList;
    } else {
      return matchDetail.matchList.swapped();
    }
  }

  @NonNull
  private ImmutableBitSet actualsMatchingExpected(
      @NonNull final Tab.Row expectedRow,
      @NonNull final List<Map.Entry<Integer, Tab.Row>> actualRows
  ) {
    BitSet bitSet = new BitSet(actualRows.size());
    for (Map.Entry<Integer, Tab.Row> actualRow : actualRows) {
      if (matchRows(expectedRow, actualRow.getValue())) {
        bitSet.set(actualRow.getKey());
      }
    }
    return new ImmutableBitSet(bitSet);
  }

  @NonNull
  private MatchDetail matchByBitSets(
      @NonNull final Map<Integer, ImmutableBitSet> matchMatrixAB,
      @NonNull final ImmutableBitSet matchedA,
      @NonNull final ImmutableBitSet matchedB,
      final int targetMatchCount,
      final ImmutableMatchList currentMatchList
  ) {
    Map<Integer, ImmutableBitSet> matchMatrixABFiltered = filterPriorMatchesAndSort(
        matchMatrixAB, matchedA, matchedB);

    Iterator<Map.Entry<Integer, ImmutableBitSet>> iterator = matchMatrixABFiltered.entrySet()
        .iterator();
    if (iterator.hasNext()) {
      Map.Entry<Integer, ImmutableBitSet> entryB = iterator.next();
      ImmutableBitSet matchesB = entryB.getValue();

      int a = entryB.getKey();
      for (int b : matchesB) {
        MatchDetail matchDetail = matchByBitSets(
            matchMatrixABFiltered, matchedA.set(a), matchedB.set(b), targetMatchCount,
            currentMatchList.add(a, b));

        if (matchDetail.isMatched) {
          return matchDetail;
        }
      }
    }
    if (matchedA.cardinality() != targetMatchCount) {
      incrementPermutations();
      return new MatchDetail(false, currentMatchList);
    } else {
      return new MatchDetail(true, currentMatchList);
    }
  }

  private void incrementPermutations() {
    if (permutationCount >= rowPermutationLimit) {
      throw new TooManyPermutationsException(
          "Number of row permutations has exceeded the configured limit of "
              + rowPermutationLimit + ".");
    }
    permutationCount = permutationCount + 1;
  }

  private static class MatchDetail {
    private final boolean isMatched;
    private final ImmutableMatchList matchList;

    private MatchDetail(boolean isMatched, ImmutableMatchList matchList) {
      this.isMatched = isMatched;
      this.matchList = matchList;
    }
  }
}
