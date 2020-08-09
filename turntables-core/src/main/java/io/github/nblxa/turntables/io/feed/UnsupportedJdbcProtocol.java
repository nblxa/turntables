package io.github.nblxa.turntables.io.feed;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import java.sql.Connection;

public final class UnsupportedJdbcProtocol<T extends Connection> implements FeedProtocol<T> {
  @NonNull
  @Override
  public ThrowingConsumer<T> feed(@NonNull String name, @NonNull Tab tab) {
    return extensionRequired();
  }

  @NonNull
  @Override
  public ThrowingConsumer<T> cleanUp(@NonNull String name, @NonNull CleanUpAction cleanUpAction) {
    return (T c) -> {
    };
  }

  private ThrowingConsumer<T> extensionRequired() {
    return (T connection) -> {
      throw new UnsupportedOperationException("Feeding data into a java.sql.Connection of subtype " +
          connection.getClass().getCanonicalName() + " requires an SPI extension. " +
          "See https://github.com/nblxa/turntables/blob/master/JDBC.md");
    };
  }
}
