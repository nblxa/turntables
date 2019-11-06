package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

final class ImmutableMatchList implements Iterable<Map.Entry<Integer, Integer>> {
  static final ImmutableMatchList EMPTY = new ImmutableMatchList(-1, -1, 0, null);
  private final int expected;
  private final int actual;
  private final int size;
  @Nullable
  private final ImmutableMatchList tail;

  private ImmutableMatchList(int expected, int actual, int size,
                             @Nullable ImmutableMatchList tail) {
    this.expected = expected;
    this.actual = actual;
    this.size = size;
    this.tail = tail;
  }

  public static ImmutableMatchList of(int expected, int actual) {
    return new ImmutableMatchList(expected, actual, 1, EMPTY);
  }

  @NonNull
  ImmutableMatchList add(int expected, int actual) {
    if (containsAny(expected, actual)) {
      throw new UnsupportedOperationException();
    }
    return new ImmutableMatchList(expected, actual, this.size + 1, this);
  }

  private boolean containsAny(int expected, int actual) {
    ImmutableMatchList target = this;
    while (target.tail != null) {
      if (expected == target.expected || actual == target.actual) {
        return true;
      }
      target = target.tail;
    }
    return false;
  }

  @NonNull
  ImmutableMatchList concat(ImmutableMatchList other) {
    ImmutableMatchList result = other;
    ImmutableMatchList target = this;
    while (target.tail != null) {
      result = result.add(target.expected, target.actual);
      target = target.tail;
    }
    return result;
  }

  Optional<Integer> getExpected(int actual) {
    ImmutableMatchList target = this;
    while (target.tail != null) {
      if (target.actual == actual) {
        return Optional.of(target.expected);
      }
      target = target.tail;
    }
    return Optional.empty();
  }

  Optional<Integer> getActual(int expected) {
    ImmutableMatchList target = this;
    while (target.tail != null) {
      if (target.expected == expected) {
        return Optional.of(target.actual);
      }
      target = target.tail;
    }
    return Optional.empty();
  }

  LinkedHashSet<Integer> expected() {
    LinkedHashSet<Integer> set = new LinkedHashSet<>();
    ImmutableMatchList target = this;
    while (target.tail != null) {
      set.add(target.expected);
      target = target.tail;
    }
    return set;
  }

  LinkedHashSet<Integer> actual() {
    LinkedHashSet<Integer> set = new LinkedHashSet<>();
    ImmutableMatchList target = this;
    while (target.tail != null) {
      set.add(target.actual);
      target = target.tail;
    }
    return set;
  }

  int size() {
    return size;
  }

  /**
   * New ImmutableMatchList with elements in the reverse order.
   */
  @NonNull
  ImmutableMatchList reversed() {
    ImmutableMatchList target = this;
    ImmutableMatchList reversed = EMPTY;
    while (target.tail != null) {
      reversed = reversed.add(target.expected, target.actual);
      target = target.tail;
    }
    return reversed;
  }

  /**
   * New ImmutableMatchList with expected and actual values swapped
   * and the same order of elements.
   */
  @NonNull
  ImmutableMatchList swapped() {
    ImmutableMatchList target = this;
    ImmutableMatchList swapped = EMPTY;
    while (target.tail != null) {
      swapped = swapped.add(target.actual, target.expected); // swap here
      target = target.tail;
    }
    return swapped.reversed();
  }

  @NonNull
  @Override
  public Iterator<Map.Entry<Integer, Integer>> iterator() {
    return new ImmutableIterator(this);
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
    return expected == entries.expected &&
        actual == entries.actual &&
        Objects.equals(tail, entries.tail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expected, actual, tail);
  }

  @Override
  public String toString() {
    ImmutableMatchList target = this;
    StringBuilder stringBuilder = new StringBuilder().append('{');
    while (target.tail != null) {
      if (target != this) {
        stringBuilder.append(',').append(' ');
      }
      stringBuilder.append(target.expected).append('=').append(target.actual);
      target = target.tail;
    }
    return stringBuilder.append('}').toString();
  }

  private static class ImmutableIterator implements Iterator<Map.Entry<Integer, Integer>> {
    private ImmutableMatchList target;

    private ImmutableIterator(ImmutableMatchList target) {
      this.target = target;
    }

    @Override
    public boolean hasNext() {
      return target.tail != null;
    }

    /**
     * Traverses in the reverse order of updates.
     */
    @NonNull
    @Override
    public Map.Entry<Integer, Integer> next() {
      if (target == null || target.tail == null) {
        throw new NoSuchElementException();
      }
      target = target.tail;
      return new AbstractMap.SimpleImmutableEntry<>(target.expected, target.actual);
    }
  }
}
