package io.github.nblxa.turntables.io.rowstore;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.io.Feed;
import io.github.nblxa.turntables.io.NameSanitizing;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class JdbcRowStore extends RowStore {
  @FunctionalInterface
  public interface ThrowingSupplier<T, E extends Throwable> {
    @NonNull
    T get() throws E;
  }

  @NonNull
  private final ThrowingSupplier<Connection, Exception> connectionSupplier;
  private volatile Connection connection;
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
    } catch (Exception se) {
      throw new IllegalStateException(se);
    }
  }

  @Override
  @SuppressFBWarnings(value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
      justification = "Input is sanitized by NameSanitizing.")
  public Tab ingest(@NonNull String name) {
    try (Connection conn = getOrReopenConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("select * from " + NameSanitizing.sanitizeName(conn, name))
    ) {
      return Turntables.from(rs);
    } catch (SQLException se) {
      throw new IllegalStateException(se);
    }
  }

  @Override
  public void cleanUp(String name, CleanUpAction cleanUpAction) {
    try (Connection conn = getOrReopenConnection()) {
      Feed.getInstance()
          .protocolFor(conn.getClass(), Connection.class)
          .cleanUp(name, cleanUpAction)
          .accept(conn);
    } catch (Exception se) {
      throw new IllegalStateException(se);
    }
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
