package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TestPrism {
  @Test
  public void noOp() {
    Tab tab = Turntables.tab()
        .col("a", Typ.DATE)
        .col("b", Typ.STRING)
        .row(LocalDate.of(2000, 1, 1), "Millenium")
        .row(LocalDate.of(2020, 2, 2), "Oh two oh two");

    Prism prism = NoOpPrism.of(tab);

    assertThat(prism.representation())
        .isEqualTo(tab.toString());
  }

  @Test
  public void prismEqualsTab() {
    Tab tab = Turntables.tab();
    Prism prism = NoOpPrism.of(Turntables.tab());
    Throwable t = catchThrowable(() -> prism.equals(tab));
    Assertions.assertThat(t)
        .isExactlyInstanceOf(UnsupportedOperationException.class)
        .hasMessage("Prism does not support equals");
  }

  @Test
  public void tabEqualsPrism() {
    Tab tab = Turntables.tab();
    Prism prism = NoOpPrism.of(Turntables.tab());
    Throwable t = catchThrowable(() -> tab.equals(prism));
    Assertions.assertThat(t)
        .isExactlyInstanceOf(UnsupportedOperationException.class)
        .hasMessage("Prism does not support equals");
  }

  @Test
  public void prismHashCode() {
    Prism prism = NoOpPrism.of(Turntables.tab());
    Throwable t = catchThrowable(() -> prism.hashCode());
    Assertions.assertThat(t)
        .isExactlyInstanceOf(UnsupportedOperationException.class)
        .hasMessage("Prism does not support hashCode");
  }

  @Test
  public void prismRowEqualsTabRow() {
    Tab exp = Turntables.tab().row(1);
    Tab act = Turntables.tab().row(2);
    Prism prism = AssertionValPrism.ofExpected(exp, act);
    Tab.Row prismRow = prism.rows().iterator().next();
    Tab.Row tabRow = act.rows().iterator().next();
    Throwable t = catchThrowable(() -> prismRow.equals(tabRow));
    Assertions.assertThat(t)
        .isExactlyInstanceOf(UnsupportedOperationException.class)
        .hasMessage("Prism does not support equals");
  }

  @Test
  public void tabRowEqualsPrismRow() {
    Tab exp = Turntables.tab().row(1);
    Tab act = Turntables.tab().row(2);
    Prism prism = AssertionValPrism.ofExpected(exp, act);
    Tab.Row prismRow = prism.rows().iterator().next();
    Tab.Row tabRow = act.rows().iterator().next();
    Throwable t = catchThrowable(() -> tabRow.equals(prismRow));
    Assertions.assertThat(t)
        .isExactlyInstanceOf(UnsupportedOperationException.class)
        .hasMessage("Prism does not support equals");
  }

  @Test
  public void prismRowHashCode() {
    Tab exp = Turntables.tab().row(1);
    Tab act = Turntables.tab().row(2);
    Prism prism = AssertionValPrism.ofExpected(exp, act);
    Tab.Row prismRow = prism.rows().iterator().next();
    Throwable t = catchThrowable(() -> prismRow.hashCode());
    Assertions.assertThat(t)
        .isExactlyInstanceOf(UnsupportedOperationException.class)
        .hasMessage("Prism does not support hashCode");
  }
}
