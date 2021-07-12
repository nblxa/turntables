package io.github.nblxa.turntables.io.rowstore;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.exception.FeedException;
import io.github.nblxa.turntables.exception.IngestionException;
import io.github.nblxa.turntables.io.Configuration;
import io.github.nblxa.turntables.io.Feed;
import io.github.nblxa.turntables.io.ThrowingSupplier;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class JdbcRowStore implements RowStore {
  protected static final Pattern QUERY = Pattern.compile("^\\s*(with|select)\\b.*",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  @NonNull
  private final ThrowingSupplier<Connection, Exception> connectionSupplier;
  private Connection connection;
  @NonNull
  private final ReentrantLock createConnectionLock = new ReentrantLock(true);
  @NonNull
  private final ReentrantLock reopenConnectionLock = new ReentrantLock(true);

  public JdbcRowStore(@NonNull ThrowingSupplier<Connection, Exception> connectionSupplier) {
    this.connectionSupplier =
        Objects.requireNonNull(connectionSupplier, "connectionSupplier is null");
  }

  @Override
  public void feed(@NonNull String name, @NonNull Tab data) {
    try (Connection conn = getOrReopenConnection()) {
      Feed.getInstance()
          .protocolFor(conn.getClass(), Connection.class)
          .feed(name, data)
          .accept(conn);
    } catch (Exception e) {
      throw new FeedException(e);
    }
  }

  @Override
  @NonNull
  @SuppressFBWarnings(value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
      justification = "This library is for use in tests only, not productively.")
  public Tab ingest(@NonNull String source) {
    try (Connection conn = getOrReopenConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(createQuery(source))
    ) {
      return Turntables.from(rs);
    } catch (SQLException se) {
      throw new IngestionException(se);
    }
  }

  @Override
  public void cleanUp(@NonNull String name, @NonNull CleanUpAction cleanUpAction) {
    try (Connection conn = getOrReopenConnection()) {
      Feed.getInstance()
          .protocolFor(conn.getClass(), Connection.class)
          .cleanUp(name, cleanUpAction)
          .accept(conn);
    } catch (Exception se) {
      throw new FeedException(se);
    }
  }

  @NonNull
  @Override
  @SuppressWarnings("unchecked")
  public Settings defaultSettings() {
    try (Connection conn = getOrReopenConnection()) {
      return Configuration.getInstance()
          .protocolFor((Class<? super Connection>) conn.getClass())
          .settings(conn);
    } catch (Exception se) {
      throw new IllegalStateException(se);
    }
  }

  protected String createQuery(String source) {
    StringBuilder sb = new StringBuilder("select * from ");
    if (QUERY.matcher(source).matches()) {
       sb.append('(').append(source).append(") turntables_subquery__");
    } else {
      sb.append(source);
    }
    return sb.toString();
  }

  @NonNull
  private Connection getOrCreateConnection() {
    if (connection == null) {
      if (createConnectionLock.isHeldByCurrentThread()) {
        throw new UnsupportedOperationException();
      }
      try {
        createConnectionLock.lock();
        Connection conn = connectionSupplier.get();
        Objects.requireNonNull(conn, "connection is null");
        connection = conn;
      } catch (Exception e) {
        throw new IllegalStateException("Connection could not be initialized", e);
      } finally {
        createConnectionLock.unlock();
      }
    }
    return connection;
  }

  private void clearConnection() {
    connection = null;
  }

  @NonNull
  private Connection getOrReopenConnection() throws SQLException {
    reopenConnectionLock.lock();
    try {
      Connection conn = getOrCreateConnection();
      if (conn.isClosed()) {
        clearConnection();
        conn = getOrCreateConnection();
      }
      connection = conn;
      return conn;
    } finally {
      reopenConnectionLock.unlock();
    }
  }
}
