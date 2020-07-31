package io.github.nblxa.turntables.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.io.feed.JdbcProtocol;
import oracle.jdbc.OracleConnection;

public class OracleJdbcFeedProtocol<T extends OracleConnection> extends JdbcProtocol<T> {
  private static final String TABLE_EXISTS_SQL = "SELECT NULL FROM USER_TABLES WHERE TABLE_NAME = ?";

  @Override
  protected Map<Typ, String> getSqlTypes() {
    Map<Typ, String> types = new EnumMap<>(super.getSqlTypes());
    types.put(Typ.DOUBLE, "DOUBLE PRECISION");
    types.put(Typ.STRING, "VARCHAR2(1000)");
    types.remove(Typ.BOOLEAN);
    return Collections.unmodifiableMap(types);
  }

  @Override
  protected boolean tableExists(@NonNull Connection connection,
                                @NonNull String tableName) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(TABLE_EXISTS_SQL)) {
      if (Turntables.getSettings().nameMode == Settings.NameMode.CASE_INSENSITIVE) {
        tableName = tableName.toUpperCase(Locale.ENGLISH);
      }
      ps.setString(1, tableName);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }
}
