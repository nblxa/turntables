package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

final class ImmutableMatchList implements Iterable<Map.Entry<Integer, Integer>> {
  static final ImmutableMatchList EMPTY = new ImmutableMatchList(Collections.emptyList());

  @NonNull
  private final List<Map.Entry<Integer, Integer>> list;

  private ImmutableMatchList(List<Map.Entry<Integer, Integer>> list) {
    this.list = list;
  }

  public static ImmutableMatchList of(int expected, int actual) {
    return new ImmutableMatchList(Collections.singletonList(Utils.entry(expected, actual)));
  }

  @NonNull
  ImmutableMatchList add(int expected, int actual) {
    if (containsAny(expected, actual)) {
      throw new UnsupportedOperationException();
    }
    List<Map.Entry<Integer, Integer>> newList = new ArrayList<>(list.size() + 1);
    newList.add(Utils.entry(expected, actual));
    ListIterator<Map.Entry<Integer, Integer>> li = list.listIterator(list.size());
    while (li.hasPrevious()) {
      newList.add(li.previous());
    }
    return new ImmutableMatchList(newList);
  }

  private boolean containsAny(int expected, int actual) {
    return list.stream()
        .anyMatch(e -> e.getKey().equals(expected) || e.getValue().equals(actual));
  }

  @NonNull
  ImmutableMatchList concat(ImmutableMatchList other) {
    List<Map.Entry<Integer, Integer>> newList = new ArrayList<>(list.size() + other.size());
    ListIterator<Map.Entry<Integer, Integer>> li = other.list.listIterator(other.size());
    while (li.hasPrevious()) {
      newList.add(li.previous());
    }
    li = list.listIterator(list.size());
    while (li.hasPrevious()) {
      newList.add(li.previous());
    }
    return new ImmutableMatchList(newList);
  }

  Optional<Integer> getExpected(int actual) {
    return list.stream()
        .filter(e -> e.getValue().equals(actual))
        .map(Map.Entry::getKey)
        .findAny();
  }

  Optional<Integer> getActual(int expected) {
    return list.stream()
        .filter(e -> e.getKey().equals(expected))
        .map(Map.Entry::getValue)
        .findAny();
  }

  LinkedHashSet<Integer> actual() {
    return list.stream()
        .map(Map.Entry::getValue)
        .collect(LinkedHashSet::new, HashSet::add, (s1, s2) -> new LinkedHashSet<>(s1).addAll(s2));
  }

  int size() {
    return list.size();
  }

  /**
   * New ImmutableMatchList with expected and actual values swapped
   * and the same order of elements.
   */
  @NonNull
  ImmutableMatchList swapped() {
    List<Map.Entry<Integer, Integer>> newList = list.stream()
        .map(e -> Utils.entry(e.getValue(), e.getKey()))
        .collect(Collectors.toList());
    return new ImmutableMatchList(newList);
  }

  @NonNull
  @Override
  public Iterator<Map.Entry<Integer, Integer>> iterator() {
    return list.iterator();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ImmutableMatchList)) {
      return false;
    }
    ImmutableMatchList entries = (ImmutableMatchList) o;
    return list.equals(entries.list);
  }

  @Override
  public int hashCode() {
    return Objects.hash(list);
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder().append('{');
    boolean first = true;
    for (Map.Entry<Integer, Integer> e : list) {
      if (first) {
        first = false;
      } else {
        stringBuilder.append(", ");
      }
      stringBuilder.append(e.getKey())
          .append('=')
          .append(e.getValue());
    }
    return stringBuilder.append('}').toString();
  }
}
