package io.github.nblxa.turntables;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Objects;

public enum Typ {
  ANY(new TypAny()),
  INTEGER(new TypInteger()),
  LONG(new TypLong()),
  STRING(new TypString()),
  BOOLEAN(new TypBoolean()),
  DOUBLE(new TypDouble()),
  DATE(new TypDate()),
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
    public boolean matchesActual(@NonNull Tab.Val other) {
      Objects.requireNonNull(other, "other");
      return other.eval() == null;
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
