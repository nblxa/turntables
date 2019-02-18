package io.github.nblxa.fluenttab.test;

import static io.github.nblxa.fluenttab.FluentTab.testInt;
import static io.github.nblxa.fluenttab.assertj.TablesAssertj.*;
import static org.assertj.core.api.Assertions.*;

import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.FluentTab;
import org.junit.Test;

public class TestAssertj {
  private static final String LS = System.lineSeparator();

  @Test
  public void testMismatch() {
    Tab act = FluentTab.tab()
        .row(1, 2)
        .row(3, 4);
    Tab exp = FluentTab.tab()
        .row(testInt(i -> i > 10), 2)
        .row(3, 4);

    Throwable t = catchThrowable(() -> {
      assertThat(act)
          .matches(exp)
          .isNotEqualTo(exp);
    });
    assertThat(t)
        .isInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(LS)
            .append("Expecting:").append(LS)
            .append(" <\"Table:").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4\">").append(LS)
            .append("to match:").append(LS)
            .append(" <\"Table:").append(LS)
            .append("    - col1 : java.util.function.IntPredicate").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4\">").append(LS)
            .append("but a mismatch was found.").toString());
  }

  @Test
  public void testMatch() {
    Tab act = FluentTab.tab()
        .row(1, 2)
        .row(3, 4);
    Tab exp = FluentTab.tab()
        .row(testInt(i -> i <= 10), 2)
        .row(3, 4);

    Throwable t = catchThrowable(() -> {
      assertThat(act)
          .matches(exp)
          .isNotEqualTo(exp);
    });
    assertThat(t).isNull();
  }
}
