package io.github.nblxa.turntables.assertion;

import static io.github.dimpon.testprivate.API.lookupPrivatesIn;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Utils;

public class TestUnorderedRowAsserter {
  interface TestCreateMatchList {
    ImmutableMatchList createMatchList();
  }

  @Test
  public void testValueRowMatches1() {
    Tab exp = Turntables.tab()
        .row("a", 1)
        .row("b", 2)
        .row("c", 3);
    Tab act = Turntables.tab()
        .row("c", 3)
        .row("b", 2)
        .row("a", 99);
    ValAsserter va = new OrderedValAsserter();
    UnorderedRowAsserter ora = new UnorderedRowAsserter(exp.rows(), act.rows(), 1, va);
    TestCreateMatchList cml = lookupPrivatesIn(ora).usingInterface(TestCreateMatchList.class);
    ImmutableMatchList ml = cml.createMatchList();
    assertThat(ml)
        .containsExactlyInAnyOrder(Utils.entry(1, 1), Utils.entry(2, 0));
  }

  @Test
  public void testValueRowMatches2() {
    Tab exp = Turntables.tab()
        .row("null", Turntables.nul())
        .row("true", false)
        .row("false", true);
    Tab act = Turntables.tab().row("true", false)
        .row("false", true)
        .row("null", false);
    ValAsserter va = new OrderedValAsserter();
    UnorderedRowAsserter ora = new UnorderedRowAsserter(exp.rows(), act.rows(), 1, va);
    TestCreateMatchList cml = lookupPrivatesIn(ora).usingInterface(TestCreateMatchList.class);
    ImmutableMatchList ml = cml.createMatchList();
    assertThat(ml)
        .containsExactlyInAnyOrder(Utils.entry(1, 0), Utils.entry(2, 1));
  }
}
