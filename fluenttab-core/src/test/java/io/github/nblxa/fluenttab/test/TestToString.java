package io.github.nblxa.fluenttab.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.FluentTab;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.Test;

public class TestToString {
  private static final String LS = System.lineSeparator();

  @Test
  public void test_emptyToString() {
    Tab tab = FluentTab.tab();
    assertThat(tab.toString()).isEqualTo("Table: null");
  }

  @Test
  public void test_toStringInt() {
    Tab tab = FluentTab.tab().row(1);
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : 1")
            .toString();
    assertThat(tab.toString()).isEqualTo(expected);
  }

  @Test
  public void test_toStringString() {
    Tab tab = FluentTab.tab().row("Hello,\tthis is a \"sample\" text\non multiple lines.");
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : \"Hello,\\tthis is a \\\"sample\\\" text\\non multiple lines.\"")
            .toString();
    assertThat(tab.toString()).isEqualTo(expected);
  }

  @Test
  public void test_toStringDate() {
    Tab tab = FluentTab.tab().row(LocalDate.of(2019, 4, 1));
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : 2019-04-01")
            .toString();
    assertThat(tab.toString()).isEqualTo(expected);
  }

  @Test
  public void test_toStringDatetime() {
    Tab tab = FluentTab.tab().row(LocalDateTime.of(2019, 4, 1, 22, 5, 43));
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : 2019-04-01T22:05:43")
            .toString();
    assertThat(tab.toString()).isEqualTo(expected);
  }

  @Test
  public void test_toStringNull() {
    Tab tab = FluentTab.tab().row(null);
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : null")
            .toString();
    assertThat(tab.toString()).isEqualTo(expected);
  }

  @Test
  public void test_toStringDouble() {
    Tab tab = FluentTab.tab().row(2.3);
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : 2.3")
            .toString();
    assertThat(tab.toString()).isEqualTo(expected);
  }

  @Test
  public void test_toStringBoolean() {
    Tab tab = FluentTab.tab().row(false);
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : false")
            .toString();
    assertThat(tab.toString()).isEqualTo(expected);
  }

  @Test
  public void test_toString2x2() {
    Tab tab = FluentTab.tab()
        .row(false, 3.4)
        .row(true, 100.005);
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : false").append(LS)
            .append("      col2 : 3.4").append(LS)
            .append("    - col1 : true").append(LS)
            .append("      col2 : 100.005")
            .toString();
    assertThat(tab.toString()).isEqualTo(expected);
  }

  @Test
  public void test_assertj() {
    Tab tab1 = FluentTab.tab().row(1, 2, 3).row(4, 5, 6);
    Tab tab2 = FluentTab.tab().row(1, 2, 3).row(4, 2, 6);

    Throwable t = catchThrowable(() -> assertThat(tab1).isEqualTo(tab2));
    assertThat(t).isInstanceOf(AssertionError.class);
  }
}
