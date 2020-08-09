package io.github.nblxa.turntables.test.oracle;

import io.github.nblxa.turntables.junit.AbstractTestRule;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

/**
 * Oracle database.
 *
 * To run tests locally, you may use either a local Oracle installation or Docker:
 *
 * <pre>
 *   # Start the container
 *   docker run --rm -itd --name oracle \
 *          -p 1521:1521 quillbuilduser/oracle-18-xe
 *   # Follow the logs
 *   docker logs -f oracle
 *   # Stop and remove the container
 *   docker stop oracle
 * </pre>
 *
 */
public class OracleRule extends AbstractTestRule {
  private final String url;

  OracleRule() {
    url = "jdbc:oracle:thin:@//" + getHost() + ":" + getPort() + "/" + getServiceName();
  }

  @Override
  protected void setUp() {
    try (Connection c = getSuperConnection()) {
      execStatement(c, getDropUserSql(getUser()));
      execStatement(c, "create user " + getUser() + " identified by " + getPassword());
      execStatement(c, "grant connect, resource, unlimited tablespace to " + getUser());
    } catch (SQLException se) {
      throw new UnsupportedOperationException(se);
    }
  }

  @Override
  protected void tearDown() {
    try (Connection c = getSuperConnection()) {
      execStatement(c, getDropUserSql(getUser()));
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
    return 1521;
  }

  String getServiceName() {
    return "xepdb1";
  }

  String getSuperUser() {
    return "system";
  }

  String getPassword() {
    return "Oracle18";
  }

  String getUser() {
    return "testdb";
  }

  private Connection getSuperConnection() throws SQLException {
    return DriverManager.getConnection(url, getSuperUser(), getPassword());
  }

  private static void execStatement(Connection connection, String sql) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.execute();
    }
  }

  private static String getDropUserSql(String user) {
    return "" +
        "begin " +
        "  for r in ( " +
        "    select t.username from dba_users t where upper(t.username) = '" + user.toUpperCase(Locale.ENGLISH) + "' " +
        "  ) " +
        "  loop " +
        "    execute immediate 'drop user ' || r.username || ' cascade'; " +
        "  end loop;" +
        "end;";
  }
}
