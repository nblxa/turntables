package io.github.nblxa.turntables.io.feed;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.io.ThrowingSupplier;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractJdbcProtocol<T extends Connection> implements FeedProtocol<T> {
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
  protected abstract Map<Typ, String> getSqlTypes();

  protected abstract boolean tableExists(@NonNull Connection connection,
                                         @NonNull String tableName) throws SQLException;

  @SuppressWarnings("UnusedReturnValue")
  protected static <V, T extends Connection> V wrapInTransaction(
      @NonNull T connection,
      @NonNull ThrowingSupplier<V, SQLException> action) throws SQLException {
    connection.setAutoCommit(false);
    try {
      V res = action.get();
      connection.commit();
      return res;
    } catch (Exception e) {
      if (!connection.isClosed()) {
        connection.rollback();
      }
      throw e;
    }
  }

  protected class Feed {
    @NonNull
    protected final Connection connection;
    @NonNull
    protected final String name;
    @NonNull
    protected final Tab tab;
    @NonNull
    protected final List<String> colNames;

    public Feed(@NonNull Connection connection, @NonNull String name, @NonNull Tab tab) {
      this.connection = Objects.requireNonNull(connection, "connection is null");
      this.name = Objects.requireNonNull(name, "unsafeName is null");
      this.tab = Objects.requireNonNull(tab, "tab is null");
      this.colNames = TableUtils.colNames(tab);
    }

    public void feed() throws SQLException {
      wrapInTransaction(connection, () -> {
        if (!tableExists(connection, name)) {
          createTable();
        }
        insertIntoTable();
        return null;
      });
    }

    @SuppressFBWarnings(value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
        justification = "This library is for use in tests only, not productively.")
    protected void createTable() throws SQLException {
      String createSql = buildCreateTableSql();
      try (Statement stmt = connection.createStatement()) {
        stmt.execute(createSql);
      }
    }

    @NonNull
    protected String buildCreateTableSql() {
      StringBuilder sb = new StringBuilder("CREATE TABLE ");
      sb.append(name);
      sb.append(" (");
      boolean first = true;
      int i = 0;
      for (Tab.Col col: tab.cols()) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append(colNames.get(i));
        sb.append(' ');
        sb.append(getSqlType(col.typ()));
        i++;
      }
      sb.append(')');
      return sb.toString();
    }

    @SuppressFBWarnings(value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
        justification = "This library is for use in tests only, not productively.")
    protected void insertIntoTable() throws SQLException {
      String insertSql = buildInsertSql();
      try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
        for (Tab.Row row: tab.rows()) {
          insertRow(stmt, row);
        }
      }
      connection.commit();
    }

    @NonNull
    protected String buildInsertSql() {
      StringBuilder sb = new StringBuilder("INSERT INTO ");
      sb.append(name);
      int numCols = colNames.size();
      if (TableUtils.hasNamedCols(tab)) {
        sb.append(" (");
        for (int i = 0; i < numCols; i++) {
          if (i > 0) {
            sb.append(", ");
          }
          sb.append(colNames.get(i));
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
        justification = "This library is for use in tests only, not productively.")
    protected void insertRow(@NonNull PreparedStatement stmt, @NonNull Tab.Row row)
        throws SQLException {
      int i = 1;
      for (Tab.Val val: row.vals()) {
        setValue(stmt, val.evaluate(), val.typ(), i);
        i++;
      }
      stmt.execute();
    }

    protected void setValue(@NonNull PreparedStatement stmt, @Nullable Object value,
                            @NonNull Typ typ, int jdbcIndex) throws SQLException {
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

  protected class CleanUp {
    @NonNull
    protected final Connection connection;
    @NonNull
    protected final String name;
    @NonNull
    protected final CleanUpAction cleanUpAction;

    public CleanUp(@NonNull Connection connection, @NonNull String name,
                   @NonNull CleanUpAction cleanUpAction) {
      this.connection = Objects.requireNonNull(connection, "connection is null");
      this.name = Objects.requireNonNull(name, "name is null");
      this.cleanUpAction = Objects.requireNonNull(cleanUpAction, "cleanUpAction is null");
    }

    public void cleanUp() throws SQLException {
      wrapInTransaction(connection, () -> {
        switch (cleanUpAction) {
          case DEFAULT:
          case DROP:
            dropTable();
            break;
          case TRUNCATE:
            truncateTable();
            break;
          case DELETE:
            deleteAll();
            break;
          case NONE:
            break;
          default:
            throw new UnsupportedOperationException("not implemented yet");
        }
        return null;
      });
    }

    @SuppressFBWarnings(value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
        justification = "This library is for use in tests only, not productively.")
    protected void dropTable() throws SQLException {
      if (tableExists(connection, name)) {
        String dropSql = "DROP TABLE " + name;
        try (Statement stmt = connection.createStatement()) {
          stmt.execute(dropSql);
        }
      }
    }

    @SuppressFBWarnings(value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
        justification = "This library is for use in tests only, not productively.")
    protected void truncateTable() throws SQLException {
      if (tableExists(connection, name)) {
        String truncateSql = "TRUNCATE TABLE " + name;
        try (Statement stmt = connection.createStatement()) {
          stmt.execute(truncateSql);
        }
      }
    }

    @SuppressFBWarnings(value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
        justification = "This library is for use in tests only, not productively.")
    protected void deleteAll() throws SQLException {
      if (tableExists(connection, name)) {
        String deleteSql = "DELETE FROM " + name;
        try (Statement stmt = connection.createStatement()) {
          stmt.execute(deleteSql);
        }
      }
    }
  }
}
