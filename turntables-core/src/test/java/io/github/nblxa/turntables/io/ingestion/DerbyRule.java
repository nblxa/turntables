package io.github.nblxa.turntables.io.ingestion;

import io.github.nblxa.turntables.junit.AbstractTestRule;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DerbyRule extends AbstractTestRule {
  private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

  private Connection connection;

  String getJdbcUrl() {
    return "jdbc:derby:memory:testdb";
  }

  @Override
  protected void setUp() {
    try {
      Class.forName(DRIVER).newInstance();
      connection = DriverManager.getConnection(getJdbcUrl() + ";create=true");
    } catch (Exception e) {
      throw new UnsupportedOperationException(e);
    }
  }

  @Override
  protected void tearDown() {
    try {
      connection.close();
      connection = null;
      DriverManager.getConnection(getJdbcUrl() + ";drop=true");
    } catch (SQLException se) {
      if (se.getSQLState().equals("08006")) {
        System.out.println("Derby database dropped.");
      } else {
        throw new UnsupportedOperationException(se);
      }
    }
  }

  Connection getConnection() {
    return connection;
  }
}
