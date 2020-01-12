package io.github.nblxa.turntables.io.feed;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Utils;
import io.github.nblxa.turntables.io.NameSanitizing;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

public class JdbcProtocol implements FeedProtocol<Connection> {
  protected final static String[] TABLE_TYPES = new String[]{"TABLE"};

  @Override
  @NonNull
  public ThrowingConsumer<Connection> feed(@NonNull String name, @NonNull Tab tab) {
    JdbcFeedInstance feedInstance = new JdbcFeedInstance(name, tab);
    return feedInstance::feed;
  }

  protected static class JdbcFeedInstance {
    @NonNull
    private Connection connection;
    @NonNull
    private final String name;
    @NonNull
    private final Tab tab;

    public JdbcFeedInstance(@NonNull String name, @NonNull Tab tab) {
      this.name = Objects.requireNonNull(name, "name is null");
      this.tab = Objects.requireNonNull(tab, "tab is null");
    }

    @NonNull
    protected Connection connection() {
      return connection;
    }

    @NonNull
    protected String name() {
      return name;
    }

    @NonNull
    protected Tab tab() {
      return tab;
    }

    public void feed(Connection connection) throws SQLException {
      this.connection = Objects.requireNonNull(connection, "connection is null");
      connection.setAutoCommit(false);
      createOrTruncateTable();
      insertIntoTable();
    }

    protected void createOrTruncateTable() throws SQLException {
      if (tableExists()) {
        truncateTable();
      } else {
        createTable();
      }
    }

    protected boolean tableExists() throws SQLException {
      DatabaseMetaData metaData = connection.getMetaData();
      try (ResultSet rs = metaData.getTables(null, null, name, TABLE_TYPES)) {
        return rs.next();
      }
    }

    protected void createTable() throws SQLException {

    }

    @NonNull
    protected String buildCreateTableSql() throws SQLException {
      StringBuilder sb = new StringBuilder("CREATE TABLE ");
      sb.append(NameSanitizing.sanitizeName(connection, name));
      sb.append(" (");
      boolean first = true;
      for (Tab.Col col: tab.cols()) {
        if (first) {
          first = false;
        } else {
          sb.append(',');
        }

      }
      sb.append(')');
      return sb.toString();
    }

    protected void truncateTable() throws SQLException {
      String truncateSql = "TRUNCATE TABLE " + NameSanitizing.sanitizeName(connection, name);
      try (Statement stmt = connection.createStatement()) {
        stmt.execute(truncateSql);
      }
      connection.commit();
    }

    protected void insertIntoTable()
        throws SQLException {
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
      sb.append(NameSanitizing.sanitizeName(connection, name));
      List<Tab.Col> colsList = Utils.asList(tab.cols());
      int numCols = colsList.size();
      if (allColumnsNamed()) {
        sb.append(" (");
        for (int i = 0; i < numCols; i++) {
          if (i > 0) {
            sb.append(',');
          }
          sb.append(NameSanitizing.sanitizeName(connection, ((Tab.Named) colsList.get(i)).name()));
        }
        sb.append(')');
      }
      sb.append(" VALUES (");
      for (int i = 0; i < numCols; i++) {
        if (i > 0) {
          sb.append(',');
        }
        sb.append('?');
      }
      sb.append(')');
      return sb.toString();
    }

    protected void insertRow(@NonNull PreparedStatement stmt, @NonNull Tab.Row row) throws SQLException {
      int i = 1;
      for (Tab.Val val: row.vals()) {
        stmt.setObject(i, val.eval());
        i++;
      }
      stmt.execute();
    }

    protected final boolean allColumnsNamed() {
      return Utils.stream(tab.cols())
          .allMatch(c -> c instanceof Tab.Named);
    }
  }
}
