package io.github.nblxa.turntables;

import io.github.nblxa.turntables.assertion.AssertionValProvider;
import io.github.nblxa.turntables.exception.StructureException;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
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
import java.util.ServiceLoader;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.Predicate;

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
    if (!col.typ().accepts(fromVal.typ())) {
      throw new StructureException("Expected " + col.typ()
          + " but got " + fromVal.typ() + ".");
    } else {
      if (col.typ() == Typ.ANY && fromVal.typ() != Typ.ANY) {
        final Tab.Col inferredCol = new TableUtils.SimpleCol(col.name(), fromVal.typ(), col.isKey());
        iterCol.set(inferredCol);
      }
    }
  }

  @NonNull
  static Typ getTyp(Object o) {
    Typ typ;
    typ = standard(o);
    if (typ != null) {
      return typ;
    }
    typ = supplier(o);
    if (typ != null) {
      return typ;
    }
    typ = predicate(o);
    if (typ != null) {
      return typ;
    }
    try {
      typ = assertion(o);
      if (typ != null) {
        return typ;
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(illegalTypeMessage(o), e);
    }
    throw new IllegalArgumentException(illegalTypeMessage(o));
  }

  @NonNull
  private static String illegalTypeMessage(@NonNull Object o) {
    return "Unsupported object type: " + o.getClass();
  }

  @NonNull
  static Tab.Val getVal(@Nullable Object o, @NonNull Typ typ) {
    if (o == null) {
      return typ.nullVal();
    }
    Tab.Val val = valStandard(o);
    if (val == null) {
      val = valSupplier(o);
    }
    if (val == null) {
      val = valPredicate(o);
    }
    if (val == null) {
      try {
        val = valAssertion(o);
      } catch (Exception e) {
        throw new IllegalArgumentException(illegalTypeMessage(o), e);
      }
    }
    if (val == null) {
      throw new IllegalArgumentException(illegalTypeMessage(o));
    } else {
      return val;
    }
  }

  @Nullable
  private static Typ standard(@Nullable Object o) {
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
    if (o instanceof Boolean) {
      return Typ.BOOLEAN;
    }
    if (o instanceof CharSequence) {
      return Typ.STRING;
    }
    if (o instanceof LocalDate) {
      return Typ.DATE;
    }
    if (o instanceof LocalDateTime) {
      return Typ.DATETIME;
    }
    if (o instanceof java.util.Date) {
      return Typ.DATETIME;
    }
    return null;
  }

  @Nullable
  private static Tab.Val valStandard(Object o) {
    if (o == null) {
      return Typ.ANY.nullVal();
    }
    Typ typ = standard(o);
    if (typ == null) {
      return null;
    } else {
      return new TableUtils.SimpleVal(typ, o);
    }
  }

  @Nullable
  private static Typ supplier(@NonNull Object o) {
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
  private static Tab.Val valSupplier(Object o) {
    if (o instanceof IntSupplier) {
      return new TableUtils.SuppVal(Typ.INTEGER, ((IntSupplier) o)::getAsInt);
    }
    if (o instanceof LongSupplier) {
      return new TableUtils.SuppVal(Typ.LONG, ((LongSupplier) o)::getAsLong);
    }
    if (o instanceof DoubleSupplier) {
      return new TableUtils.SuppVal(Typ.DOUBLE, ((DoubleSupplier) o)::getAsDouble);
    }
    if (o instanceof BooleanSupplier) {
      return new TableUtils.SuppVal(Typ.BOOLEAN, ((BooleanSupplier) o)::getAsBoolean);
    }
    return null;
  }

  @Nullable
  private static Typ predicate(@NonNull Object o) {
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
    return null;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  private static Tab.Val valPredicate(Object o) {
    if (o instanceof Predicate<?>) {
      return new TableUtils.SimpleAssertionVal(Typ.ANY, ((Predicate<Object>) o),
          Predicate.class::getCanonicalName);
    }
        /* Boxing and unboxing primitives is not nice but performance is not deemed critical
           here, at a benefit of having a simple implementation of the assertion value class. */
    if (o instanceof IntPredicate) {
      return new TableUtils.SimpleAssertionVal(Typ.INTEGER,
          (Object i) -> ((IntPredicate) o).test((Integer) i),
          IntPredicate.class::getCanonicalName);
    }
    if (o instanceof LongPredicate) {
      return new TableUtils.SimpleAssertionVal(Typ.LONG,
          (Object l) -> ((LongPredicate) o).test((Long) l),
          LongPredicate.class::getCanonicalName);
    }
    if (o instanceof DoublePredicate) {
      return new TableUtils.SimpleAssertionVal(Typ.DOUBLE,
          (Object d) -> ((DoublePredicate) o).test((Double) d),
          LongPredicate.class::getCanonicalName);
    }
    return null;
  }

  @Nullable
  private static Typ assertion(@NonNull Object o) {
    Tab.Val val = valAssertion(o);
    if (val != null) {
      return val.typ();
    }
    return null;
  }

  @Nullable
  private static Tab.Val valAssertion(@NonNull Object o) {
    return assertionValProviderForClass(o.getClass())
        .map(op -> op.assertionVal(o))
        .orElse(null);
  }

  @NonNull
  private static Optional<AssertionValProvider> assertionValProviderForClass(
      @NonNull Class<?> klass) {
    ServiceLoader<AssertionValProvider> loader = ServiceLoader.load(AssertionValProvider.class);
    AssertionValProvider res = null;
    for (AssertionValProvider prov : loader) {
      if (prov != null && prov.getAssertionClass().isAssignableFrom(klass)) {
        if (res == null) {
          res = prov;
        } else {
          throw new IllegalStateException("More than one provider found for class: "
              + klass.getCanonicalName());
        }
      }
    }
    return Optional.ofNullable(res);
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
