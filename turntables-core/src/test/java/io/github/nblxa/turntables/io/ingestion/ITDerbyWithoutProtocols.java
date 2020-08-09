package io.github.nblxa.turntables.io.ingestion;

import static io.github.nblxa.turntables.assertj.assertj.AssertAssertJ.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.exception.FeedException;
import io.github.nblxa.turntables.junit.TestDataFactory;
import io.github.nblxa.turntables.junit.TestDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

public class ITDerbyWithoutProtocols {
  @ClassRule
  public static final DerbyRule DERBY = new DerbyRule();

  @Rule
  public final TestDataSource DATA = TestDataFactory.jdbc(DERBY::getJdbcUrl, "", "");

  @Before
  public void createTable() throws SQLException {
    String ddl = "CREATE TABLE testtab (a int, b varchar(100), c date)";
    Connection connection = DERBY.getConnection();
    try (PreparedStatement psDdl = connection.prepareStatement(ddl)) {
      psDdl.execute();
    }
    String insert = "INSERT INTO testtab (a, b, c) VALUES (?, ?, ?)";
    try (PreparedStatement psInsert = connection.prepareStatement(insert)) {
      psInsert.setInt(1, 10);
      psInsert.setString(2, "qwerty");
      java.sql.Date date = java.sql.Date.valueOf(LocalDate.of(2019, 1, 9));
      psInsert.setDate(3, date);
      psInsert.execute();
      psInsert.setInt(1, 20);
      psInsert.setString(2, "text");
      psInsert.setDate(3, null);
      psInsert.execute();
    }
  }

  @After
  public void dropTable() throws SQLException {
    String ddl = "DROP TABLE testtab";
    Connection connection = DERBY.getConnection();
    try (PreparedStatement psDdl = connection.prepareStatement(ddl)) {
      psDdl.execute();
    }
  }

  @Test
  public void testFeedUnsupportedJdbcProtocol() {
    Throwable t = catchThrowable(() -> DATA.feed("tab1", Turntables.tab()
        .row(1, "Alice")
        .row(2, "Bob")));
    assertThat(t)
        .isExactlyInstanceOf(FeedException.class)
        .getCause()
        .isExactlyInstanceOf(UnsupportedOperationException.class)
        .hasMessage("Feeding data into a java.sql.Connection of subtype "
            + "org.apache.derby.impl.jdbc.EmbedConnection requires an SPI extension. "
            + "See https://github.com/nblxa/turntables/blob/master/JDBC.md");
  }

  @Test
  public void testIngestResultSetProtocol() {
    Tab actual = DATA.ingest("testtab");

    Turntables.assertThat(actual)
        .matches()
        .row(10, "qwerty", LocalDate.of(2019, 1, 9))
        .row(20, "text", null)
        .asExpected();
  }

  @Test
  public void testFromResultSet() throws Exception {
    Connection c = DERBY.getConnection();
    ResultSet rs = c.prepareStatement("select * from testtab").executeQuery();

    Tab actual = Turntables.from(rs);

    Turntables.assertThat(actual)
        .matches()
        .row(10, "qwerty", LocalDate.of(2019, 1, 9))
        .row(20, "text", null)
        .asExpected();
  }
}
