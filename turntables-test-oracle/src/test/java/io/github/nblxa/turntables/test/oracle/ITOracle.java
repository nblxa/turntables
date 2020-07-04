package io.github.nblxa.turntables.test.oracle;

import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;

import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.assertj.assertj.AssertAssertJ;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import io.github.nblxa.turntables.junit.TestDataSource;
import io.github.nblxa.turntables.junit.TestDataFactory;
import io.github.nblxa.turntables.junit.TestTable;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.OracleContainer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ITOracle {

  @ClassRule
  public static JdbcDatabaseContainer<?> ORACLE = new OracleContainer("quillbuilduser/oracle-18-xe")
      .withUsername("system")
      .withPassword("Oracle18");

  private final TestDataSource testDataSource = new TestDataFactory()
      .jdbc(ORACLE::getJdbcUrl, "system", "Oracle18")
      .settings(new Settings.Builder()
          .decimalMode(Settings.DecimalMode.ALLOW_BIG)
          .colNamesMode(Settings.ColNamesMode.CASE_INSENSITIVE)
          .build());

  private final TestTable testTab = testDataSource.table("employees")
      .col("id", Typ.INTEGER)
      .col("name", Typ.STRING)
      .col("dept", Typ.STRING)
      .row(1, "Alice", "Dev")
      .row(2, "Bob", "Ops")
      .cleanUpAfterTest(CleanUpAction.DROP);

  @Rule
  public TestRule chain = RuleChain
      .outerRule(testDataSource)
      .around(testTab);

  @Test
  public void test() throws SQLException {
    // Simulate the application logic
    try (Connection conn = ORACLE.createConnection("");
         PreparedStatement s = conn.prepareStatement("update employees set dept = 'QA'")) {
      s.execute();
    }

    Tab actual = testTab.ingest();

    Turntables.assertThat(actual)
        .rowMode(Turntables.RowMode.MATCHES_IN_ANY_ORDER)
        .settings(new Settings.Builder().decimalMode(Settings.DecimalMode.ALLOW_BIG).build())
        .matches()
        .row(1, "Alice", "QA")
        .row(2, "Bob", "QA")
        .asExpected();
  }

  @Test
  public void testMismatch() throws SQLException {
    // Simulate the application logic
    try (Connection conn = ORACLE.createConnection("");
         PreparedStatement s = conn.prepareStatement("update employees set dept = 'QA'")) {
      s.execute();
    }

    Tab actual = testTab.ingest();

    Throwable t = null;
    // comment next line to check in the IDE:
    t = Assertions.catchThrowable(() -> {
      Turntables.assertThat(actual)
          .colMode(Turntables.ColMode.MATCHES_IN_GIVEN_ORDER)
          .rowMode(Turntables.RowMode.MATCHES_IN_GIVEN_ORDER)
          .matches()
          .col("id", Typ.INTEGER)
          .col("name", Typ.STRING)
          .col("dept", Typ.STRING)
          .row(1, "Hugh", "QA")
          .row(2, "Mary", "QA")
          .asExpected();
      // comment next line to check in the IDE:
    });

    // Assert that column names from the actual tab are also used in expected tab's representation
    // so that there is no diff in column names when we are matching by position and names
    // are irrelevant.
    String ls = System.lineSeparator();
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(ls)
            .append("EXPECTED: Table:").append(ls)
            .append("    - id   : 1").append(ls)
            .append("      name : Hugh").append(ls)
            .append("      dept : QA").append(ls)
            .append("    - id   : 2").append(ls)
            .append("      name : Mary").append(ls)
            .append("      dept : QA").append(ls)
            .append("BUT: WAS Table:").append(ls)
            .append("    - id   : 1").append(ls)
            .append("      name : Alice").append(ls)
            .append("      dept : QA").append(ls)
            .append("    - id   : 2").append(ls)
            .append("      name : Bob").append(ls)
            .append("      dept : QA").append(ls).toString());
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
    try (Connection conn = ORACLE.createConnection("");
         PreparedStatement s = conn.prepareStatement(
             "delete from colors " +
                 "where red_id not between 0 and 255 " +
                 "or green_id not between 0 and 255 " +
                 "or blue_id not between 0 and 255")) {
      s.execute();
    }

    Tab actualData = testDataSource.ingest("colors");

    Turntables.assertThat(actualData)
        .rowMode(Turntables.RowMode.MATCHES_IN_ANY_ORDER)
        .matches()
        .row(255, 0, 0)
        .row(40, 140, 25)
        .row(0, 0, 0)
        .asExpected();
  }

  @Test
  public void testString() throws SQLException {
    Tab initialData = Turntables.tab()
        .col("first_name", Typ.STRING)
        .col("last_name", Typ.STRING)
        .row("Leo", "Tolstoy")
        .row("William", "Shakespeare");

    testDataSource.feed("writers", initialData);

    // Simulate the application logic
    try (Connection conn = ORACLE.createConnection("");
         PreparedStatement s = conn.prepareStatement(
             "insert into writers (first_name, last_name) values ('Alexandre', 'Dumas')")) {
      s.execute();
    }

    Tab actualData = testDataSource.ingest("writers");

    Turntables.assertThat(actualData)
        .rowMode(Turntables.RowMode.MATCHES_IN_ANY_ORDER)
        .matches()
        .row("Leo", "Tolstoy")
        .row("William", "Shakespeare")
        .row("Alexandre", "Dumas")
        .asExpected();
  }

  @Test
  public void testDouble() throws SQLException {
    Tab initialData = Turntables.tab()
        .col("constant", Typ.STRING)
        .col("value", Typ.DOUBLE)
        .row("Pi", 3.1415926d)
        .row("e", 2.71828182846d);

    testDataSource.feed("constants", initialData);

    // Simulate the application logic
    try (Connection conn = ORACLE.createConnection("");
         PreparedStatement s = conn.prepareStatement(
             "update constants set value = value * 2")) {
      s.execute();
    }

    Tab actualData = testDataSource.ingest("constants");

    Turntables.assertThat(actualData)
        .rowMode(Turntables.RowMode.MATCHES_IN_ANY_ORDER)
        .matches()
        .row("Pi", 3.1415926d * 2)
        .row("e", 2.71828182846d * 2)
        .asExpected();
  }

  @Test
  public void testBoolean() {
    Tab initialData = Turntables.tab()
        .col("constant", Typ.STRING)
        .col("value", Typ.BOOLEAN)
        .row("false", false)
        .row("true", true)
        .row("null", Turntables.nul());

    Throwable t = Assertions.catchThrowable(() -> testDataSource.feed("constants", initialData));
    Assertions.assertThat(t)
        .isExactlyInstanceOf(IllegalStateException.class)
        .hasCauseInstanceOf(UnsupportedOperationException .class)
        .getCause()
        .hasMessageStartingWith("Data type not supported: boolean");
  }

  @Test
  public void testReFeed() throws SQLException {
    testDataSource.feed(testTab.getName(), Turntables.tab()
        .row(1, "Leo", "Dev")
        .row(2, "William", "Ops"));

    // Simulate the application logic
    try (Connection conn = ORACLE.createConnection("");
         PreparedStatement s = conn.prepareStatement("update employees set dept = 'QA'")) {
      s.execute();
    }

    Tab actual = testTab.ingest();

    Turntables.assertThat(actual)
        .rowMode(Turntables.RowMode.MATCHES_IN_ANY_ORDER)
        .matches()
        .row(1, "Leo", "QA")
        .row(2, "William", "QA")
        .asExpected();
  }
}
