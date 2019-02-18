package io.github.nblxa.fluenttab.test.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.FluentTab;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainerProvider;

public class ITMySql {

  @Rule
  public JdbcDatabaseContainer mysql = new MySQLContainerProvider()
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
    try (PreparedStatement ps = conn.prepareStatement("select * from testtab");
         ResultSet rs = ps.executeQuery()) {
      Tab expected = FluentTab.tab()
          .row(10, "abc")
          .row(20, "def");
      Tab actual = FluentTab.from(rs);
      assertThat(actual).isEqualTo(expected);
    }
  }
}
