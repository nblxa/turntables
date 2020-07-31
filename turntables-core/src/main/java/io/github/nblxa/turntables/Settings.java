package io.github.nblxa.turntables;

import java.util.Objects;
import edu.umd.cs.findbugs.annotations.NonNull;

public final class Settings {
  /**
   * Controls the conversions to and from {@link java.math.BigDecimal} for numeric columns.
   */
  public enum DecimalMode {
    /**
     * Default mode ({@link DecimalMode#EXACT}).
     */
    DEFAULT,

    /**
     * Match numbers by exact type and value.
     *
     * <p>It is neither allowed to use values of types {@code Integer}, {@code Long} or {@code Double}
     * in columns defined as {@link Typ#DECIMAL}, nor to use values of type {@code BigDecimal}
     * in columns defined as {@link Typ#INTEGER}, {@link Typ#LONG} or {@link Typ#DOUBLE}.
     *
     * <p>When matching {@code actual} against {@code expected}, the {@code actual} values should
     * precisely match the declared or inferred type for the corresponding {@code expected} column.
     */
    EXACT,

    /**
     * Match numbers by value, converting the types when decimal values are encountered.
     *
     * <p>This allows using values of types {@code Integer}, {@code Long} or {@code Double}
     * in columns defined as {@link Typ#DECIMAL}, and the other way around: using values of type
     * {@code BigDecimal} in columns defined as {@link Typ#INTEGER}, {@link Typ#LONG}
     * or {@link Typ#DOUBLE}.
     *
     * <p>When constructing a {@link Tab}, values will always be converted to the declared
     * or inferred type of the column.
     *
     * <p>When matching {@code actual} against {@code expected}, the {@code actual} value will be
     * converted to the declared or inferred type of {@code expected} column for the purpose
     * of the assertion.
     */
    CONVERT
  }

  /**
   * Controls the character case sensitivity of table and column names when interacting
   * with external data sources and when matching {@code actual} against {@code expected}
   * by column names.
   */
  public enum NameMode {
    /**
     * Default mode ({@link NameMode#CASE_SENSITIVE}).
     */
    DEFAULT,

    /**
     * Table and column names are case-sensitive.
     */
    CASE_SENSITIVE,

    /**
     * Table and column names are case-insensitive. Differences in character case will not cause
     * assertions to fail and will not be highlighted in the diffs. The character case of the column
     * names shown in the diffs will depend on the matching mode, and on whether {@code actual}
     * or {@code expected} has unnamed columns.
     */
    CASE_INSENSITIVE
  }


  /**
   * Row matching modes allow the test developer to tell Turntables how it should decide
   * which rows to match against each other in the expected and the actual tables.
   */
  public enum RowMode {
    /**
     * Default row matching mode: match rows in the exact order of their appearance in both
     * the expected and the actual table. Every row of expected must have a single matching row
     * of actual, and vice versa.
     */
    MATCH_IN_GIVEN_ORDER,

    /**
     * Match rows in any order. The order of the rows' appearance does not play a role for matching,
     * what is important is that every row of expected must have a single matching row of actual,
     * and vice versa.
     */
    MATCH_IN_ANY_ORDER,

    /**
     * Match rows by key. The order of the rows' appearance does not play a role for matching.
     * Every row of expected must have a single matching row of actual with equal values of key columns,
     * and vice versa.
     * @see Tab.Col#isKey
     */
    MATCH_BY_KEY
  }

  /**
   * Column matching modes allow the developer to tell Turntables how it should match actual
   * and expected columns and values.
   */
  public enum ColMode {
    /**
     * Columns and values are matched in the order they are provided in the expected and the actual
     * tables. First column of actual must match the first column of expected, and so on.
     * Column names if provided, will be ignored for the purpose of matching.
     */
    MATCH_IN_GIVEN_ORDER,

    /**
     * Columns and values are matched by column name. The order of columns in actual and expected
     * does not matter for the purpose of matching.
     */
    MATCH_BY_NAME
  }

