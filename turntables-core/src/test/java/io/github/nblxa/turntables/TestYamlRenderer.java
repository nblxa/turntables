package io.github.nblxa.turntables;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TestYamlRenderer {
  private static final String LS = System.lineSeparator();

  private static final Renderer renderer = YamlRenderer.DEFAULT_SIMPLE;

  @Test
  public void test0x0() {
    Tab tab = Turntables.tab();
    String yml = renderer.renderTab(tab, 0);
    assertThat(yml).isEqualTo("Table: null");
  }

  @Test
  public void test1x0() {
    Tab tab = Turntables.tab()
        .col("weather", Typ.STRING);
    String yml = renderer.renderTab(tab, 0);
    assertThat(yml).isEqualTo("Table: null");
  }

  @Test
  public void test1x1() {
    Tab tab = Turntables.tab()
        .col("weather", Typ.STRING)
        .row("breeze");
    String yml = renderer.renderTab(tab, 0);
    String expected = new StringBuilder()
        .append("Table:").append(LS)
        .append("    - weather : breeze")
        .toString();
    assertThat(yml).isEqualTo(expected);
  }

  @Test
  public void test2x2() {
    Tab tab = Turntables.tab()
        .col("weather", Typ.STRING)
        .col("mood", Typ.INTEGER)
        .row("breeze", 8)
        .row("storm", 1);
    String yml = renderer.renderTab(tab, 0);
    String expected = new StringBuilder()
        .append("Table:").append(LS)
        .append("    - weather : breeze").append(LS)
        .append("      mood    : 8").append(LS)
        .append("    - weather : storm").append(LS)
        .append("      mood    : 1")
        .toString();
    assertThat(yml).isEqualTo(expected);
  }

  @Test
  public void testExtended() {
    Tab tab = Turntables.tab()
        .col("weather", Typ.STRING)
        .col("mood", Typ.INTEGER)
        .row("breeze", 8)
        .row("storm", 1);
    String yml = YamlRenderer.DEFAULT_EXTENDED.renderTab(tab, 0);
    String expected = new StringBuilder()
        .append("Table:").append(LS)
        .append("  cols:").append(LS)
        .append("      - name : weather").append(LS)
        .append("        type : string").append(LS)
        .append("        key  : false").append(LS)
        .append("      - name : mood").append(LS)
        .append("        type : integer").append(LS)
        .append("        key  : false").append(LS)
        .append("  rows:").append(LS)
        .append("      - weather : breeze").append(LS)
        .append("        mood    : 8").append(LS)
        .append("      - weather : storm").append(LS)
        .append("        mood    : 1")
        .toString();
    assertThat(yml).isEqualTo(expected);
  }
}
