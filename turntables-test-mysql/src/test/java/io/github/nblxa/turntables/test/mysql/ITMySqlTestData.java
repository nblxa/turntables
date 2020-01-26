package io.github.nblxa.turntables.test.mysql;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;

import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import io.github.nblxa.turntables.junit.TestDataSource;
import io.github.nblxa.turntables.junit.TestDataFactory;
import io.github.nblxa.turntables.junit.TestTable;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainerProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static io.github.nblxa.turntables.Turntables.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ITMySqlTestData {

  private JdbcDatabaseContainer<?> mysql = new MySQLContainerProvider()
      .newInstance()
      .withDatabaseName("test")
      .withUsername("scott")
      .withPassword("tiger");

  private TestDataSource testDataSource = new TestDataFactory()
      .jdbc(mysql::getJdbcUrl, "scott", "tiger");

  private TestTable testTab = testDataSource.table("employees")
      .col("id", Typ.INTEGER).col("name", Typ.STRING).col("dept", Typ.STRING)
      .row(1, "Alice", "Dev")
      .row(2, "Bob", "Ops")
      .cleanupAfterTest(CleanUpAction.DROP);

  @Rule
  public TestRule chain = RuleChain
      .outerRule(mysql)
      .around(testDataSource)
      .around(testTab);

  @Test
  public void test() throws SQLException {
    // Simulate the application logic
    try (Connection conn = mysql.createConnection("");
         PreparedStatement s = conn.prepareStatement("update employees set dept = 'QA'")) {
      s.execute();
    }

    Tab actual = testTab.ingest();

    Tab expected = Turntables.tab()
        .row(1, "Alice", "QA")
        .row(2, "Bob", "QA");
    Turntables.assertThat(actual)
        .matches(expected);
  }


  @Test
  public void testMismatch() throws SQLException {
    // Simulate the application logic
    try (Connection conn = mysql.createConnection("");
         PreparedStatement s = conn.prepareStatement("update employees set dept = 'QA'")) {
      s.execute();
    }

    Tab actual = testTab.ingest();

    Tab expected = Turntables.tab()
        .row(1, "Hugh", "QA")
        .row(2, "Mary", "QA");

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      assertThat(actual)
          .colMode(Turntables.ColMode.MATCHES_IN_GIVEN_ORDER)
          .rowMode(Turntables.RowMode.MATCHES_IN_GIVEN_ORDER)
          .matches(expected);
      // comment next line to check in the IDE:
    });

    // Assert that column names from the actual tab are also used in expected tab's representation
    // so that there is no diff in column names when we are matching by position and names
    // are irrelevant.
    String ls = System.lineSeparator();
    Assertions.assertThat(t)
        .isInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(ls)
            .append("Expected: <\"Table:").append(ls)
            .append("    - id   : 1").append(ls)
            .append("      name : Hugh").append(ls)
            .append("      dept : QA").append(ls)
            .append("    - id   : 2").append(ls)
            .append("      name : Mary").append(ls)
            .append("      dept : QA\">").append(ls)
            .append("but was: <\"Table:").append(ls)
            .append("    - id   : 1").append(ls)
            .append("      name : Alice").append(ls)
            .append("      dept : QA").append(ls)
            .append("    - id   : 2").append(ls)
            .append("      name : Bob").append(ls)
            .append("      dept : QA\">").append(ls).toString());
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