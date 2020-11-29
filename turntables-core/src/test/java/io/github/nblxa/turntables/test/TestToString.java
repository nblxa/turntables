package io.github.nblxa.turntables.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import java.time.LocalDate;
import java.time.LocalDateTime;
import io.github.nblxa.turntables.Typ;
import org.junit.Test;

public class TestToString {
  private static final String LS = System.lineSeparator();

  @Test
  public void test_emptyToString() {
    Tab tab = Turntables.tab();
    assertThat(tab).hasToString("Table: null");
  }

  @Test
  public void test_toStringInt() {
    Tab tab = Turntables.tab().row(1);
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : 1")
            .toString();
    assertThat(tab).hasToString(expected);
  }

  @Test
  public void test_toStringString() {
    Tab tab = Turntables.tab().row("Hello,\tthis is a \"sample\" text\non multiple lines.");
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : \"Hello,\\tthis is a \\\"sample\\\" text\\non multiple lines.\"")
            .toString();
    assertThat(tab).hasToString(expected);
  }

  @Test
  public void test_toStringDate() {
    Tab tab = Turntables.tab().row(LocalDate.of(2019, 4, 1));
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : 2019-04-01")
            .toString();
    assertThat(tab).hasToString(expected);
  }

  @Test
  public void test_toStringDatetime() {
    Tab tab = Turntables.tab().row(LocalDateTime.of(2019, 4, 1, 22, 5, 43));
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : 2019-04-01T22:05:43")
            .toString();
    assertThat(tab).hasToString(expected);
  }

  @Test
  public void test_toStringNull() {
    Tab tab = Turntables.tab().row(null);
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : null")
            .toString();
    assertThat(tab).hasToString(expected);
  }

  @Test
  public void test_toStringDouble() {
    Tab tab = Turntables.tab().row(2.3);
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : 2.3")
            .toString();
    assertThat(tab).hasToString(expected);
  }

  @Test
  public void test_toStringBoolean() {
    Tab tab = Turntables.tab().row(false);
    String expected =
        new StringBuilder()
            .append("Table:").append(LS)
            .append("    - col1 : false")
            .toString();
    assertThat(tab).hasToString(expected);
  }

  @Test
  public void test_toString2x2() {
    Tab tab = Turntables.tab()
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
    assertThat(tab).hasToString(expected);
  }

  @Test
  public void test_assertj() {
    Tab tab1 = Turntables.tab().row(1, 2, 3).row(4, 5, 6);
    Tab tab2 = Turntables.tab().row(1, 2, 3).row(4, 2, 6);

    Throwable t = catchThrowable(() -> assertThat(tab1).isEqualTo(tab2));
    assertThat(t).isInstanceOf(AssertionError.class);
  }

  @Test
  public void testCols() {
    Tab tab = Turntables.tab().col("x", Typ.INTEGER).key("y", Typ.STRING);
    assertThat(tab.cols())
        .hasToString("[[x integer], [y KEY string]]");
  }

  @Test
  public void testRow() {
    Tab tab = Turntables.tab()
        .col(Typ.INTEGER)
        .col(Typ.STRING)
        .row(1, "red");
    assertThat(tab.rows().iterator().next())
        .hasToString(new StringBuilder()
            .append("Row:").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : red")
            .toString());
  }
}
