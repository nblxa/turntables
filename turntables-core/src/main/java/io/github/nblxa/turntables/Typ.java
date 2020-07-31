package io.github.nblxa.turntables;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.nblxa.turntables.exception.UnsupportedTypConversionException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

/**
 * <tt>Typ</tt> is shorthand for <tt>Type</tt>.
 *
 * This enum defines the possible types a column or a value may have.
 */
public enum Typ {
  /**
   * Any type.
   * <p>Use this to define a {@link Tab.Col} whose type should be inferred.
   * <p>A {@link Tab.Val} cannot be of type ANY.
   */
  ANY(new TypAny()),

  /**
   * Integer type with numeric values corresponding to Java {@link Integer}.
   */
  INTEGER(new TypInteger()),

  /**
   * Long type with numeric values corresponding to Java {@link Long}.
   */
  LONG(new TypLong()),

  /**
   * String type with text values of any length.
   */
  STRING(new TypString()),

  /**
   * Boolean type.
   */
  BOOLEAN(new TypBoolean()),

  /**
   * Double type with numeric values corresponding to Java {@link Double}.
   */
  DOUBLE(new TypDouble()),

  /**
   * Decimal type corresponding to Java {@link java.math.BigDecimal}.
   */
  DECIMAL(new TypDecimal()),

  /**
   * Date type.
   * <p>This corresponds to a date type without time component or time zone,
   * e.g. Java {@link java.time.LocalDate}.
   */
  DATE(new TypDate()),

  /**
   * Date &amp; time.
   * <p>This corresponds to a datetime type without time component or time zone,
   * e.g. Java {@link java.time.LocalDateTime}.
   */
  DATETIME(new TypDatetime());

  private final Internal<?> internal;
  private final Tab.Val nullVal;

  Typ(Internal<?> internal) {
    this.internal = internal;
    this.nullVal = new NullVal(this);
  }

  public boolean accepts(Typ typ) {
    return typ == this || internal.accepts(typ);
  }

  @NonNull
  Object convert(@NonNull Object obj) {
    return internal.convert(obj);
  }

  @NonNull
  Tab.Val nullVal() {
    return nullVal;
  }

  @Override
  public String toString() {
    return super.toString().toLowerCase(Locale.getDefault());
  }

  abstract static class Internal<T> {
    private Internal() {
    }

    boolean accepts(Typ typ) {
      return typ == ANY;
    }

    @NonNull
    T convert(@NonNull Object obj) {
      throw new UnsupportedTypConversionException("Type conversion is not supported for "
          + getClass().getSimpleName());
    }

    boolean isDecimalConversionEnabled() {
      return Turntables.getSettings().decimalMode == Settings.DecimalMode.CONVERT;
    }
  }

  static class TypAny extends Internal<Object> {
    @Override
    boolean accepts(Typ typ) {
      return true;
    }

    @NonNull
    @Override
    Object convert(@NonNull Object obj) {
      return obj;
    }
  }

  static class TypInteger extends Internal<Integer> {
    @Override
    boolean accepts(Typ typ) {
      return (typ == Typ.DECIMAL && isDecimalConversionEnabled()) || super.accepts(typ);
    }

    @NonNull
    @Override
    Integer convert(@NonNull Object obj) {
      if (obj instanceof BigDecimal && isDecimalConversionEnabled()) {
        return ((BigDecimal) obj).intValueExact();
      }
      return super.convert(obj);
    }
  }

  static class TypLong extends Internal<Long> {
    @Override
    boolean accepts(Typ typ) {
      return (typ == Typ.DECIMAL && isDecimalConversionEnabled()) || super.accepts(typ);
    }

    @NonNull
    @Override
    Long convert(@NonNull Object obj) {
      if (obj instanceof BigDecimal && isDecimalConversionEnabled()) {
        return ((BigDecimal) obj).longValueExact();
      }
      return super.convert(obj);
    }
  }

  static class TypString extends Internal<String> {
  }

  static class TypBoolean extends Internal<Boolean> {
  }

  static class TypDouble extends Internal<Double> {
    @Override
    boolean accepts(Typ typ) {
      return (typ == Typ.DECIMAL && isDecimalConversionEnabled()) || super.accepts(typ);
    }

    @NonNull
    @Override
    Double convert(@NonNull Object obj) {
      if (obj instanceof BigDecimal && isDecimalConversionEnabled()) {
        return ((BigDecimal) obj).doubleValue();
      }
      return super.convert(obj);
    }
  }

  static class TypDecimal extends Internal<BigDecimal> {
    @Override
    boolean accepts(Typ typ) {
      return ((typ == Typ.INTEGER || typ == Typ.LONG || typ == Typ.DOUBLE)
          && isDecimalConversionEnabled())
          || super.accepts(typ);
    }

    @NonNull
    @Override
    BigDecimal convert(@NonNull Object obj) {
      if (obj instanceof Integer && isDecimalConversionEnabled()) {
        return new BigDecimal(BigInteger.valueOf((Integer) obj));
      }
      if (obj instanceof Long && isDecimalConversionEnabled()) {
        return BigDecimal.valueOf((Long) obj);
      }
      if (obj instanceof Double && isDecimalConversionEnabled()) {
        return BigDecimal.valueOf((Double) obj);
      }
      return super.convert(obj);
    }
  }

  static class TypDate extends Internal<LocalDate> {
  }

  static class TypDatetime extends Internal<LocalDateTime> {
  }

  static final class NullVal extends AbstractTab.AbstractVal {
    @NonNull
    private final Typ typ;

    private NullVal(@NonNull Typ typ) {
      this.typ = typ;
    }

    @Override
    @NonNull
    public Typ typ() {
      return typ;
    }

    @Override
    @Nullable
    public Object evaluate() {
      return null;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof NullVal)) {
        return false;
      }
      NullVal nullVal = (NullVal) o;
      return nullVal.canEqual(this) && typ() == nullVal.typ();
    }

    @Override
    public boolean canEqual(Object o) {
      return o instanceof NullVal;
    }

    @Override
    public int hashCode() {
      return Objects.hash(typ());
    }

    @Override
    public String toString() {
      return "null";
    }
  }
}
