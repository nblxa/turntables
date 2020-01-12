package io.github.nblxa.turntables.io.feed;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;

public class DefaultProtocol implements FeedProtocol<Object> {
  private static final DefaultProtocol INSTANCE = new DefaultProtocol();

  private DefaultProtocol() {
  }

  @NonNull
  public static DefaultProtocol getInstance() {
    return INSTANCE;
  }

  @Override
  @NonNull
  public ThrowingConsumer<Object> feed(@NonNull String name, @NonNull Tab tab) {
    throw new UnsupportedOperationException("A feed protocol could not be found.");
  }
}
