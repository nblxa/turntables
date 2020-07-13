package io.github.nblxa.turntables.test.mysql;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import io.github.nblxa.turntables.Typ;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

public class ITMySql {

  @ClassRule
  public static final MySqlRule MYSQL = new MySqlRule();

  private Connection conn;

  @Before
  public void setUp() throws SQLException {
    conn = DriverManager.getConnection(MYSQL.getJdbcUrl(), MYSQL.getUser(), MYSQL.getPassword());
    conn.setAutoCommit(true);
    try (PreparedStatement ps = conn.prepareStatement(
        "create table testtab (a integer, b varchar(10))")) {
      ps.execute();
    }
    try (PreparedStatement ps = conn.prepareStatement("insert into testtab (a, b) values (?, ?)")) {
      ps.setInt(1, 10);
      ps.setString(2, "abc");
      ps.execute();
      ps.setInt(1, 20);
      ps.setString(2, "def");
      ps.execute();
    }
  }

  @Test
  public void test() throws SQLException {
    try (Connection conn = MYSQL.getConnection();
         PreparedStatement s = conn.prepareStatement("UPDATE testtab SET a = a / 10")) {
      s.execute();
    }

    final Tab actual;
    try (PreparedStatement ps = conn.prepareStatement("select * from testtab");
         ResultSet rs = ps.executeQuery()) {
      actual = Turntables.from(rs);
    }

    Turntables.assertThat(actual)
        .matches()
        .col("a", Typ.INTEGER).col("b", Typ.STRING)
        .row(1, "abc")
        .row(2, "def")
        .asExpected();
  }
}
