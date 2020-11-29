package io.github.nblxa.turntables.test.oracle;

import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TestTable;
import io.github.nblxa.turntables.Turntables;

import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.assertj.assertj.AssertAssertJ;
import io.github.nblxa.turntables.exception.FeedException;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import io.github.nblxa.turntables.junit.TestDataSource;
import io.github.nblxa.turntables.junit.TestDataFactory;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.regex.Pattern;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ITOracle {

  @ClassRule
  public static final OracleRule ORACLE = new OracleRule();

  @Rule
  public final TestDataSource testDataSource = TestDataFactory.jdbc(
      ORACLE::getJdbcUrl, ORACLE.getUser(), ORACLE.getPassword());

  @TestTable(name = "employees", cleanUpAction = CleanUpAction.DEFAULT)
  public final Tab employees = Turntables.tab()
      .col("id").col("name").col("dept")
      .row(1, "Alice", "Dev")
      .row(2, "Bob", "Ops");

  @Test
  public void test() throws SQLException {
    // Simulate the application logic
    try (Connection conn = ORACLE.getConnection();
         PreparedStatement s = conn.prepareStatement("update employees set dept = 'QA'")) {
      s.execute();
    }

    Tab actual = testDataSource.ingest("employees");

    Turntables.assertThat(actual)
        .matches()
        .row(1, "Alice", "QA")
        .row(2, "Bob", "QA")
        .asExpected();
  }

  @Test
  public void testMismatch() throws SQLException {
    // Simulate the application logic
    try (Connection conn = ORACLE.getConnection();
         PreparedStatement s = conn.prepareStatement("update employees set dept = 'QA'")) {
      s.execute();
    }

    Tab actual = testDataSource.ingest("employees");

    Throwable t = null;
    // comment next line to check in the IDE:
    t = Assertions.catchThrowable(() -> {
      Turntables.assertThat(actual)
          .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
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
            .append("      dept : QA"));
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
        .isExactlyInstanceOf(FeedException.class)
        .hasCauseInstanceOf(UnsupportedOperationException .class)
        .getCause()
        .hasMessageStartingWith("Data type not supported: boolean");
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
    try (Connection conn = ORACLE.getConnection();
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
    try (Connection conn = ORACLE.getConnection();
         PreparedStatement s = conn.prepareStatement(
             "update famous_people set birth_date = birth_date + interval '1' day")) {
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
    try (Connection conn = ORACLE.getConnection();
         PreparedStatement s = conn.prepareStatement(
             "update app_logs set updated_ts = updated_ts + interval '1' hour, created_ts = created_ts + interval '1' hour")) {
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
    try (Connection conn = ORACLE.getConnection();
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
    try (Connection conn = ORACLE.getConnection();
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
    try (Connection conn = ORACLE.getConnection();
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
    try (Connection conn = ORACLE.getConnection();
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
    testDataSource.feed("employees", Turntables.tab()
        .row(1, "Leo", "Dev")
        .row(2, "William", "Ops"));

    // Simulate the application logic
    try (Connection conn = ORACLE.getConnection();
         PreparedStatement s = conn.prepareStatement("update employees set dept = 'QA'")) {
      s.execute();
    }

    Tab actual = testDataSource.ingest("employees");

    Turntables.assertThat(actual)
        .matches()
        .row(1, "Leo", "QA")
        .row(2, "William", "QA")
        .asExpected();
  }

  @TestTable(name = "table_to_drop", cleanUpAction = CleanUpAction.DROP)
  public final Tab tableToDrop = Turntables.tab().row(42);

  @TestTable(name = "table_to_delete", cleanUpAction = CleanUpAction.DELETE)
  public final Tab tableToDelete = Turntables.tab().row(43);

  @TestTable(name = "table_to_truncate", cleanUpAction = CleanUpAction.TRUNCATE)
  public final Tab tableToTruncate = Turntables.tab().row(44);

  @TestTable(name = "table_cleanup_none", cleanUpAction = CleanUpAction.NONE)
  public final Tab tableCleanUpNone = Turntables.tab().row(45);

  @Test
  public void testDrop1() {
    Tab actual = testDataSource.ingest("table_to_drop");

    Turntables.assertThat(actual)
        .matchesExpected(tableToDrop);
  }

  @Test
  public void testDrop2() {
    Tab actual = testDataSource.ingest("table_to_drop");

    Turntables.assertThat(actual)
        .matchesExpected(tableToDrop);
  }

  @Test
  public void testDelete1() {
    Tab actual = testDataSource.ingest("table_to_delete");

    Turntables.assertThat(actual)
        .matchesExpected(tableToDelete);
  }

  @Test
  public void testDelete2() {
    Tab actual = testDataSource.ingest("table_to_delete");

    Turntables.assertThat(actual)
        .matchesExpected(tableToDelete);
  }

  @Test
  public void testTruncate1() {
    Tab actual = testDataSource.ingest("table_to_truncate");

    Turntables.assertThat(actual)
        .matchesExpected(tableToTruncate);
  }

  @Test
  public void testTruncate2() {
    Tab actual = testDataSource.ingest("table_to_truncate");

    Turntables.assertThat(actual)
        .matchesExpected(tableToTruncate);
  }

  /**
   * This test must be executed together with at least one other.
   */
  @Test
  public void zzzTestNone() {
    Tab actual = testDataSource.ingest("table_cleanup_none");

    Throwable t = catchThrowable(() ->
        Turntables.assertThat(actual)
            .matchesExpected(tableCleanUpNone));
    Assertions.assertThat(t)
        .isInstanceOf(AssertionError.class);
  }
}
