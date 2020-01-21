package io.github.nblxa.turntables.test.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import io.github.nblxa.turntables.Typ;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainerProvider;

public class ITMySql {

  @Rule
  public JdbcDatabaseContainer<?> mysql = new MySQLContainerProvider()
      .newInstance()
      .withDatabaseName("test")
      .withUsername("scott")
      .withPassword("tiger");

  private Connection conn;

  @Before
  public void setUp() throws SQLException {
    conn = DriverManager.getConnection(mysql.getJdbcUrl(), "scott", "tiger");
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
    Tab expected = Turntables.tab()
        .col("a", Typ.INTEGER).col("b", Typ.STRING)
        .row(1, "abc")
        .row(2, "def");

    try (Connection conn = mysql.createConnection("");
         PreparedStatement s = conn.prepareStatement("UPDATE testtab SET a = a / 10")) {
      s.execute();
    }

    try (PreparedStatement ps = conn.prepareStatement("select * from testtab");
         ResultSet rs = ps.executeQuery()) {
      Tab actual = Turntables.from(rs);
      Turntables.assertThat(actual)
          .matches(expected);
    }
  }
}
