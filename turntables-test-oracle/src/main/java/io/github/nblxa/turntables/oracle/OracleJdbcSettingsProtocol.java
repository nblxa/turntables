package io.github.nblxa.turntables.oracle;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.io.settings.JdbcProtocol;
import oracle.jdbc.OracleConnection;

public class OracleJdbcSettingsProtocol<T extends OracleConnection> extends JdbcProtocol<T> {
  @NonNull
  @Override
  public Settings settings(@NonNull T connection) {
    return super.settings(connection)
        .getBuilder()
        .decimalMode(Settings.DecimalMode.CONVERT)
        .nameMode(Settings.NameMode.CASE_INSENSITIVE)
        .build();
  }
}
