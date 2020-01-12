package io.github.nblxa.turntables.io.feed;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.Utils;
import io.github.nblxa.turntables.io.NameSanitizing;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

public class JdbcProtocol implements FeedProtocol<Connection> {
  protected final static String[] TABLE_TYPES = new String[]{"TABLE"};
  private static final Map<Typ, String> SQL_TYPE_MAP;
  static {
    Map<Typ, String> m = new EnumMap<>(Typ.class);
    m.put(Typ.BOOLEAN, "BOOLEAN");
    m.put(Typ.DATE, "DATE");
    m.put(Typ.DATETIME, "DATETIME");
    m.put(Typ.DOUBLE, "DOUBLE");
    m.put(Typ.INTEGER, "INTEGER");
    m.put(Typ.LONG, "LONG");
    m.put(Typ.STRING, "VARCHAR(255)");
    SQL_TYPE_MAP = Collections.unmodifiableMap(m);
  }

  @Override
  @NonNull
  public ThrowingConsumer<Connection> feed(@NonNull String name, @NonNull Tab tab) {
    JdbcFeed feed = new JdbcFeed(name, tab);
    return feed::feed;
  }

  private static class JdbcFeed {
    @NonNull
    private final String unsafeName;
    @NonNull
    private final Tab tab;

    private JdbcFeed(@NonNull String unsafeName, @NonNull Tab tab) {
      this.unsafeName = Objects.requireNonNull(unsafeName, "unsafeName is null");
      this.tab = Objects.requireNonNull(tab, "tab is null");
    }

    public void feed(@NonNull Connection connection) throws SQLException {
      Objects.requireNonNull(connection, "connection is null");
      String sanitizedName = NameSanitizing.sanitizeName(connection, unsafeName);
      Objects.requireNonNull(sanitizedName, "sanitizedName is null");
      JdbcFeedInstance instance = new JdbcFeedInstance(connection, sanitizedName, tab);
      instance.feed();
    }
  }

  private static class JdbcFeedInstance {
    @NonNull
    private final Connection connection;
    @NonNull
    private final String sanitizedName;
    @NonNull
    private final Tab tab;
    @Nullable
    private List<String> sanitizedColNames;

    private JdbcFeedInstance(@NonNull Connection connection, @NonNull String sanitizedName,
                            @NonNull Tab tab) {
      this.connection = connection;
      this.sanitizedName = sanitizedName;
      this.tab = tab;
    }

    private void feed() throws SQLException {
      connection.setAutoCommit(false);
      createOrTruncateTable();
      insertIntoTable();
    }

    private void createOrTruncateTable() throws SQLException {
      if (tableExists()) {
        truncateTable();
      } else {
        createTable();
      }
    }

    private boolean tableExists() throws SQLException {
      DatabaseMetaData metaData = connection.getMetaData();
      try (ResultSet rs = metaData.getTables(null, null, sanitizedName, TABLE_TYPES)) {
        return rs.next();
      }
    }

    private void createTable() throws SQLException {
      String createSql = buildCreateTableSql();
      try (Statement stmt = connection.createStatement()) {
        stmt.execute(createSql);
      }
      connection.commit();
    }

    @NonNull
    private String buildCreateTableSql() {
      StringBuilder sb = new StringBuilder("CREATE TABLE ");
      sb.append(sanitizedName);
      sb.append(" (");
      List<String> sanitizedColNames = getOrCreateSanitizedColNames();
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

    private void truncateTable() throws SQLException {
      String truncateSql = "TRUNCATE TABLE " + sanitizedName;
      try (Statement stmt = connection.createStatement()) {
        stmt.execute(truncateSql);
      }
      connection.commit();
    }

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
      sb.append(" (");
      List<String> sanitizedColNames = getOrCreateSanitizedColNames();
      int numCols = sanitizedColNames.size();
      for (int i = 0; i < numCols; i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append(sanitizedColNames.get(i));
      }
      sb.append(')');
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

    private void insertRow(@NonNull PreparedStatement stmt, @NonNull Tab.Row row)
        throws SQLException {
      int i = 1;
      for (Tab.Val val: row.vals()) {
        stmt.setObject(i, val.eval());
        i++;
      }
      stmt.execute();
    }

    @NonNull
    private List<String> getOrCreateSanitizedColNames() {
      if (sanitizedColNames == null) {
        sanitizedColNames = Utils.stream(tab.cols())
            .map(Tab.Col::name)
            .map(n -> NameSanitizing.sanitizeName(connection, n))
            .collect(Collectors.toList());
      }
      return sanitizedColNames;
    }

    @NonNull
    private String getSqlType(Typ typ) {
      String sqlType = SQL_TYPE_MAP.get(typ);
      if (sqlType == null) {
        throw new NoSuchElementException("SQL type not found for: " + typ);
      }
      return sqlType;
    }
  }
}
