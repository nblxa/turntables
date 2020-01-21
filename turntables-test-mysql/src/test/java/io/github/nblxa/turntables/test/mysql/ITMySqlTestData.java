package io.github.nblxa.turntables.test.mysql;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;

import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import io.github.nblxa.turntables.junit.TestDataSource;
import io.github.nblxa.turntables.junit.TestDataFactory;
import io.github.nblxa.turntables.junit.TestTable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainerProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ITMySqlTestData {

  private JdbcDatabaseContainer mysql = new MySQLContainerProvider()
      .newInstance()
      .withDatabaseName("test")
      .withUsername("scott")
      .withPassword("tiger");

  private TestDataSource testDataSource = new TestDataFactory()
      .jdbc(mysql::getJdbcUrl, "scott", "tiger");

  private TestTable testTab = testDataSource.table("testtab")
      .col("a", Typ.INTEGER).col("b", Typ.STRING)
      .row(10, "abc")
      .row(20, "def")
      .cleanupAfterTest(CleanUpAction.DROP);

  @Rule
  public TestRule chain = RuleChain
      .outerRule(mysql)
      .around(testDataSource)
      .around(testTab);

  @Test
  public void test() throws SQLException {
    Tab expected = Turntables.tab()
        .row(1, "abc")
        .row(2, "def");

    // Simulate the application logic
    try (Connection conn = mysql.createConnection("");
         PreparedStatement s = conn.prepareStatement("update testtab set a = a / 10")) {
      s.execute();
    }

    Tab actual = testTab.ingest();
    Turntables.assertThat(actual)
        .matches(expected);
  }

  @Test
  public void testInteger() throws SQLException {
    Tab initialData = Turntables.tab()
        .col("red_id", Typ.INTEGER)
        .col("green_id", Typ.INTEGER)
        .col("blue_id", Typ.INTEGER)
        .row(0, 0, 0)
        .row(40, 140, 25)
        .row(255, 0, 0)
        .row(-1, Integer.MAX_VALUE, Integer.MIN_VALUE);

    testDataSource.feed("colors", initialData);

    // Simulate the application logic
    try (Connection conn = mysql.createConnection("");
         PreparedStatement s = conn.prepareStatement(
             "delete from colors " +
                 "where red_id not between 0 and 255 " +
                 "or green_id not between 0 and 255 " +
                 "or blue_id not between 0 and 255")) {
      s.execute();
    }

    Tab expectedData = Turntables.tab()
        .row(255, 0, 0)
        .row(40, 140, 25)
        .row(0, 0, 0);

    Tab actualData = testDataSource.ingest("colors");

    Turntables.assertThat(actualData)
        .rowMode(Turntables.RowMode.MATCHES_IN_ANY_ORDER)
        .matches(expectedData);
  }
}