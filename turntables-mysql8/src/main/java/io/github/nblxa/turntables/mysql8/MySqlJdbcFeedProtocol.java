package io.github.nblxa.turntables.mysql8;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.io.feed.AbstractJdbcProtocol;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class MySqlJdbcFeedProtocol<T extends Connection> extends AbstractJdbcProtocol<T> {
  static final String[] TABLE_TYPES = new String[]{"TABLE"};
  private static final Map<Typ, String> SQL_TYPES;
  static {
    Map<Typ, String> m = new EnumMap<>(Typ.class);
    m.put(Typ.BOOLEAN, "BOOLEAN");
    m.put(Typ.DATE, "DATE");
    m.put(Typ.DATETIME, "DATETIME(6)");
    m.put(Typ.DECIMAL, "DECIMAL(65,30)");
    m.put(Typ.DOUBLE, "DOUBLE");
    m.put(Typ.INTEGER, "INTEGER");
    m.put(Typ.LONG, "BIGINT");
    m.put(Typ.STRING, "VARCHAR(255)");
    SQL_TYPES = Collections.unmodifiableMap(m);
  }

  @NonNull
  @Override
  public ThrowingConsumer<T> feed(@NonNull String name, @NonNull Tab tab) {
    return conn -> new Feed(conn, name, tab).feed();
  }

  @NonNull
  @Override
  public ThrowingConsumer<T> cleanUp(@NonNull String name, @NonNull CleanUpAction cleanUpAction) {
    return conn -> new CleanUp(conn, name, cleanUpAction).cleanUp();
  }

  @NonNull
  @Override
  protected Map<Typ, String> getSqlTypes() {
    return SQL_TYPES;
  }

  @Override
  protected boolean tableExists(@NonNull Connection connection,
                                @NonNull String tableName) throws SQLException {
    DatabaseMetaData metaData = connection.getMetaData();
    try (ResultSet rs = metaData.getTables(null, null, tableName, TABLE_TYPES)) {
      return rs.next();
    }
  }
}
