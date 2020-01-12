package io.github.nblxa.turntables.test.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;

import io.github.nblxa.turntables.Typ;
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
      .row(20, "def");

  @Rule
  public TestRule chain = RuleChain
      .outerRule(mysql)
      .around(testDataSource)
      .around(testTab);

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

    Tab actual = testTab.injest();
    assertThat(actual)
        .isEqualTo(expected);
  }
}
