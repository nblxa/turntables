package io.github.nblxa.turntables.test.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;

import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.junit.TestData;
import io.github.nblxa.turntables.junit.TestDataFactory;
import io.github.nblxa.turntables.junit.TestTable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainerProvider;

public class ITMySqlTestData {

  private JdbcDatabaseContainer mysql = new MySQLContainerProvider()
      .newInstance()
      .withDatabaseName("test")
      .withUsername("scott")
      .withPassword("tiger");

  private TestData testData = new TestDataFactory()
      .jdbc(() -> mysql.getJdbcUrl(), "scott", "tiger");

  private TestTable testTab = testData.table("testtab")
      .col("a", Typ.INTEGER).col("b", Typ.STRING)
      .row(10, "abc")
      .row(20, "def");

  @Rule
  public TestRule chain = RuleChain
      .outerRule(mysql)
      .around(testData);

  @Test
  public void test() {
    Tab expected = Turntables.tab()
        .row(10, "abc")
        .row(20, "def");
    Tab actual = testTab.injest();
    assertThat(actual).isEqualTo(expected);
  }
}
