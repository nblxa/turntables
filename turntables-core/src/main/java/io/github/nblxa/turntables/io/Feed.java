package io.github.nblxa.turntables.io;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.io.feed.DefaultProtocol;
import io.github.nblxa.turntables.io.feed.FeedProtocol;
import io.github.nblxa.turntables.io.feed.JdbcProtocol;
import java.sql.Connection;
import java.util.Map;

public class Feed extends ClassTreeHolder<FeedProtocol<?>> {
  private static final Feed INSTANCE = new Feed();

  private Feed() {
  }

  public static Feed getInstance() {
    return INSTANCE;
  }

  @SuppressWarnings("unchecked")
  public <T> FeedProtocol<T> protocolFor(Class<? extends T> protocolClass,
                                         @SuppressWarnings("unused") Class<T> consumerClass) {
    return (FeedProtocol<T>) getProtocolFor(protocolClass);
  }

  @NonNull
  @Override
  protected ClassTree<FeedProtocol<?>> defaultProtocols() {
    ClassTree<FeedProtocol<?>> tree = ClassTree.newInstance(DefaultProtocol.getInstance());
    tree = tree.add(Connection.class, new JdbcProtocol());
    return tree;
  }

  @NonNull
  @Override
  protected Map<Class<?>, FeedProtocol<?>> getProtocolMap(@NonNull IoProtocolProvider provider) {
    return provider.feedProtocols();
  }
}
