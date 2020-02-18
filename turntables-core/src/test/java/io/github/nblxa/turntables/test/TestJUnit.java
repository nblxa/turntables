package io.github.nblxa.turntables.test;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import io.github.nblxa.turntables.io.rowstore.RowStore;
import io.github.nblxa.turntables.junit.AbstractTestTable;
import io.github.nblxa.turntables.junit.FluentTestTable;
import io.github.nblxa.turntables.junit.NamedColTestTable;
import io.github.nblxa.turntables.junit.TestDataSource;
import io.github.nblxa.turntables.junit.TestTable;
import io.github.nblxa.turntables.junit.UnnamedColTestTable;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.HashMap;
import java.util.Map;

import static io.github.nblxa.turntables.Turntables.assertThat;

public class TestJUnit {
  public static class MockRowStore implements RowStore {
    private final Map<String, Tab> tabs = new HashMap<>();

    @Override
    public void feed(String name, Tab tab) {
      tabs.put(name, tab);
    }

    @Override
    public Tab ingest(String name) {
      return tabs.get(name);
    }

    @Override
    public void cleanUp(String name, CleanUpAction cleanUpAction) {
      if (cleanUpAction == CleanUpAction.DELETE) {
        throw new UnsupportedOperationException("simulated error during clean-up");
      }
    }
  }

  private static RowStore mockRowStore = new MockRowStore();

  private TestDataSource source = new TestDataSource(mockRowStore);
  private TestTable t0 = source.table("t0");
  private TestTable t1 = source.table("t1").col(Typ.INTEGER);
  private TestTable t2 = source.table("t2").col("x", Typ.INTEGER);
  private TestTable t3 = source.table("t3").key(Typ.INTEGER);
  private TestTable t4 = source.table("t4").col("x", Typ.INTEGER);
  private TestTable t5 = source.table("t5").col(Typ.DATE).key(Typ.DATE);
  private TestTable t6 = source.table("t6").col("x", Typ.DATE).key("y", Typ.DATE);
  private AbstractTestTable<?, ?> t7 = source.table("t7")
      .col(Typ.INTEGER)
      .row(1)
      .cleanUpAfterTest(CleanUpAction.TRUNCATE);
  private UnnamedColTestTable t8 = source.table("t8").col(Typ.INTEGER);
  private NamedColTestTable t9 = source.table("t9").col("z", Typ.INTEGER);
  private FluentTestTable t10 = source.table("t10");

  private TestDataSource source2 = new TestDataSource(mockRowStore);
  private FluentTestTable t11 = source2.table("t11");

  @Rule
  public TestRule chain = RuleChain
      .outerRule(source)
      .around(t0)
      .around(t1)
      .around(t2)
      .around(t3)
      .around(t4)
      .around(t5)
      .around(t6)
      .around(t7)
      .around(t8)
      .around(t9)
      .around(t10);

  @Rule
  public TestRule chain2 = RuleChain
      .outerRule(source2)
      .around(t11);

  @Test
  public void test0_ingest() {
    Tab tab = t0.ingest();

    assertThat(tab)
        .matches()
        .asExpected();
  }

  @Test
  public void test1_ingest() {
    Tab tab = t1.ingest();

    assertThat(tab)
        .matches()
        .col(Typ.INTEGER)
        .asExpected();
  }

  @Test
  public void test2_ingest() {
    Tab tab = t2.ingest();

    assertThat(tab)
        .matches()
        .col("x", Typ.INTEGER)
        .asExpected();
  }

  @Test
  public void test3_ingest() {
    Tab tab = t3.ingest();

    assertThat(tab)
        .matches()
        .key(Typ.INTEGER)
        .asExpected();
  }

  @Test
  public void test4_ingest() {
    Tab tab = t4.ingest();

    assertThat(tab)
        .matches()
        .key("y", Typ.INTEGER)
        .asExpected();
  }

