package io.github.nblxa.turntables.io;

import java.sql.Connection;
import java.util.Map;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.io.settings.DefaultProtocol;
import io.github.nblxa.turntables.io.settings.JdbcProtocol;
import io.github.nblxa.turntables.io.settings.SettingsProtocol;

public class Configuration extends ClassTreeHolder<SettingsProtocol<?>> {
  private static final Configuration INSTANCE = new Configuration();

  private Configuration() {
  }

  public static Configuration getInstance() {
    return INSTANCE;
  }

  @SuppressWarnings("unchecked")
  public <T> SettingsProtocol<T> protocolFor(Class<T> klass) {
    return (SettingsProtocol<T>) getProtocolFor(klass);
  }

  @NonNull
  @Override
  protected ClassTree<SettingsProtocol<?>> defaultProtocols() {
    ClassTree<SettingsProtocol<?>> tree = ClassTree.newInstance(DefaultProtocol.getInstance());
    tree = tree.add(Connection.class, new JdbcProtocol<>());
    return tree;
  }

  @NonNull
  @Override
  protected Map<Class<?>, SettingsProtocol<?>> getProtocolMap(@NonNull IoProtocolProvider provider) {
    return provider.settingsProtocols();
  }
}
