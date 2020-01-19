package io.github.nblxa.turntables.io.feed;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;

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
    return DefaultProtocol::noProtocolFound;
  }

  @NonNull
  @Override
  public ThrowingConsumer<Object> cleanUp(@NonNull String name, @NonNull CleanUpAction cleanUpAction) {
    return DefaultProtocol::noProtocolFound;
  }

  private static void noProtocolFound(Object o) {
    throw new UnsupportedOperationException("A feed protocol could not be found for class " +
        o.getClass().getCanonicalName());
  }
}
