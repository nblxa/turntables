package io.github.nblxa.turntables.io.ingestion;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import io.github.nblxa.turntables.junit.TestDataFactory;
import io.github.nblxa.turntables.junit.TestDataSource;
import io.github.nblxa.turntables.junit.TestTable;
import java.time.LocalDate;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

public class ITDerby {
  @ClassRule
  public static final DerbyRule DERBY = new DerbyRule();

  private final TestDataSource testDataSource = new TestDataFactory()
      .jdbc(DERBY::getJdbcUrl, "", "");

  private final TestTable testTab = testDataSource.table("testtab")
      .col("a", Typ.INTEGER)
      .col("b", Typ.STRING)
      .col("c", Typ.DATE)
      .row(10, "qwerty", LocalDate.of(2019, 1, 9))
      .row(20, "text", null)
      .cleanUpAfterTest(CleanUpAction.DROP);

  @Rule
  public TestRule chain = RuleChain
      .outerRule(testDataSource)
      .around(testTab);

  @Test
  public void testDerby() {
    Tab actual = testTab.ingest();
    Turntables.assertThat(actual)
        .matches()
        .row(10, "qwerty", LocalDate.of(2019, 1, 9))
        .row(20, "text", null)
        .asExpected();
  }
}