  @NonNull
  public final DecimalMode decimalMode;
  @NonNull
  public final NameMode nameMode;
  @NonNull
  public final RowMode rowMode;
  @NonNull
  public final ColMode colMode;

  private Settings(@NonNull DecimalMode decimalMode, @NonNull NameMode nameMode,
                  @NonNull RowMode rowMode, @NonNull ColMode colMode) {
    this.decimalMode = decimalMode;
    this.nameMode = nameMode;
    this.rowMode = rowMode;
    this.colMode = colMode;
  }

  @NonNull
  public static BuilderImpl builder() {
    return new BuilderImpl();
  }

  @NonNull
  public BuilderImpl getBuilder() {
    return new BuilderImpl(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Settings settings = (Settings) o;
    return decimalMode == settings.decimalMode &&
        nameMode == settings.nameMode &&
        rowMode == settings.rowMode &&
        colMode == settings.colMode;
  }

  @Override
  public int hashCode() {
    return Objects.hash(decimalMode, nameMode, rowMode, colMode);
  }

  @Override
  public String toString() {
    return "Settings{" +
        "decimalMode=" + decimalMode +
        ", nameMode=" + nameMode +
        ", rowMode=" + rowMode +
        ", colMode=" + colMode +
        '}';
  }

  public interface Builder<S extends Builder<S>> {
    /**
     * Specify whether to convert decimal to other numeric types or not.
     * <p>Default is {@link Settings.DecimalMode#DEFAULT}.
     * @param decimalMode decimal mode
     * @return the builder object
     */
    @NonNull
    Builder<S> decimalMode(@NonNull Settings.DecimalMode decimalMode);

    /**
     * Specify whether to use case-insensitive mode for column and table names.
     * <p>Default is {@link Settings.NameMode#DEFAULT}.
     * @param nameMode name mode
     * @return the builder object
     */
    @NonNull
    Builder<S> nameMode(@NonNull Settings.NameMode nameMode);

    /**
     * Specify the mode in which to match rows.
     * <p>Default is {@link Settings.RowMode#MATCH_IN_GIVEN_ORDER}.
     * @param rowMode row mode
     * @return the builder object
     */
    @NonNull
    Builder<S> rowMode(@NonNull Settings.RowMode rowMode);

    /**
     * Specify the mode in which to match columns.
     * <p>Default is {@link Settings.ColMode#MATCH_IN_GIVEN_ORDER}.
     * @param colMode column mode
     * @return the builder object
     */
    @NonNull
    Builder<S> colMode(@NonNull Settings.ColMode colMode);
  }

  public static class BuilderImpl implements Builder<BuilderImpl> {
    @NonNull
    private DecimalMode decimalMode = DecimalMode.DEFAULT;
    @NonNull
    private NameMode nameMode = NameMode.DEFAULT;
    @NonNull
    private RowMode rowMode = RowMode.MATCH_IN_GIVEN_ORDER;
    @NonNull
    private ColMode colMode = ColMode.MATCH_IN_GIVEN_ORDER;

    private BuilderImpl() {
    }

    private BuilderImpl(Settings settings) {
      this.decimalMode = settings.decimalMode;
      this.nameMode = settings.nameMode;
      this.rowMode = settings.rowMode;
      this.colMode = settings.colMode;
    }

    @Override
    @NonNull
    public BuilderImpl decimalMode(@NonNull DecimalMode decimalMode) {
      this.decimalMode = decimalMode;
      return this;
    }

    @Override
    @NonNull
    public BuilderImpl nameMode(@NonNull NameMode nameMode) {
      this.nameMode = nameMode;
      return this;
    }

    @Override
    @NonNull
    public BuilderImpl rowMode(@NonNull RowMode rowMode) {
      this.rowMode = rowMode;
      return this;
    }

    @Override
    @NonNull
    public BuilderImpl colMode(@NonNull ColMode colMode) {
      this.colMode = colMode;
      return this;
    }

    @NonNull
    public Settings build() {
      return new Settings(decimalMode, nameMode, rowMode, colMode);
    }
  }
}
