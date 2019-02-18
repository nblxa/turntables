package io.github.nblxa.fluenttab.assertion;

import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.Utils;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class KeyBasedRowAsserter extends AbstractRowAsserter {
  private Map<List<Tab.Val>, Map.Entry<List<Tab.Row>, List<Tab.Row>>> rowsPerKey;
  private long rowPermutationLimit;

  KeyBasedRowAsserter(@NonNull Iterable<Tab.Row> expected, @NonNull Iterable<Tab.Row> actual,
                      @NonNull Iterable<Tab.Col> expCols, @NonNull Iterable<Tab.Col> actCols,
                      long rowPermutationLimit, ValAsserter valAsserter) {
    super(valAsserter);
    this.rowPermutationLimit = rowPermutationLimit;
    this.rowsPerKey = new HashMap<>();
    List<Integer> expKeyIndexes = keyColIndexes(expCols);
    for (Tab.Row row : expected) {
      List<Tab.Val> kv = sublist(Utils.asList(row.vals()), expKeyIndexes);
      rowsPerKey.compute(kv, (k, e) -> {
        if (e == null) {
          List<Tab.Row> el = new ArrayList<>();
          el.add(row);
          List<Tab.Row> al = new ArrayList<>();
          return new AbstractMap.SimpleImmutableEntry<>(el, al);
        } else {
          e.getKey().add(row);
          return e;
        }
      });
    }
    List<Integer> actKeyIndexes = keyColIndexes(actCols);
    for (Tab.Row row : actual) {
      List<Tab.Val> kv = sublist(Utils.asList(row.vals()), actKeyIndexes);
      rowsPerKey.compute(kv, (k, e) -> {
        if (e == null) {
          List<Tab.Row> el = new ArrayList<>();
          List<Tab.Row> al = new ArrayList<>();
          al.add(row);
          return new AbstractMap.SimpleImmutableEntry<>(el, al);
        } else {
          e.getValue().add(row);
          return e;
        }
      });
    }
  }

  @NonNull
  private static List<Integer> keyColIndexes(@NonNull Iterable<Tab.Col> cols) {
    final List<Tab.Col> colList = Utils.asList(cols);
    return IntStream.range(0, colList.size())
        .filter(i -> colList.get(i).isKey())
        .boxed()
        .collect(Collectors.toList());
  }

  @NonNull
  private static List<Tab.Val> sublist(@NonNull List<Tab.Val> vals,
                                       @NonNull Collection<Integer> indexes) {
    return indexes.stream()
        .sorted()
        .map(vals::get)
        .collect(Collectors.toList());
  }

  @Override
  public boolean match() {
    return rowsPerKey.values()
        .stream()
        .allMatch(e -> matchRowLists(e.getKey(), e.getValue()));
  }

  @NonNull
  @Override
  public Iterable<Map.Entry<Optional<Tab.Row>, Optional<Tab.Row>>> getRowPairs() {
    int initSize = rowsPerKey.size();
    List<Map.Entry<Optional<Tab.Row>, Optional<Tab.Row>>> pairs = new ArrayList<>(initSize);
    rowsPerKey.values().forEach(ea -> {
      final List<Tab.Row> e = ea.getKey();
      final List<Tab.Row> a = ea.getValue();
      if (e.size() <= 1 && a.size() <= 1) {
        Optional<Tab.Row> oe = e.isEmpty() ? Optional.empty() : Optional.of(e.get(0));
        Optional<Tab.Row> oa = a.isEmpty() ? Optional.empty() : Optional.of(a.get(0));
        pairs.add(new AbstractMap.SimpleImmutableEntry<>(oe, oa));
      } else {
        new UnorderedRowAsserter(e, a, rowPermutationLimit, valAsserter).getRowPairs()
            .forEach(pairs::add);
      }
    });
    return pairs;
  }

  private boolean matchRowLists(List<Tab.Row> expRows, List<Tab.Row> actRows) {
    final int expSize = expRows.size();
    final int actSize = actRows.size();
    if (expSize != actSize) {
      return false;
    }
    if (expSize == 1) {
      return matchRows(expRows.get(0), actRows.get(0));
    }
    return new UnorderedRowAsserter(expRows, actRows, rowPermutationLimit, valAsserter).match();
  }
}
