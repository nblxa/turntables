package io.github.nblxa.turntables.oracle18;

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
import io.github.nblxa.turntables.io.feed.AbstractJdbcProtocol;
import oracle.jdbc.OracleConnection;

public class OracleJdbcFeedProtocol<T extends OracleConnection> extends AbstractJdbcProtocol<T> {
  private static final String TABLE_EXISTS_SQL = "SELECT NULL FROM USER_TABLES WHERE TABLE_NAME = ?";
  private static final Map<Typ, String> SQL_TYPES;
  static {
    Map<Typ, String> m = new EnumMap<>(Typ.class);
    m.put(Typ.DATE, "DATE");
    m.put(Typ.DATETIME, "TIMESTAMP(9)");
    m.put(Typ.DECIMAL, "NUMBER");
    m.put(Typ.DOUBLE, "DOUBLE PRECISION");
    m.put(Typ.INTEGER, "INTEGER");
    m.put(Typ.LONG, "NUMBER");
    m.put(Typ.STRING, "VARCHAR2(1000)");
    SQL_TYPES = Collections.unmodifiableMap(m);
  }

  @NonNull
  @Override
  protected Map<Typ, String> getSqlTypes() {
    return SQL_TYPES;
  }

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
