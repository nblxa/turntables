package io.github.nblxa.turntables.io.ingestion;

import io.github.nblxa.turntables.junit.AbstractTestRule;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DerbyRule extends AbstractTestRule {
  String getJdbcUrl() {
    return "jdbc:derby:memory:testdb";
  }

  @Override
  protected void setUp() {
    try {
      DriverManager.getConnection("jdbc:derby:memory:testdb;create=true");
    } catch (SQLException se) {
      throw new UnsupportedOperationException(se);
    }
  }

  @Override
  protected void tearDown() {
    try {
      DriverManager.getConnection("jdbc:derby:memory:testdb;drop=true");
    } catch (SQLException se) {
      if (se.getSQLState().equals("08006")) {
        System.out.println("Derby database dropped.");
      } else {
        throw new UnsupportedOperationException(se);
      }
    }
  }
}
