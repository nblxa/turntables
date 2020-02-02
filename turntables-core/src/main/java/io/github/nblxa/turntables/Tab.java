package io.github.nblxa.turntables;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * <tt>Tab</tt> is shorthand for <tt>Table</tt>, a minimal interface describing a container
 * for data in a tabular format.<p>
 *
 * A <tt>Tab</tt> consists of columns and rows.<p>
 *
 * Two <tt>Tab</tt>s <tt>equal</tt> each other when their columns and rows follow
 * the same order and <tt>equal</tt> each other, correspondingly.
 */
public interface Tab {
  /**
   * The columns of the table.
   * <p>The <tt>Iterable</tt> should always have the same order of columns.
   * @return the columns of the table
   */
  @NonNull
  Iterable<Col> cols();

  /**
   * The rows of the table.
   * <p>The <tt>Iterable</tt> should always have the same order of rows.
   * @return the columns of the table
   */
  @NonNull
  Iterable<Row> rows();

  /**
   * <tt>Col</tt> is shorthand for "Column".<p>
   * Columns summarize the information about the table's columns such as their names and types.
   */
  interface Col {
    /**
     * Column's type.
     * @return column's type
     */
    @NonNull
    Typ typ();

    /**
     * Is this column part of the key?
     * @return true if the column is part of the key
     */
    boolean isKey();

    /**
     * Creates a {@link Val} of the column's type from the given object.
     *
     * @deprecated replace with {@link Utils#getVal}
     * @param o object to convert to this column's type
     * @return {@link Val} for the given object
     */
    @Deprecated
    @NonNull
    Tab.Val valOf(@Nullable Object o);

    /**
     * Says whether the column accepts a value (i.e. the type allows this).
     *
     * @deprecated replace with {@link Typ#acceptsTyp}
     * @param val value to check
     * @return true if the value can be stored in the column
     */
    @Deprecated
    boolean accepts(@NonNull Val val);
  }

  /**
   * <tt>Val</tt> is shorthand for <tt>Value</tt>. This type is a wrapper for values sitting
   * in table rows at each column.
   */
  interface Val {
    /**
     * Get the value's type.
     * @return value's type
     */
    @NonNull
    Typ getTyp();

    /**
     * Evaluate the <tt>Val</tt> and return the actual value stored in the table.<p>
     * Repeated calls must return the same object reference.
     * @return the actual value
     */
    @Nullable
    Object eval();

    /**
     * Assuming this value is <tt>expected</tt>, does the given <tt>actual</tt> value match?<p>
     * If it doesn't this will be counted as mismatch in assertions.
     *
     * @param actual the actual value to match
     * @return true if the actual matches this value
     */
    boolean matchesActual(@NonNull Val actual);
  }

  /**
   * A Row is a horizontal record in the table.<p>
   *
   * Rows contain a value ({@link Val}) for each column ({@link Col}).
   */
  interface Row {
    /**
     * The columns of the row.<p>
     * Elements must equal to those of the parent table's {@link Tab#cols} method.
     * @return the columns of the row
     */
    @NonNull
    Iterable<Col> cols();

    /**
     * Values stored in each column.<p>
     * Should have the same number of elements as {@link Row#cols}.
     * @return values stored in each column.
     */
    @NonNull
    Iterable<Val> vals();
  }

  /**
   * Fluent API for constructing a {@link Tab}.
   */
  interface ColAdder<U extends UnnamedColAdder<U, R>, N extends NamedColAdder<N, R>,
      R extends RowAdder<R>>
    extends UnnamedColAdderPart<U>, NamedColAdderPart<N>, ColAdderRowAdderPart<R> {
  }

  /**
   * Fluent API for constructing a {@link Tab} with unnamed columns.
   */
  interface UnnamedColAdder<U extends UnnamedColAdder<U, R>, R extends RowAdder<R>>
    extends UnnamedColAdderPart<U>, ColAdderRowAdderPart<R> {
  }

  /**
   * Fluent API for constructing a {@link Tab} with named columns.
   */
  interface NamedColAdder<N extends NamedColAdder<N, R>, R extends RowAdder<R>>
    extends NamedColAdderPart<N>, ColAdderRowAdderPart<R> {
  }

  interface UnnamedColAdderPart<U extends UnnamedColAdderPart<U>> {
    /**
     * Add a {@link Tab.Col} to the table with a given type.
     * @param typ column type
     * @return the builder to add further elements to
     */
    @NonNull
    U col(@NonNull Typ typ);

    /**
     * Add a key {@link Tab.Col} to the table with a given type.
     * @param typ key column type
     * @return the builder to add further elements to
     */
    @NonNull
    U key(@NonNull Typ typ);
  }

  interface NamedColAdderPart<N extends NamedColAdderPart<N>> {
    /**
     * Add a named {@link Tab.Col} to the table with a given type.
     * @param name column name
     * @param typ column type
     * @return the builder to add further elements to
     */
    @NonNull
    N col(@NonNull String name, @NonNull Typ typ);

    /**
     * Add a named key {@link Tab.Col} to the table with a given type.
     * @param name key column name
     * @param typ key column type
     * @return the builder to add further elements to
     */
    @NonNull
    N key(@NonNull String name, @NonNull Typ typ);
  }

  interface ColAdderRowAdderPart<R extends RowAdder<R>> {
    /**
     * Return the builder object that allows adding rows to the table.
     * @return the builder to add rows to
     */
    @NonNull
    R rowAdder();
  }

  interface RowAdder<T extends RowAdder<T>> {
    /**
     * Add a {@link Tab.Row} to the table with the given values.
     * @param first the value of the first column
     * @param rest variable-size array with the rest of the values
     * @return the builder to add further rows to
     */
    @NonNull
    T row(@Nullable Object first, @NonNull Object... rest);

    /**
     * Construct the {@link Tab} with the columns and rows provided to this builder.
     * @return new table
     */
    @NonNull
    Tab tab();
  }

  /**
   * Named column.
   */
  interface NamedCol extends Tab.Col {
    /**
     * Return the column's name.
     * @return column's name
     */
    @NonNull
    String name();
  }

  /**
   * Table with named columns.
   */
  interface NamedColTab extends Tab {
    /**
     * Return the named columns of this table.
     * <p>The <tt>Iterable</tt> should always have the same order of columns.
     * @return the named columns of the table
     */
    @NonNull
    Iterable<NamedCol> namedCols();
  }
}
