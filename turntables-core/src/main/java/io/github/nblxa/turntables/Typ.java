package io.github.nblxa.turntables;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
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

  private final Internal internal;
  private final Tab.Val nullVal;

  Typ(Internal internal) {
    this.internal = internal;
    this.nullVal = new NullVal(this);
  }

  boolean acceptsTyp(Typ typ) {
    return typ == this || internal.accepts(typ);
  }

  Tab.Val nullVal() {
    return nullVal;
  }

  abstract static class Internal {
    private Internal() {
    }

    boolean accepts(Typ typ) {
      return typ == ANY;
    }
  }

  static class TypAny extends Internal {
    @Override
    boolean accepts(Typ typ) {
      return true;
    }
  }

  static class TypInteger extends Internal {
  }

  static class TypLong extends Internal {
  }

  static class TypString extends Internal {
  }

  static class TypBoolean extends Internal {
  }

  static class TypDouble extends Internal {
  }

  static class TypDate extends Internal {
  }

  static class TypDatetime extends Internal {
  }

  static final class NullVal extends AbstractTab.AbstractVal {
    private final Typ typ;

    private NullVal(Typ typ) {
      this.typ = typ;
    }

    @Override
    @NonNull
    public Typ getTyp() {
      return typ;
    }

    @Override
    @Nullable
    public Object eval() {
      return null;
    }

    @Override
    public boolean matchesActual(@NonNull Tab.Val actual) {
      Objects.requireNonNull(actual, "other");
      return actual.eval() == null;
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
      return nullVal.canEqual(this) && getTyp() == nullVal.getTyp();
    }

    @Override
    public boolean canEqual(Object o) {
      return o instanceof NullVal;
    }

    @Override
    public int hashCode() {
      return Objects.hash(getTyp());
    }

    @Override
    public String toString() {
      return "null";
    }
  }
}
