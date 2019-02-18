package io.github.nblxa.fluenttab;

import io.github.nblxa.fluenttab.assertion.AssertionValProvider;
import io.github.nblxa.fluenttab.exception.StructureException;
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
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

  static void inferTyp(Tab.Col col, Tab.Val fromVal, ListIterator<Tab.Col> iterCol) {
    if (!col.accepts(fromVal)) {
      throw new StructureException("Expected " + col.typ()
          + " but got " + fromVal.getTyp() + ".");
    } else {
      if (col.typ() == Typ.ANY && fromVal.getTyp() != Typ.ANY) {
        Tab.Col inferredCol = new InferredTypDecoratorCol(col, fromVal.getTyp());
        iterCol.set(inferredCol);
      }
    }
  }

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
      typ = custom(o);
      if (typ != null) {
        return typ;
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(illegalTypeMessage(o), e);
    }
    throw new IllegalArgumentException(illegalTypeMessage(o));
  }

  private static String illegalTypeMessage(Object o) {
    return "Unsupported object type: " + o.getClass();
  }

  static Tab.Val getVal(Object o, Typ typ) {
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
        val = valCustom(o);
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

  private static Typ standard(Object o) {
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

  private static Typ supplier(Object o) {
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

  private static Typ predicate(Object o) {
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

  private static Tab.Val valPredicate(Object o) {
    if (o instanceof Predicate) {
      return new TableUtils.SimpleAssertionVal(Typ.ANY, ((Predicate) o)::test,
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

  private static Typ custom(Object o) {
    Tab.Val val = valCustom(o);
    if (val instanceof AbstractTab.AbstractAssertionVal) {
      return AbstractTab.AbstractAssertionVal.TYP;
    }
    if (val != null) {
      return val.getTyp();
    }
    return null;
  }

  private static Tab.Val valCustom(Object o) {
    if (o != null) {
      AssertionValProvider provider = assertionValProviderForClass(o.getClass())
          .orElseThrow(() -> new IllegalStateException(
              "No provider found for class: " + o.getClass().getCanonicalName()));
      return provider.assertionVal(o);
    }
    return null;
  }

  private static Optional<AssertionValProvider> assertionValProviderForClass(Class<?> klass) {
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

  static void checkCols(Iterable<? extends Tab.Col> cols) {
    Objects.requireNonNull(cols, "Columns are null");
    Iterator<? extends Tab.Col> iter = cols.iterator();
    if (!iter.hasNext()) {
      throw new IllegalStateException("No columns defined");
    }
    Tab.Col first = iter.next();
    boolean named = first instanceof TableUtils.NamedCol;
    int i = 0;
    while (iter.hasNext()) {
      i++;
      Tab.Col col = iter.next();
      if ((!named && col instanceof TableUtils.NamedCol)
          || (named && !(col instanceof TableUtils.NamedCol))) {
        throw new IllegalArgumentException(
            "Mixing named and unnamed columns at position #" + i);
      }
    }
  }

  public static <T> ArrayList<T> toArrayList(Iterable<T> iterable) {
    Iterator<T> iterator = iterable.iterator();
    ArrayList<T> collection = new ArrayList<>();
    iterator.forEachRemaining(collection::add);
    return collection;
  }

  public static <T> List<T> asList(Iterable<T> iterable) {
    if (iterable instanceof List) {
      return (List<T>) iterable;
    } else {
      return new IterableListDecorator<>(iterable);
    }
  }

  public static <T, U> Iterable<U> paired(Iterable<T> expected, Iterable<T> actual,
                                          BiFunction<T, T, U> mapper) {
    Iterator<T> expectedIter = expected.iterator();
    Iterator<T> actualIter = actual.iterator();
    return () -> new Iterator<U>() {
      @Override
      public boolean hasNext() {
        boolean expectedHasNext = expectedIter.hasNext();
        boolean actualHasNext = actualIter.hasNext();
        if (expectedHasNext && actualHasNext) {
          return true;
        }
        if (!(expectedHasNext || actualHasNext)) {
          return false;
        }
        throw new IllegalStateException("expectedHasNext=" + expectedHasNext
            + " actualHasNext=" + actualHasNext);
      }

      @Override
      public U next() {
        return mapper.apply(expectedIter.next(), actualIter.next());
      }
    };
  }

  public static <K, V> Map.Entry<K, V> entry(K k, V v) {
    return new AbstractMap.SimpleImmutableEntry<>(k, v);
  }

  public static <V, U> boolean areBothPresent(Optional<V> expected, Optional<U> actual) {
    return expected.isPresent() && actual.isPresent();
  }

  public static <T> Stream<T> stream(Iterable<T> iterable) {
    Spliterator<T> spliterator =
        Spliterators.spliteratorUnknownSize(iterable.iterator(), Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false);
  }

  private static class InferredTypDecoratorCol extends AbstractTab.AbstractCol {
    private final Tab.Col decoratedCol;

    private InferredTypDecoratorCol(@NonNull Tab.Col decoratedColTypAny, Typ typ) {
      super(typ, decoratedColTypAny.isKey());
      if (decoratedColTypAny.typ() != Typ.ANY) {
        throw new IllegalArgumentException("Expected " + Typ.ANY
            + " but got " + decoratedColTypAny.typ());
      }
      this.decoratedCol = Objects.requireNonNull(decoratedColTypAny, "decoratedColTypAny");
    }

    @NonNull
    @Override
    public Tab.Val valOf(@Nullable Object o) {
      return decoratedCol.valOf(o);
    }
  }

  private Utils() {
    throw new UnsupportedOperationException();
  }
}