  @Test
  public void test5_ingest() {
    Tab tab = t5.ingest();

    assertThat(tab)
        .matches()
        .col(Typ.DATE)
        .key(Typ.DATE)
        .asExpected();
  }

  @Test
  public void test6_ingest() {
    Tab tab = t6.ingest();

    assertThat(tab)
        .matches()
        .col("x", Typ.DATE)
        .key("y", Typ.DATE)
        .asExpected();
  }

  @Test
  public void test7_ingest() {
    Tab tab = t7.ingest();

    assertThat(tab)
        .matches()
        .col(Typ.INTEGER)
        .row(1)
        .asExpected();
  }

  @Test
  public void test1_mismatch() {
    Tab tab = t1.ingest();

    Throwable t = Assertions.catchThrowable(() ->
      assertThat(tab)
          .matches()
          .col(Typ.DATE)
          .asExpected());
    Assertions.assertThat(t)
        .isExactlyInstanceOf(AssertionError.class);
  }

  @Test
  public void test7_mismatch() {
    Tab tab = t7.ingest();

    Throwable t = Assertions.catchThrowable(() ->
      assertThat(tab)
          .matches()
          .col(Typ.INTEGER)
          .row(2)
          .asExpected());
    Assertions.assertThat(t)
        .isExactlyInstanceOf(AssertionError.class);
  }

  @Test
  public void test7_tab() {
    Tab tab = t7.tab();

    assertThat(tab)
        .matches()
        .col(Typ.INTEGER)
        .row(1)
        .asExpected();
  }

  @Test
  public void test7_cleanUpAction() {
    CleanUpAction cleanUpAction = t7.getCleanUpAction();

    Assertions.assertThat(cleanUpAction)
        .isEqualTo(CleanUpAction.TRUNCATE);
  }

  @Test
  public void test8_rowAdder_unnamed() {
    UnnamedColTestTable rowAdder = t8.rowAdder().row(42);

    assertThat(rowAdder.tab())
        .matches()
        .row(42)
        .asExpected();
  }

  @Test
  public void test9_rowAdder_named() {
    NamedColTestTable rowAdder = t9.rowAdder().row(42);

    assertThat(rowAdder.tab())
        .matches()
        .row(42)
        .asExpected();
  }

  @Test
  public void test10_rowAdder_fluent() {
    FluentTestTable rowAdder = t10.rowAdder().row(42);

    assertThat(rowAdder.tab())
        .matches()
        .row(42)
        .asExpected();
  }

  @Test
  public void test11_tableAlreadyDefined() {
    Throwable t = Assertions.catchThrowable(() ->
        source2.table("t11")
            .apply(new Fail(new NullPointerException()), Description.EMPTY)
            .evaluate());
    Assertions.assertThat(t)
        .isExactlyInstanceOf(IllegalStateException.class)
        .hasMessage("TestTable t11 is already defined!");
  }

  @Test
  public void test_testFailError() {
    Throwable t = Assertions.catchThrowable(() ->
        source2.table("table_not_defined")
            .apply(new Fail(new NullPointerException()), Description.EMPTY)
            .evaluate());
    Assertions.assertThat(t)
        .isExactlyInstanceOf(NullPointerException.class);
  }

  @Test
  public void test_testRuleCleanUpError() {
    Throwable t = Assertions.catchThrowable(() ->
        source2.table("delete_clean_up_action_throws_uoe")
            .row(32)
            .cleanUpAfterTest(CleanUpAction.DELETE)
            .apply(new Statement() {
              @Override
              public void evaluate() {
              }
            }, Description.EMPTY)
            .evaluate()
    );
    Assertions.assertThat(t)
        .isExactlyInstanceOf(UnsupportedOperationException.class)
        .hasMessage("simulated error during clean-up");
  }

  @Test(expected = AssertionError.class)
  public void test_mismatch_raisesAssertionError() {
    Tab act = Turntables.tab()
        .row(1);
    assertThat(act)
        .matches()
        .row(42)
        .asExpected();
  }
}
