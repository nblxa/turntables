package io.github.nblxa.turntables.io.injestion;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ITDerby {
  private Connection connection;

  @Before
  public void setUp() throws Exception {
    String dbUrl = "jdbc:derby:memory:testdb;create=true";
    connection = DriverManager.getConnection(dbUrl);
    String ddl = "CREATE TABLE testtab (a int, b varchar(100), c date)";
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
  public void tearDown() throws SQLException {
    try {
      DriverManager.getConnection("jdbc:derby:memory:testdb;drop=true");
    } catch (SQLException se) {
      if (se.getSQLState().equals("08006")) {
        System.out.println("Derby database dropped.");
      } else {
        throw se;
      }
    }
  }

  @Test
  public void testDerby() throws SQLException {
    Tab expected = Turntables.tab()
        .col("a", Typ.INTEGER)
        .col("b", Typ.STRING)
        .col("c", Typ.DATE)
        .row(10, "qwerty", LocalDate.of(2019, 1, 9))
        .row(20, "text", null);

    try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM testtab");
         ResultSet rs = ps.executeQuery()) {

      Tab actual = Turntables.from(rs);
      assertThat(actual.rows()).hasSize(2);
    }
  }
}
