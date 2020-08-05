package io.github.nblxa.turntables.test.mysql;

import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.assertj.assertj.AssertAssertJ;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import io.github.nblxa.turntables.junit.TestDataSource;
import io.github.nblxa.turntables.junit.TestDataFactory;
import io.github.nblxa.turntables.junit.TestTable;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.catchThrowable;

public class ITMySql {
  @ClassRule
  public static final MySqlRule MYSQL = new MySqlRule();

  private final TestDataSource testDataSource = new TestDataFactory()
      .jdbc(MYSQL::getJdbcUrl, MYSQL.getUser(), MYSQL.getPassword());

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
    try (Connection conn = MYSQL.getConnection();
         PreparedStatement s = conn.prepareStatement("update employees set dept = 'QA'")) {
      s.execute();
    }

    Tab actual = testTab.ingest();

    Turntables.assertThat(actual)
        .matches()
        .row(1, "Alice", "QA")
        .row(2, "Bob", "QA")
        .asExpected();
  }

  @Test
  public void testMismatch() throws SQLException {
    // Simulate the application logic
    try (Connection conn = MYSQL.getConnection();
         PreparedStatement s = conn.prepareStatement("update employees set dept = 'QA'")) {
      s.execute();
    }

    Tab actual = testTab.ingest();

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      Turntables.assertThat(actual)
          .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
          .matches()
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
  public void testBoolean() throws SQLException {
    Tab initialData = Turntables.tab()
        .col("constant", Typ.STRING)
        .col("value", Typ.BOOLEAN)
        .row("false", false)
        .row("true", true)
        .row("null", Turntables.nul());

    testDataSource.feed("constants", initialData);

    // Simulate the application logic
    try (Connection conn = MYSQL.getConnection();
         PreparedStatement s = conn.prepareStatement(
             "update constants set value = not value")) {
      s.execute();
    }

    Tab actualData = testDataSource.ingest("constants");

    Turntables.assertThat(actualData)
        .matches()
        .row("null", Turntables.nul())
        .row("true", Turntables.test((Boolean b) -> !b))
        .row("false", (BooleanSupplier) () -> true)
        .asExpected();
  }

  @Test
  public void testDouble() throws SQLException {
    Tab initialData = Turntables.tab()
        .col("constant", Typ.STRING)
        .col("value", Typ.DOUBLE)
        .row("Pi", 3.1415926d)
        .row("e", 2.71828182846d)
        .row("one", 1.0);

    testDataSource.feed("constants", initialData);

    // Simulate the application logic
    try (Connection conn = MYSQL.getConnection();
         PreparedStatement s = conn.prepareStatement(
             "update constants set value = value * 2")) {
      s.execute();
    }

    Tab actualData = testDataSource.ingest("constants");

    Turntables.assertThat(actualData)
        .matches()
        .row("one", Turntables.testDouble(d -> d == 2.0))
        .row("e", (DoubleSupplier) () -> 2.71828182846d * 2)
        .row("Pi", 3.1415926d * 2)
        .asExpected();
  }

  @Test
  public void testDate() throws SQLException {
    Tab initialData = Turntables.tab()
        .col("person", Typ.STRING)
        .col("birth_date", Typ.DATE)
        .row("Galileo Galilei", LocalDate.of(1564, 2, 15))
        .row("Leonardo da Vinci", new java.util.Date(java.sql.Date.valueOf(LocalDate.of(1452, 4, 15)).getTime()))
        .row("Isaac Newton", java.sql.Date.valueOf(LocalDate.of(1642, 12, 25)));

    testDataSource.feed("famous_people", initialData);

    // Simulate the application logic
    try (Connection conn = MYSQL.getConnection();
         PreparedStatement s = conn.prepareStatement(
             "update famous_people set birth_date = date_add(birth_date, interval 1 day)")) {
      s.execute();
    }

    Tab actualData = testDataSource.ingest("famous_people");

    Turntables.assertThat(actualData)
        .matches()
        .row("Isaac Newton", LocalDate.of(1642, 12, 26))
        .row("Leonardo da Vinci", LocalDate.of(1452, 4, 16))
        .row("Galileo Galilei", Turntables.test((LocalDate ld) -> LocalDate.of(1564, 2, 16).equals(ld)))
        .asExpected();
  }

  @Test
  public void testDateTime() throws SQLException {
    Tab initialData = Turntables.tab()
        .col("record_id", Typ.INTEGER)
        .col("updated_ts", Typ.DATETIME)
        .col("created_ts", Typ.DATETIME)
        .row(1, LocalDateTime.of(2020, 8, 2, 10, 11, 15, 45_000),
            LocalDateTime.of(2020, 8, 2, 10, 11, 17, 999_999_000))
        .row(2, java.sql.Timestamp.valueOf(LocalDateTime.of(2020, 8, 2, 10, 12, 15, 45_000)),
            java.sql.Timestamp.valueOf(LocalDateTime.of(2020, 8, 2, 10, 12, 15, 245_000)))
        .row(3, LocalDateTime.of(2020, 8, 2, 15, 48, 9, 1_000), null);

    testDataSource.feed("app_logs", initialData);

    // Simulate the application logic
    try (Connection conn = MYSQL.getConnection();
         PreparedStatement s = conn.prepareStatement(
             "update app_logs set updated_ts = date_add(updated_ts, interval 1 hour), created_ts = date_add(created_ts, interval 1 hour)")) {
      s.execute();
    }

    Tab actualData = testDataSource.ingest("app_logs");

    Turntables.assertThat(actualData)
        .matches()
        .key(Typ.INTEGER)
        .col(Typ.DATETIME)
        .col(Typ.DATETIME)
        .row(2, LocalDateTime.of(2020, 8, 2, 11, 12, 15, 45_000),
            LocalDateTime.of(2020, 8, 2, 11, 12, 15, 245_000))
        .row(3, LocalDateTime.of(2020, 8, 2, 16, 48, 9, 1_000), null)
        .row(1, LocalDateTime.of(2020, 8, 2, 11, 11, 15, 45_000),
            LocalDateTime.of(2020, 8, 2, 11, 11, 17, 999_999_000))
        .asExpected();
  }

  @Test
  public void testDecimal() throws SQLException {
    Tab initialData = Turntables.tab()
        .col("value", Typ.DECIMAL)
        .row(BigDecimal.valueOf(Long.MAX_VALUE))
        .row(BigDecimal.valueOf(.00000_00000_00000_00000_00000_00001))
        .row(null);

    testDataSource.feed("decimal_values", initialData);

    // Simulate the application logic
    try (Connection conn = MYSQL.getConnection();
         PreparedStatement s = conn.prepareStatement(
             "update decimal_values set value = value + 1")) {
      s.execute();
    }

    Tab actualData = testDataSource.ingest("decimal_values");

    Turntables.assertThat(actualData)
        .matches()
        .row(BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE))
        .row(BigDecimal.valueOf(.00000_00000_00000_00000_00000_00001).add(BigDecimal.ONE))
        .row(null)
        .asExpected();
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
    try (Connection conn = MYSQL.getConnection();
         PreparedStatement s = conn.prepareStatement(
             "delete from colors " +
                 "where red_id not between 0 and 255 " +
                 "or green_id not between 0 and 255 " +
                 "or blue_id not between 0 and 255")) {
      s.execute();
    }

    Tab actualData = testDataSource.ingest("colors");

    Turntables.assertThat(actualData)
        .matches()
        .row(255, 0, 0)
        .row(40, 140, (IntSupplier) () -> 25)
        .row(0, 0, Turntables.testInt(i -> i == 0))
        .asExpected();
  }

  @Test
  public void testLong() throws SQLException {
    Tab initialData = Turntables.tab()
        .col("long_value", Typ.LONG)
        .row(0L)
        .row(Long.MIN_VALUE)
        .row(Long.MAX_VALUE)
        .row(null);

    testDataSource.feed("long_values", initialData);

    // Simulate the application logic
    try (Connection conn = MYSQL.getConnection();
         PreparedStatement s = conn.prepareStatement(
             "update long_values " +
                 "set long_value = 1 " +
                 "where long_value = 0")) {
      s.execute();
    }

    Tab actualData = testDataSource.ingest("long_values");

    Turntables.assertThat(actualData)
        .matches()
        .row(Turntables.testLong(l -> l == 1L))
        .row(Long.MIN_VALUE)
        .row((LongSupplier) () -> Long.MAX_VALUE)
        .row(null)
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
    try (Connection conn = MYSQL.getConnection();
         PreparedStatement s = conn.prepareStatement(
             "insert into writers (first_name, last_name) values ('Alexandre', 'Dumas')")) {
      s.execute();
    }

    Tab actualData = testDataSource.ingest("writers");

    Turntables.assertThat(actualData)
        .matches()
        .row("Leo", "Tolstoy")
        .row("William", "Shakespeare")
        .row("Alexandre", Pattern.compile("DUMAS", Pattern.CASE_INSENSITIVE))
        .asExpected();
  }

  @Test
  public void testReFeed() throws SQLException {
    testDataSource.feed(testTab.getName(), Turntables.tab()
        .row(1, "Leo", "Dev")
        .row(2, "William", "Ops"));

    // Simulate the application logic
    try (Connection conn = MYSQL.getConnection();
         PreparedStatement s = conn.prepareStatement("update employees set dept = 'QA'")) {
      s.execute();
    }

    Tab actual = testTab.ingest();

    Turntables.assertThat(actual)
        .matches()
        .row(1, "Leo", "QA")
        .row(2, "William", "QA")
        .asExpected();
  }
}
