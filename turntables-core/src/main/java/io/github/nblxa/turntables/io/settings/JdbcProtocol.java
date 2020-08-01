package io.github.nblxa.turntables.io.settings;

import java.sql.Connection;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Settings;

public class JdbcProtocol<T extends Connection> implements SettingsProtocol<T> {
  @NonNull
  @Override
  public Settings settings(@NonNull T connection) {
    return Settings.builder()
        .rowMode(Settings.RowMode.MATCH_IN_ANY_ORDER)
        .build();
  }
}
