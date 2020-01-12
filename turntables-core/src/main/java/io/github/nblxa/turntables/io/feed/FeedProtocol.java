package io.github.nblxa.turntables.io.feed;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;

public interface FeedProtocol<U> {
  @FunctionalInterface
  interface ThrowingConsumer<V> {
    void accept(V t) throws Exception;
  }

  @NonNull
  ThrowingConsumer<U> feed(@NonNull String name, @NonNull Tab tab);
}
