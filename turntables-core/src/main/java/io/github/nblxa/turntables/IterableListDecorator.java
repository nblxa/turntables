package io.github.nblxa.turntables;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
final class IterableListDecorator<T> extends AbstractList<T> {
  @NonNull
  private final Iterable<T> iterable;
  @Nullable
  private transient List<T> list = null;

  IterableListDecorator(@NonNull Iterable<T> iterable) {
    this.iterable = Objects.requireNonNull(iterable, "iterable is null");
  }

  private List<T> list() {
    // This class is not thread-safe, not synchronized!
    if (list == null) {
      list = Utils.toArrayList(iterable);
    }
    return list;
  }

  @Override
  public int size() {
    return list().size();
  }

  @Override
  public boolean isEmpty() {
    if (list == null) {
      return !iterable.iterator().hasNext();
    } else {
      return list.isEmpty();
    }
  }

  @Override
  public boolean contains(Object o) {
    return list().contains(o);
  }

  @Override
  public Iterator<T> iterator() {
    if (list == null) {
      return iterable.iterator();
    } else {
      return list.iterator();
    }
  }

  @Override
  public Spliterator<T> spliterator() {
    if (list == null) {
      return Spliterators.spliteratorUnknownSize(iterable.iterator(), Spliterator.ORDERED);
    } else {
      return list.spliterator();
    }
  }

  @Override
  public Object[] toArray() {
    return list().toArray();
  }

  @Override
  public <T1> T1[] toArray(T1[] a) {
    return list().toArray(a);
  }

  @Override
  public T get(int index) {
    return list().get(index);
  }

  @Override
  public ListIterator<T> listIterator() {
    return list().listIterator();
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    return list().listIterator(index);
  }
}
