package io.github.nblxa.turntables.io.feed;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.io.NameSanitizing;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class JdbcProtocol<T extends Connection> implements FeedProtocol<T> {
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

  protected Map<Typ, String> getSqlTypes() {
    return SQL_TYPES;
  }

  @NonNull
  @Override
  public ThrowingConsumer<T> feed(@NonNull String name, @NonNull Tab tab) {
    return conn -> new JdbcFeed(conn, name, tab).feed();
  }

  @NonNull
  @Override
  public ThrowingConsumer<T> cleanUp(@NonNull String name, @NonNull CleanUpAction cleanUpAction) {
    return conn -> new JdbcCleanUp(conn, name, cleanUpAction).cleanUp();
  }

  protected boolean tableExists(@NonNull Connection connection,
                                @NonNull String tableName) throws SQLException {
    DatabaseMetaData metaData = connection.getMetaData();
    try (ResultSet rs = metaData.getTables(null, null, tableName, TABLE_TYPES)) {
      return rs.next();
    }
  }

  private class JdbcFeed {
    @NonNull
    private final Connection connection;
    @NonNull
    private final String sanitizedName;
    @NonNull
    private final Tab tab;
    @NonNull
    private final List<String> sanitizedColNames;

    private JdbcFeed(@NonNull Connection connection, @NonNull String unsafeName,
                     @NonNull Tab tab) {
      this.connection = Objects.requireNonNull(connection, "connection is null");
      Objects.requireNonNull(unsafeName, "unsafeName is null");
      this.sanitizedName = NameSanitizing.sanitizeName(connection, unsafeName);
      this.tab = Objects.requireNonNull(tab, "tab is null");
      this.sanitizedColNames = TableUtils.colNames(tab)
          .stream()
          .map(n -> NameSanitizing.sanitizeName(connection, n))
          .collect(Collectors.toList());
    }

    private void feed() throws SQLException {
      connection.setAutoCommit(false);
      createOrTruncateTable();
      insertIntoTable();
    }

    private void createOrTruncateTable() throws SQLException {
      if (tableExists(connection, sanitizedName)) {
        truncateTable();
      } else {
        createTable();
      }
    }

    @SuppressFBWarnings(value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
        justification = "Input is sanitized by NameSanitizing.")
    private void createTable() throws SQLException {
      String createSql = buildCreateTableSql();
      try (Statement stmt = connection.createStatement()) {
        stmt.execute(createSql);
      }
    }

    @NonNull
    private String buildCreateTableSql() {
      StringBuilder sb = new StringBuilder("CREATE TABLE ");
      sb.append(sanitizedName);
      sb.append(" (");
      boolean first = true;
      int i = 0;
      for (Tab.Col col: tab.cols()) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append(sanitizedColNames.get(i));
        sb.append(' ');
        sb.append(getSqlType(col.typ()));
        i++;
      }
      sb.append(')');
      return sb.toString();
    }

    @SuppressFBWarnings(value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
        justification = "Input is sanitized by NameSanitizing.")
    private void truncateTable() throws SQLException {
      String truncateSql = "TRUNCATE TABLE " + sanitizedName;
      try (Statement stmt = connection.createStatement()) {
        stmt.execute(truncateSql);
      }
    }

    @SuppressFBWarnings(value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
        justification = "Input is sanitized by NameSanitizing.")
    private void insertIntoTable() throws SQLException {
      String insertSql = buildInsertSql();
      try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
        for (Tab.Row row: tab.rows()) {
          insertRow(stmt, row);
        }
      }
      connection.commit();
    }

    @NonNull
    private String buildInsertSql() {
      StringBuilder sb = new StringBuilder("INSERT INTO ");
      sb.append(sanitizedName);
      int numCols = sanitizedColNames.size();
      if (TableUtils.hasNamedCols(tab)) {
        sb.append(" (");
        for (int i = 0; i < numCols; i++) {
          if (i > 0) {
            sb.append(", ");
          }
          sb.append(sanitizedColNames.get(i));
        }
        sb.append(')');
      }
      sb.append(" VALUES (");
      for (int i = 0; i < numCols; i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append('?');
      }
      sb.append(')');
      return sb.toString();
    }

    @SuppressFBWarnings(value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
        justification = "Input is sanitized by NameSanitizing.")
    private void insertRow(@NonNull PreparedStatement stmt, @NonNull Tab.Row row)
        throws SQLException {
      int i = 1;
      for (Tab.Val val: row.vals()) {
        setValue(stmt, val.evaluate(), val.typ(), i);
        i++;
      }
      stmt.execute();
    }

    protected void setValue(PreparedStatement stmt, Object value, Typ typ, int jdbcIndex)
        throws SQLException {
      if (typ == Typ.INTEGER && value instanceof Integer) {
        stmt.setInt(jdbcIndex, (Integer) value);
        return;
      }
      if (typ == Typ.LONG && value instanceof Long) {
        stmt.setLong(jdbcIndex, (Long) value);
        return;
      }
      if (typ == Typ.DOUBLE && value instanceof Double) {
        stmt.setDouble(jdbcIndex, (Double) value);
        return;
      }
      if (typ == Typ.DECIMAL && value instanceof BigDecimal) {
        stmt.setBigDecimal(jdbcIndex, (BigDecimal) value);
        return;
      }
      if (typ == Typ.STRING && value != null) {
        stmt.setString(jdbcIndex, value.toString());
        return;
      }
      if (typ == Typ.BOOLEAN && value instanceof Boolean) {
        stmt.setBoolean(jdbcIndex, (Boolean) value);
        return;
      }
      if (typ == Typ.DATE) {
        java.sql.Date dt = null;
        if (value instanceof java.sql.Date) {
          dt = (java.sql.Date) value;
        }
        if (value instanceof LocalDate) {
          dt = java.sql.Date.valueOf((LocalDate) value);
        }
        if (dt != null) {
          stmt.setDate(jdbcIndex, dt);
          return;
        }
      }
      if (typ == Typ.DATETIME) {
        java.sql.Timestamp ts = null;
        if (value instanceof java.sql.Timestamp) {
          ts = (java.sql.Timestamp) value;
        }
        if (value instanceof LocalDateTime) {
          ts = java.sql.Timestamp.valueOf((LocalDateTime) value);
        }
        if (ts != null) {
          stmt.setTimestamp(jdbcIndex, ts);
          return;
        }
      }
      stmt.setObject(jdbcIndex, value);
    }

    @NonNull
    private String getSqlType(Typ typ) {
      String sqlType = getSqlTypes().get(typ);
      if (sqlType == null) {
        throw new UnsupportedOperationException("Data type not supported: " + typ);
      }
      return sqlType;
    }
  }

  private class JdbcCleanUp {
    @NonNull
    private final Connection connection;
    @NonNull
    private final String sanitizedName;
    @NonNull
    private final CleanUpAction cleanUpAction;

    public JdbcCleanUp(@NonNull Connection connection, @NonNull String unsafeName,
                       @NonNull CleanUpAction cleanUpAction) {
      this.connection = Objects.requireNonNull(connection, "connection is null");
      Objects.requireNonNull(unsafeName, "unsafeName is null");
      this.sanitizedName = NameSanitizing.sanitizeName(connection, unsafeName);
      this.cleanUpAction = Objects.requireNonNull(cleanUpAction, "cleanUpAction is null");
    }

    private void cleanUp() throws SQLException {
      switch (cleanUpAction) {
        case NONE:
          return;
        case DROP:
          dropTable();
          return;
        default:
          throw new UnsupportedOperationException("not implemented yet");
      }
    }

    @SuppressFBWarnings(value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
        justification = "Input is sanitized by NameSanitizing.")
    private void dropTable() throws SQLException {
      if (tableExists(connection, sanitizedName)) {
        String dropSql = "DROP TABLE " + sanitizedName;
        try (Statement stmt = connection.createStatement()) {
          stmt.execute(dropSql);
        }
      }
    }
  }
}
