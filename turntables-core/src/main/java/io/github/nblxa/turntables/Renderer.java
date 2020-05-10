package io.github.nblxa.turntables;

/**
 * Creates a textual representation of a {@link Tab} by rendering it as {@link String}.
 */
public interface Renderer {
  /**
   * Render a {@link Tab} as {@link String}.
   *
   * The first line of the rendered text is not indented. <p>{@code indent} represents
   * the level of indentation of the first line in the context where the table's representation
   * will be used.
   *
   * @param tab table
   * @param indent the initial level of indentation
   * @return the rendered table as String
   */
  String renderTab(Tab tab, int indent);

  /**
   * Render a {@link Tab.Row} as {@link String}.
   *
   * The first line of the rendered text is not indented. <p>{@code indent} represents
   * the level of indentation of the first line in the context where the table's representation
   * will be used.
   *
   * @param row table row
   * @param indent the initial level of indentation
   * @return the rendered row as String
   */
  String renderRow(Tab.Row row, int indent);

  /**
   * Render a {@link Tab.Val} as {@link String}.
   *
   * @param val table cell value
   * @return the rendered value as String
   */
  String renderVal(Tab.Val val);
}
