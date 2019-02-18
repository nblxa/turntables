package io.github.nblxa.fluenttab;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TestYamlUtils {
  @Test
  public void test0x0() {
    Tab tab = FluentTab.tab();
    String yml = YamlUtils.renderTab(tab, 0);
    assertThat(yml).isEqualTo("Table: null");
  }

  @Test
  public void test1x0() {
    Tab tab = FluentTab.tab()
        .col("weather", Typ.STRING);
    String yml = YamlUtils.renderTab(tab, 0);
    assertThat(yml).isEqualTo("Table: null");
  }

  @Test
  public void test1x1() {
    Tab tab = FluentTab.tab()
        .col("weather", Typ.STRING)
        .row("breeze");
    String yml = YamlUtils.renderTab(tab, 0);
    String expected = new StringBuilder("Table:")
        .append(System.lineSeparator())
        .append("    - weather : breeze")
        .toString();
    assertThat(yml).isEqualTo(expected);
  }

  @Test
  public void test2x2() {
    Tab tab = FluentTab.tab()
        .col("weather", Typ.STRING)
        .col("mood", Typ.INTEGER)
        .row("breeze", 8)
        .row("storm", 1);
    String yml = YamlUtils.renderTab(tab, 0);
    String expected = new StringBuilder("Table:")
        .append(System.lineSeparator())
        .append("    - weather : breeze")
        .append(System.lineSeparator())
        .append("      mood    : 8")
        .append(System.lineSeparator())
        .append("    - weather : storm")
        .append(System.lineSeparator())
        .append("      mood    : 1")
        .toString();
    assertThat(yml).isEqualTo(expected);
  }
}
