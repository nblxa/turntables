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

  @NonNull
  public final DecimalMode decimalMode;

  @NonNull
  public final NameMode nameMode;

  public Settings(@NonNull DecimalMode decimalMode, @NonNull NameMode nameMode) {
    this.decimalMode = decimalMode;
    this.nameMode = nameMode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Settings settings = (Settings) o;
    return decimalMode == settings.decimalMode &&
        nameMode == settings.nameMode;
  }

  @Override
  public int hashCode() {
    return Objects.hash(decimalMode, nameMode);
  }

  @Override
  public String toString() {
    return "Settings{" +
        "decimalMode=" + decimalMode +
        ", nameMode=" + nameMode +
        '}';
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    @NonNull
    private DecimalMode decimalMode = DecimalMode.DEFAULT;
    @NonNull
    private NameMode nameMode = NameMode.DEFAULT;

    private Builder() {
    }

    @NonNull
    public Builder decimalMode(@NonNull DecimalMode decimalMode) {
      this.decimalMode = decimalMode;
      return this;
    }

    @NonNull
    public Builder nameMode(@NonNull NameMode nameMode) {
      this.nameMode = nameMode;
      return this;
    }

    @NonNull
    public Settings build() {
      return new Settings(decimalMode, nameMode);
    }
  }
}
