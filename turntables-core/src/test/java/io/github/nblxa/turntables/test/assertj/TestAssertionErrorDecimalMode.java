package io.github.nblxa.turntables.test.assertj;

import static org.assertj.core.api.Assertions.catchThrowable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import org.junit.Test;
import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.assertj.assertj.AssertAssertJ;

public class TestAssertionErrorDecimalMode {
  private static final String LS = System.lineSeparator();

  @Test
  public void testExpectedIsDecimal() {
    Tab exp = Turntables.tab()
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .row(new BigDecimal(BigInteger.valueOf(10L)),
            BigDecimal.valueOf(10L),
            BigDecimal.valueOf(10.0d));
    Tab act = Turntables.tab()
        .col(Typ.INTEGER)
        .col(Typ.LONG)
        .col(Typ.DOUBLE)
        .row(10, 10L, 10.0d);
    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      Turntables.assertThat(act).matchesExpected(exp);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : col1").append(LS)
            .append("        type : decimal").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col2").append(LS)
            .append("        type : decimal").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col3").append(LS)
            .append("        type : decimal").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - col1 : 10").append(LS)
            .append("        col2 : 10").append(LS)
            .append("        col3 : 10.0").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : col1").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col2").append(LS)
            .append("        type : long").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col3").append(LS)
            .append("        type : double").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - col1 : 10").append(LS)
            .append("        col2 : 10").append(LS)
            .append("        col3 : 10.0"));
  }

  @Test
  public void testActualIsDecimal() {
    Tab exp = Turntables.tab()
        .col(Typ.INTEGER)
        .col(Typ.LONG)
        .col(Typ.DOUBLE)
        .row(10, 10L, 10.0d);
    Tab act = Turntables.tab()
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .row(new BigDecimal(BigInteger.valueOf(10L)),
            BigDecimal.valueOf(10L),
            BigDecimal.valueOf(10.0d));
    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      Turntables.assertThat(act).matchesExpected(exp);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : col1").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col2").append(LS)
            .append("        type : long").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col3").append(LS)
            .append("        type : double").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - col1 : 10").append(LS)
            .append("        col2 : 10").append(LS)
            .append("        col3 : 10.0").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : col1").append(LS)
            .append("        type : decimal").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col2").append(LS)
            .append("        type : decimal").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col3").append(LS)
            .append("        type : decimal").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - col1 : 10").append(LS)
            .append("        col2 : 10").append(LS)
            .append("        col3 : 10.0"));
  }

  @Test
  public void testExpectedIsDecimalMatchesInConvertMode() {
    Tab exp = Turntables.tab()
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .row(new BigDecimal(BigInteger.valueOf(10L)),
            BigDecimal.valueOf(10L),
            BigDecimal.valueOf(10.0d));
    Tab act = Turntables.tab()
        .col(Typ.INTEGER)
        .col(Typ.LONG)
        .col(Typ.DOUBLE)
        .row(10, 10L, 10.0d);
    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      Turntables.assertThat(act)
          .decimalMode(Settings.DecimalMode.CONVERT)
          .matchesExpected(exp);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isNull();
  }

  @Test
  public void testActualIsDecimalMatchesInConvertMode() {
    Tab exp = Turntables.tab()
        .col(Typ.INTEGER)
        .col(Typ.LONG)
        .col(Typ.DOUBLE)
        .row(10, 10L, 10.0d);
    Tab act = Turntables.tab()
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .row(new BigDecimal(BigInteger.valueOf(10L)),
            BigDecimal.valueOf(10L),
            BigDecimal.valueOf(10.0d));
    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      Turntables.assertThat(act)
          .decimalMode(Settings.DecimalMode.CONVERT)
          .matchesExpected(exp);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isNull();
  }

  @Test
  public void testExpectedIsDecimalValMismatchInConvertMode() {
    Tab exp = Turntables.tab()
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .col(Typ.STRING)
        .row(new BigDecimal(BigInteger.valueOf(10L)),
            BigDecimal.valueOf(10L),
            BigDecimal.valueOf(10.0d),
            "x");
    Tab act = Turntables.tab()
        .col(Typ.INTEGER)
        .col(Typ.LONG)
        .col(Typ.DOUBLE)
        .col(Typ.STRING)
        .row(10, 10L, 10.0d, "y");
    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      Turntables.assertThat(act)
          .decimalMode(Settings.DecimalMode.CONVERT)
          .matchesExpected(exp);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("    - col1 : 10").append(LS)
            .append("      col2 : 10").append(LS)
            .append("      col3 : 10.0").append(LS)
            .append("      col4 : x").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("    - col1 : 10").append(LS)
            .append("      col2 : 10").append(LS)
            .append("      col3 : 10.0").append(LS)
            .append("      col4 : y"));
  }

  @Test
  public void testActualIsDecimalValMismatchInConvertMode() {
    Tab exp = Turntables.tab()
        .col(Typ.INTEGER)
        .col(Typ.LONG)
        .col(Typ.DOUBLE)
        .col(Typ.STRING)
        .row(10, 10L, 10.0d, "x");
    Tab act = Turntables.tab()
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .col(Typ.STRING)
        .row(new BigDecimal(BigInteger.valueOf(10L)),
            BigDecimal.valueOf(10L),
            BigDecimal.valueOf(10.0d),
            "y");
    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      Turntables.assertThat(act)
          .decimalMode(Settings.DecimalMode.CONVERT)
          .matchesExpected(exp);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("    - col1 : 10").append(LS)
            .append("      col2 : 10").append(LS)
            .append("      col3 : 10.0").append(LS)
            .append("      col4 : x").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("    - col1 : 10").append(LS)
            .append("      col2 : 10").append(LS)
            .append("      col3 : 10.0").append(LS)
            .append("      col4 : y"));
  }

  @Test
  public void testExpectedIsDecimalColMismatchInConvertMode() {
    Tab exp = Turntables.tab()
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .col(Typ.STRING)
        .row(new BigDecimal(BigInteger.valueOf(10L)),
            BigDecimal.valueOf(10L),
            BigDecimal.valueOf(10.0d),
            "x");
    Tab act = Turntables.tab()
        .col(Typ.INTEGER)
        .col(Typ.LONG)
        .col(Typ.DOUBLE)
        .col(Typ.DATE)
        .row(10, 10L, 10.0d, LocalDate.of(2020, 7, 31));
    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      Turntables.assertThat(act)
          .decimalMode(Settings.DecimalMode.CONVERT)
          .matchesExpected(exp);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : col1").append(LS)
            .append("        type : decimal").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col2").append(LS)
            .append("        type : decimal").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col3").append(LS)
            .append("        type : decimal").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col4").append(LS)
            .append("        type : string").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - col1 : 10").append(LS)
            .append("        col2 : 10").append(LS)
            .append("        col3 : 10.0").append(LS)
            .append("        col4 : x").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : col1").append(LS)
            .append("        type : decimal").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col2").append(LS)
            .append("        type : decimal").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col3").append(LS)
            .append("        type : decimal").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col4").append(LS)
            .append("        type : date").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - col1 : 10").append(LS)
            .append("        col2 : 10").append(LS)
            .append("        col3 : 10.0").append(LS)
            .append("        col4 : 2020-07-31"));
  }

  @Test
  public void testActualIsDecimalColMismatchInConvertMode() {
    Tab exp = Turntables.tab()
        .col(Typ.INTEGER)
        .col(Typ.LONG)
        .col(Typ.DOUBLE)
        .col(Typ.STRING)
        .row(10, 10L, 10.0d, "x");
    Tab act = Turntables.tab()
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .col(Typ.DECIMAL)
        .col(Typ.DATE)
        .row(new BigDecimal(BigInteger.valueOf(10L)),
            BigDecimal.valueOf(10L),
            BigDecimal.valueOf(10.0d),
            LocalDate.of(2020, 7, 31));
    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      Turntables.assertThat(act)
          .decimalMode(Settings.DecimalMode.CONVERT)
          .matchesExpected(exp);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : col1").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col2").append(LS)
            .append("        type : long").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col3").append(LS)
            .append("        type : double").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col4").append(LS)
            .append("        type : string").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - col1 : 10").append(LS)
            .append("        col2 : 10").append(LS)
            .append("        col3 : 10.0").append(LS)
            .append("        col4 : x").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : col1").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col2").append(LS)
            .append("        type : long").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col3").append(LS)
            .append("        type : double").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col4").append(LS)
            .append("        type : date").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - col1 : 10").append(LS)
            .append("        col2 : 10").append(LS)
            .append("        col3 : 10.0").append(LS)
            .append("        col4 : 2020-07-31"));
  }
}
