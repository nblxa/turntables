package io.github.nblxa.turntables;

import edu.umd.cs.findbugs.annotations.NonNull;

public class Settings {
  public enum DecimalMode {
    /**
     * Default mode ({@code EXACT}).
     */
    DEFAULT,

    /**
     * Match numbers by exact type.
     *
     * <p>BigDecimal will not match with Integer or Double.
     */
    EXACT,

    /**
     * Match numbers by value.
     *
     * <p>BigDecimal will match Integer or Double if they evaluate to the same number.
     */
    ALLOW_BIG
  }

  public enum ColNamesMode {
    DEFAULT,
    CASE_SENSITIVE,
    CASE_INSENSITIVE
  }

  @NonNull
  public final DecimalMode decimalMode;

  @NonNull
  public final ColNamesMode colNamesMode;

  public Settings(@NonNull DecimalMode decimalMode, @NonNull ColNamesMode colNamesMode) {
    this.decimalMode = decimalMode;
    this.colNamesMode = colNamesMode;
  }

  public static class Builder {
    @NonNull
    private DecimalMode decimalMode = DecimalMode.DEFAULT;
    @NonNull
    private ColNamesMode colNamesMode = ColNamesMode.DEFAULT;

    @NonNull
    public Builder decimalMode(@NonNull DecimalMode decimalMode) {
      this.decimalMode = decimalMode;
      return this;
    }

    @NonNull
    public Builder colNamesMode(@NonNull ColNamesMode colNamesMode) {
      this.colNamesMode = colNamesMode;
      return this;
    }

    @NonNull
    public Settings build() {
      return new Settings(decimalMode, colNamesMode);
    }
  }
}
