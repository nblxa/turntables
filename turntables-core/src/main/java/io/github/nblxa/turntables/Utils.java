package io.github.nblxa.turntables;

import io.github.nblxa.turntables.exception.StructureException;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class Utils {
  @NonNull
  static Object[] prepend(@Nullable Object first, @NonNull Object[] rest) {
    Objects.requireNonNull(rest, "rest");
    Object[] objects = new Object[1 + rest.length];
    objects[0] = first;
    System.arraycopy(rest, 0, objects, 1, rest.length);
    return objects;
  }

  static void checkObjects(@NonNull Object[] objects) {
    Objects.requireNonNull(objects, "Row is null");
    if (objects.length == 0) {
      throw new IllegalArgumentException("Empty row");
    }
  }

  static void inferTyp(@NonNull Tab.Col col, @NonNull Tab.Val fromVal,
                       @NonNull ListIterator<Tab.Col> iterCol) {
    checkColAccepts(col.typ(), fromVal.typ());
    if (col.typ() == Typ.ANY && fromVal.typ() != Typ.ANY) {
      final Tab.Col inferredCol = new TableUtils.SimpleCol(col.name(), fromVal.typ(), col.isKey());
      iterCol.set(inferredCol);
    }
  }

  private static void checkColAccepts(Typ colTyp, Typ valTyp) {
    if (!colTyp.accepts(valTyp)) {
      throw new StructureException("Expected " + colTyp
          + " or compatible but got " + valTyp + ".");
    }
  }

  @NonNull
  static Typ getTyp(Object o) {
    Typ typ;
    typ = typStandard(o);
    if (typ != null) {
      return typ;
    }
    typ = typSupplier(o);
    if (typ != null) {
      return typ;
    }
    typ = typPredicate(o);
    if (typ != null) {
      return typ;
    }
    throw new IllegalArgumentException(illegalTypeMessage(o));
  }

  @NonNull
  private static String illegalTypeMessage(@NonNull Object o) {
    return "Unsupported object type: " + o.getClass();
  }

  @NonNull
  static Tab.Val getVal(Object o, Typ colTyp) {
    Tab.Val val = valStandard(o, colTyp);
    if (val == null) {
      val = valSupplier(o, colTyp);
    }
    if (val == null) {
      val = valPredicate(o, colTyp);
    }
    if (val == null) {
      throw new IllegalArgumentException(illegalTypeMessage(o));
    } else {
      return val;
    }
  }

  @Nullable
  private static Typ typStandard(@Nullable Object o) {
    if (o == null) {
      return Typ.ANY;
    }
    if (o instanceof Integer) {
      return Typ.INTEGER;
    }
    if (o instanceof Long) {
      return Typ.LONG;
    }
    if (o instanceof Double) {
      return Typ.DOUBLE;
    }
    if (o instanceof BigDecimal) {
      return Typ.DECIMAL;
    }
    if (o instanceof BigInteger) {
      return Typ.DECIMAL;
    }
    if (o instanceof Boolean) {
      return Typ.BOOLEAN;
    }
    if (o instanceof CharSequence) {
      return Typ.STRING;
    }
    if (o instanceof LocalDateTime || o instanceof java.sql.Timestamp) {
      return Typ.DATETIME;
    }
    if (o instanceof LocalDate || o instanceof java.util.Date) {
      return Typ.DATE;
    }
    return null;
  }

  @Nullable
  private static Tab.Val valStandard(Object o, Typ colTyp) {
    Typ typ = typStandard(o);
    if (typ == null) {
      return null;
    } else {
      checkColAccepts(colTyp, typ);
      return new TableUtils.SimpleVal(colTyp == Typ.ANY ? typ : colTyp, o);
    }
  }

  @Nullable
  private static Typ typSupplier(@NonNull Object o) {
    if (o instanceof IntSupplier) {
      return Typ.INTEGER;
    }
    if (o instanceof LongSupplier) {
      return Typ.LONG;
    }
    if (o instanceof DoubleSupplier) {
      return Typ.DOUBLE;
    }
    if (o instanceof BooleanSupplier) {
      return Typ.BOOLEAN;
    }
    return null;
  }

  @Nullable
  private static Tab.Val valSupplier(Object o, Typ colTyp) {
    if (o instanceof IntSupplier) {
      Typ typ = typSupplier(o);
      assert typ != null;
      checkColAccepts(colTyp, typ);
      return new TableUtils.SuppVal(colTyp == Typ.ANY ? typ : colTyp, ((IntSupplier) o)::getAsInt);
    }
    if (o instanceof LongSupplier) {
      Typ typ = typSupplier(o);
      assert typ != null;
      checkColAccepts(colTyp, typ);
      return new TableUtils.SuppVal(colTyp == Typ.ANY ? typ : colTyp, ((LongSupplier) o)::getAsLong);
    }
    if (o instanceof DoubleSupplier) {
      Typ typ = typSupplier(o);
      assert typ != null;
      checkColAccepts(colTyp, typ);
      return new TableUtils.SuppVal(colTyp == Typ.ANY ? typ : colTyp, ((DoubleSupplier) o)::getAsDouble);
    }
    if (o instanceof BooleanSupplier) {
      Typ typ = typSupplier(o);
      assert typ != null;
      checkColAccepts(colTyp, typ);
      return new TableUtils.SuppVal(colTyp == Typ.ANY ? typ : colTyp, ((BooleanSupplier) o)::getAsBoolean);
    }
    return null;
  }

  @Nullable
  private static Typ typPredicate(@NonNull Object o) {
    if (o instanceof Predicate) {
      return Typ.ANY;
    }
    if (o instanceof IntPredicate) {
      return Typ.INTEGER;
    }
    if (o instanceof LongPredicate) {
      return Typ.LONG;
    }
    if (o instanceof DoublePredicate) {
      return Typ.DOUBLE;
    }
    if (o instanceof Pattern) {
      return Typ.STRING;
    }
    return null;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  private static Tab.Val valPredicate(Object o, Typ colTyp) {
    if (o instanceof Predicate<?>) {
      Typ typ = typPredicate(o);
      assert typ != null;
      checkColAccepts(colTyp, typ);
      return new TableUtils.AssertionVal(colTyp == Typ.ANY ? typ : colTyp, ((Predicate<Object>) o),
          Predicate.class::getCanonicalName);
    }
    /* Boxing and unboxing primitives is not nice but performance is not deemed critical
       here, at a benefit of having a simple implementation of the assertion value class. */
    if (o instanceof IntPredicate) {
      Typ typ = typPredicate(o);
      assert typ != null;
      checkColAccepts(colTyp, typ);
      return new TableUtils.AssertionVal(colTyp == Typ.ANY ? typ : colTyp,
          (Object i) -> ((IntPredicate) o).test((Integer) i),
          IntPredicate.class::getCanonicalName);
    }
    if (o instanceof LongPredicate) {
      Typ typ = typPredicate(o);
      assert typ != null;
      checkColAccepts(colTyp, typ);
      return new TableUtils.AssertionVal(colTyp == Typ.ANY ? typ : colTyp,
          (Object l) -> ((LongPredicate) o).test((Long) l),
          LongPredicate.class::getCanonicalName);
    }
    if (o instanceof DoublePredicate) {
      Typ typ = typPredicate(o);
      assert typ != null;
      checkColAccepts(colTyp, typ);
      return new TableUtils.AssertionVal(colTyp == Typ.ANY ? typ : colTyp,
          (Object d) -> ((DoublePredicate) o).test((Double) d),
          LongPredicate.class::getCanonicalName);
    }
    if (o instanceof Pattern) {
      Typ typ = typPredicate(o);
      assert typ != null;
      checkColAccepts(colTyp, typ);
      return new TableUtils.AssertionVal(colTyp == Typ.ANY ? typ : colTyp,
          (Object cs) -> ((Pattern) o).matcher((CharSequence) cs).matches(),
          o::toString);
    }
    return null;
  }

  static void checkCols(@NonNull List<? extends Tab.Col> cols) {
    Objects.requireNonNull(cols, "Columns are null");
    Iterator<? extends Tab.Col> iter = cols.iterator();
    if (!iter.hasNext()) {
      throw new IllegalStateException("No columns defined");
    }
  }

  @NonNull
  public static <T, U> List<U> paired(@NonNull Iterable<T> expected, @NonNull Iterable<T> actual,
                                      @NonNull BiFunction<T, T, U> mapper) {
    List<U> result = new ArrayList<>();
    Iterator<T> expIter = expected.iterator();
    Iterator<T> actIter = actual.iterator();
    while (true) {
      boolean expHasNext = expIter.hasNext();
      boolean actHasNext = actIter.hasNext();
      if (!expHasNext && !actHasNext) {
        break;
      }
      if (!expHasNext) {
        throw new IllegalStateException("actual has more elements than expected");
      }
      if (!actHasNext) {
        throw new IllegalStateException("expected has more elements than actual");
      }
      result.add(mapper.apply(expIter.next(), actIter.next()));
    }
    return result;
  }

  @NonNull
  public static <T, U> List<U> pairedSparsely(
      @NonNull Iterable<T> expected, @NonNull Iterable<T> actual,
      @NonNull BiFunction<Optional<T>, Optional<T>, U> mapper) {
    List<U> result = new ArrayList<>();
    Iterator<T> expIter = expected.iterator();
    Iterator<T> actIter = actual.iterator();
    while (true) {
      boolean expHasNext = expIter.hasNext();
      boolean actHasNext = actIter.hasNext();
      if (!expHasNext && !actHasNext) {
        break;
      }
      final Optional<T> expOpt;
      if (expHasNext) {
        expOpt = Optional.of(expIter.next());
      } else {
        expOpt = Optional.empty();
      }
      final Optional<T> actOpt;
      if (actHasNext) {
        actOpt = Optional.of(actIter.next());
      } else {
        actOpt = Optional.empty();
      }
      result.add(mapper.apply(expOpt, actOpt));
    }
    return result;
  }

  @NonNull
  public static <K, V> Map.Entry<K, V> entry(@Nullable K k, @Nullable V v) {
    return new AbstractMap.SimpleImmutableEntry<>(k, v);
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public static <V, U> boolean areBothPresent(@NonNull Optional<V> expected,
                                              @NonNull Optional<U> actual) {
    return expected.isPresent() && actual.isPresent();
  }

  private Utils() {
    throw new UnsupportedOperationException();
  }
}
