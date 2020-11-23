package io.github.nblxa.turntables;

import java.util.ArrayDeque;
import java.util.Deque;
import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class DequeThreadLocal<T> extends InheritableThreadLocal<Deque<T>> {
  private final T defaultValue = dequeTopValue();

  public abstract T dequeTopValue();

  @Override
  protected Deque<T> initialValue() {
    Deque<T> s = new ArrayDeque<>();
    s.push(defaultValue);
    return s;
  }

  public Transaction putValue(@NonNull T settings) {
    get().push(settings);
    return this::rollbackValue;
  }

  public void rollbackValue() {
    Deque<T> d = get();
    if (!d.isEmpty()) {
      d.pop();
    }
    if (d.isEmpty()) {
      set(initialValue());
    }
  }

  public T getValue() {
    return get().peek();
  }

  public interface Transaction extends AutoCloseable {
    @Override
    void close();
  }
}
