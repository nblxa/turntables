package io.github.nblxa.turntables.test.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import io.github.nblxa.turntables.junit.AbstractTestRule;

/**
 * MySQL database.
 *
 * To run tests locally, you may use either a local MySQL installation or Docker:
 *
 * <pre>
 *   # Start the container
 *   docker run --rm -itd --name mysql \
 *          -e MYSQL_ROOT_PASSWORD=tiger \
 *          -e MYSQL_DATABASE=testdb \
 *          -p 3306:3306 mysql:8.0.21
 *   # Follow the logs
 *   docker logs -f mysql
 *   # Stop and remove the container
 *   docker stop mysql
 * </pre>
 *
 */
public class MySqlRule extends AbstractTestRule {
  private final String superUrl;
  private final String url;

  MySqlRule() {
    superUrl = "jdbc:mysql://" + getHost() + ":" + getPort();
    url = superUrl + "/" + getDatabase();
  }

  @Override
  protected void setUp() {
    try (Connection c = getSuperConnection()) {
      execStatement(c, "drop database if exists " + getDatabase());
      execStatement(c, "create database " + getDatabase());
    } catch (SQLException se) {
      throw new UnsupportedOperationException(se);
    }
  }

  @Override
  protected void tearDown() {
    try (Connection c = getSuperConnection()) {
      execStatement(c, "drop database if exists " + getDatabase());
    } catch (SQLException se) {
      throw new UnsupportedOperationException(se);
    }
  }

  Connection getConnection() throws SQLException {
    return DriverManager.getConnection(url, getUser(), getPassword());
  }

  String getJdbcUrl() {
    return url;
  }

  String getHost() {
    return "localhost";
  }

  int getPort() {
    return 3306;
  }

  String getDatabase() {
    return "turntables";
  }

  String getUser() {
    return "root";
  }

  String getPassword() {
    return "tiger";
  }

  private Connection getSuperConnection() throws SQLException {
    return DriverManager.getConnection(superUrl, getUser(), getPassword());
  }

  private static void execStatement(Connection connection, String sql) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.execute();
    }
  }
}
