package io.github.nblxa.turntables;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.nblxa.turntables.exception.AssertionEvaluationException;
import io.github.nblxa.turntables.exception.StructureException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class TableUtils {
  private static final String COL = "col";

  static final class SimpleCol extends AbstractTab.AbstractCol {
    public SimpleCol(String name, Typ typ, boolean isKey) {
      super(name, typ, isKey);
    }
  }

  static final class SimpleVal extends AbstractTab.AbstractVal {
    @NonNull
    private final Typ typ;
    @NonNull
    private final Object obj;

    SimpleVal(Typ typ, Object obj) {
      this.typ = Objects.requireNonNull(typ, "typ");
      this.obj = Objects.requireNonNull(obj, "obj");
    }

    @NonNull
    @Override
    public Typ typ() {
      return typ;
    }

    @NonNull
    @Override
    public Object evaluate() {
      return obj;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof SimpleVal)) {
        return false;
      }
      SimpleVal simpleVal = (SimpleVal) o;
      return typ == simpleVal.typ &&
          obj.equals(simpleVal.obj);
    }

    @Override
    public int hashCode() {
      return Objects.hash(typ, obj);
    }

    @Override
    public boolean canEqual(Object o) {
      return o instanceof SimpleVal;
    }
  }

  static final class SimpleRow extends AbstractTab.AbstractRow {
    @NonNull
    private final List<Tab.Val> vals;

    SimpleRow(List<Tab.Col> cols, List<Tab.Val> vals) {
      super(cols);
      this.vals = Objects.requireNonNull(vals, "vals is null");
    }

    @NonNull
    @Override
    public List<Tab.Val> vals() {
      return vals;
    }
  }

  static final class SuppVal extends AbstractTab.AbstractVal {
    @NonNull
    private final Typ typ;
    @NonNull
    private final Supplier<?> supp;
    private final AtomicBoolean suppCalled;
    private Object obj;
    private volatile boolean inited;

    SuppVal(@NonNull Typ typ, @NonNull Supplier<?> supp) {
      this.typ = Objects.requireNonNull(typ, "typ");
      this.supp = Objects.requireNonNull(supp, "supp");
      this.obj = null;
      this.inited = false;
      this.suppCalled = new AtomicBoolean(false);
    }

    @Override
    @NonNull
    public Typ typ() {
      return typ;
    }

    /**
     * Thread-safe lazy evaluation of the Supplier.
     *
     * <p>
     * Modifications of this method must be tested for thread safety:
     * see io.github.nblxa.turntables.TestSupplierValConcurrency
     *
     * @return the value of the supplier executed exactly once.
     */
    @Override
    @Nullable
    public Object evaluate() {
      boolean tryAgain;
      Object val;
      do {
        if (inited) {
          return obj;
        } else {
          boolean suppCalledInThisThread = false;
          if (suppCalled.compareAndSet(false, true)) {
            // calling alien code outside of the synchronized block
            // to prevent locking issues
            val = supp.get();
            suppCalledInThisThread = true;
          } else {
            val = null;
          }
          synchronized (this) {
            if (inited) {
              return obj;
            } else {
              if (suppCalledInThisThread) {
                obj = val;
                inited = true;
                tryAgain = false;
              } else {
                tryAgain = true;
              }
            }
          }
        }
      } while (tryAgain);
      return val;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof SuppVal)) {
        return false;
      }
      SuppVal suppVal = (SuppVal) o;
      return typ == suppVal.typ &&
          supp.equals(suppVal.supp);
    }

    @Override
    public int hashCode() {
      return Objects.hash(typ(), supp);
    }

    @Override
    public boolean canEqual(Object o) {
      return o instanceof SuppVal;
    }

    @Override
    public String toString() {
      return renderer().renderVal(this);
    }
  }

  public static final class AssertionVal extends AbstractTab.AbstractVal {
    @NonNull
    private final Typ typ;
    @NonNull
    private final Predicate<Object> assertionPredicate;
    @NonNull
    private final Supplier<? extends CharSequence> toStringSupplier;

    AssertionVal(@NonNull Typ typ, @NonNull Predicate<Object> assertionPredicate,
                 @NonNull Supplier<? extends CharSequence> toStringSupplier) {
      this.typ = Objects.requireNonNull(typ, "typ");
      this.assertionPredicate =
          Objects.requireNonNull(assertionPredicate, "assertionPredicate");
      this.toStringSupplier =
          Objects.requireNonNull(toStringSupplier, "toStringSupplier");
    }

    @Override
    @NonNull
    public Typ typ() {
      return typ;
    }

    @Override
    @Nullable
    public Object evaluate() {
      throw new AssertionEvaluationException();
    }

    @Override
    public boolean matchesActual(@NonNull Tab.Val actual) {
      Objects.requireNonNull(actual, "other");
      return assertionPredicate.test(actual.evaluateAs(typ));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof AssertionVal)) {
        return false;
      }
      AssertionVal that = (AssertionVal) o;
      return that.canEqual(this) && typ.equals(that.typ)
          && assertionPredicate.equals(that.assertionPredicate)
          && toStringSupplier.equals(that.toStringSupplier);
    }

    @Override
    public int hashCode() {
      return Objects.hash(typ, assertionPredicate, toStringSupplier);
    }

    @Override
    public boolean canEqual(Object o) {
      return o instanceof AssertionVal;
    }

    @Override
    public String toString() {
      return toStringSupplier.get()
          .toString();
    }
  }

  public abstract static class AbstractColRowAdderTable extends AbstractTab
      implements Tab.RowAdder<RowAdderTable>, Tab.ColAdderRowAdderPart<RowAdderTable> {
    @Override
    @NonNull
    public RowAdderTable row(@Nullable Object first, @NonNull Object... rest) {
      Object[] objects = Utils.prepend(first, rest);
      Utils.checkObjects(objects);
      List<Col> schema;
      if (cols().iterator().hasNext()) {
        schema = cols();
      } else {
        schema = makeCols(objects);
      }
      return new RowAdderTable(schema).row(first, rest);
    }

    @Override
    @NonNull
    public List<Row> rows() {
      return Collections.emptyList();
    }

    @NonNull
    @Override
    public Tab tab() {
      return this;
    }

    @NonNull
    @Override
    public RowAdderTable rowAdder() {
      return new RowAdderTable(cols());
    }
  }

  public static final class ColAdderTable extends AbstractColRowAdderTable
      implements Tab.ColAdder<UnnamedColAdderTable, NamedColAdderTable, RowAdderTable> {

    @Override
    @NonNull
    public NamedColAdderTable col(@NonNull String name, @NonNull Typ typ) {
      return new NamedColAdderTable().col(name, typ);
    }

    @Override
    @NonNull
    public NamedColAdderTable key(@NonNull String name, @NonNull Typ typ) {
      return new NamedColAdderTable().key(name, typ);
    }

    @Override
    @NonNull
    public UnnamedColAdderTable col(@NonNull Typ typ) {
      return new UnnamedColAdderTable().col(typ);
    }

    @Override
    @NonNull
    public UnnamedColAdderTable key(@NonNull Typ typ) {
      return new UnnamedColAdderTable().key(typ);
    }

    @NonNull
    @Override
    public RowAdderTable rowAdder() {
      return new RowAdderTable(cols());
    }
  }

  @SuppressWarnings("java:S2160")
  public static final class UnnamedColAdderTable extends AbstractColRowAdderTable
      implements Tab.UnnamedColAdder<UnnamedColAdderTable, RowAdderTable> {
    private int colIndex = 1;

    @Override
    @NonNull
    public UnnamedColAdderTable col(@NonNull Typ typ) {
      Objects.requireNonNull(typ, "typ");
      mutableCols().add(new SimpleCol(COL + (colIndex++), typ, false));
      return this;
    }

    @Override
    @NonNull
    public UnnamedColAdderTable key(@NonNull Typ typ) {
      Objects.requireNonNull(typ, "typ");
      mutableCols().add(new SimpleCol(COL + (colIndex++), typ, true));
      return this;
    }

    @NonNull
    @Override
    public RowAdderTable rowAdder() {
      return new RowAdderTable(cols());
    }
  }

  @SuppressWarnings("java:S2160")
  public static final class NamedColAdderTable extends AbstractColRowAdderTable
      implements Tab.NamedColAdder<NamedColAdderTable, RowAdderTable> {

    @Override
    @NonNull
    public NamedColAdderTable col(@NonNull String name, @NonNull Typ typ) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(typ, "typ");
      mutableCols().add(new SimpleCol(name, typ, false));
      return this;
    }

    @Override
    @NonNull
    public NamedColAdderTable key(@NonNull String name, @NonNull Typ typ) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(typ, "typ");
      mutableCols().add(new SimpleCol(name, typ, true));
      return this;
    }
  }

  public static final class RowAdderTable extends AbstractTab
      implements Tab.RowAdder<RowAdderTable> {

    @NonNull
    private final List<Tab.Row> mutableRows = new ArrayList<>();
    @NonNull
    private final List<Tab.Row> rows = Collections.unmodifiableList(mutableRows);

    public RowAdderTable(List<Col> cols) {
      super(cols);
    }

    @Override
    @NonNull
    public RowAdderTable row(@Nullable Object first, @NonNull Object... rest) {
      Object[] objects = Utils.prepend(first, rest);
      addRow(objects);
      return this;
    }

    @Override
    @NonNull
    public List<Row> rows() {
      return rows;
    }

    @Override
    @NonNull
    public Tab tab() {
      return this;
    }

    private List<Tab.Val> getVals(Object[] objects) {
      return inferCols(mutableCols(), objects);
    }

    private void addRow(Object[] objects) {
      Utils.checkObjects(objects);
      List<Tab.Col> cols = mutableCols();
      if (cols.isEmpty()) {
        cols.addAll(makeCols(objects));
        Utils.checkCols(cols);
      }
      List<Tab.Val> vals = getVals(objects);
      this.mutableRows.add(new SimpleRow(cols(), vals));
    }
  }

  public static final class Builder {
    @NonNull
    public static NamedColBuilder named() {
      return new NamedColBuilder();
    }

    @NonNull
    public static RowBuilder rowBuilder() {
      return new RowBuilder(Collections.emptyList());
    }

    private Builder() {
    }
  }

  public static final class NamedColBuilder implements Tab.NamedColAdder<NamedColBuilder,
        RowBuilder> {
    private final List<Tab.Col> cols;

    public NamedColBuilder() {
      cols = new ArrayList<>();
    }

    @NonNull
    @Override
    public NamedColBuilder col(@NonNull String name, @NonNull Typ typ) {
      Objects.requireNonNull(name);
      Objects.requireNonNull(typ);
      cols.add(new SimpleCol(name, typ, false));
      return this;
    }

    @NonNull
    @Override
    public NamedColBuilder key(@NonNull String name, @NonNull Typ typ) {
      Objects.requireNonNull(name);
      Objects.requireNonNull(typ);
      cols.add(new SimpleCol(name, typ, true));
      return this;
    }

    @NonNull
    @Override
    public RowBuilder rowAdder() {
      return new RowBuilder(cols);
    }
  }

  public static final class RowBuilder implements Tab.RowAdder<RowBuilder> {
    private final List<Tab.Col> cols;
    private final List<Tab.Row> rows;

    private RowBuilder(List<Tab.Col> cols) {
      this.cols = new ArrayList<>(Objects.requireNonNull(cols));
      this.rows = new ArrayList<>();
    }

    @NonNull
    @Override
    public RowBuilder row(@Nullable Object first, @NonNull Object... rest) {
      Objects.requireNonNull(rest);
      return row(Utils.prepend(first, rest));
    }

    @NonNull
    public RowBuilder row(@NonNull Object[] values) {
      Objects.requireNonNull(values);
      if (cols.isEmpty()) {
        cols.addAll(makeCols(values));
      }
      List<Tab.Val> vals = inferCols(cols, values);
      rows.add(new SimpleRow(cols, vals));
      return this;
    }

    @NonNull
    @Override
    public Tab tab() {
      return new FixedTable(cols, rows);
    }
  }

  public static final class FixedTable extends AbstractTab {
    @NonNull
    private final List<Row> rows;

    private FixedTable(@NonNull List<Col> cols, @NonNull List<Row> rows) {
      super(cols);
      this.rows = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(rows)));
    }

    @NonNull
    @Override
    public List<Row> rows() {
      return rows;
    }
  }

  @NonNull
  private static List<Tab.Col> makeCols(@NonNull Object[] objects) {
    List<Tab.Col> cols = new ArrayList<>();
    int colIndex = 1;
    for (Object o : objects) {
      Typ typ = Utils.getTyp(o);
      cols.add(new SimpleCol(COL + (colIndex++), typ, false));
    }
    return cols;
  }

  private static List<Tab.Val> inferCols(List<Tab.Col> cols, Object[] objects) {
    Objects.requireNonNull(objects);
    Iterator<Object> iterObj = Arrays.asList(objects).iterator();
    ListIterator<Tab.Col> iterCol = cols.listIterator();
    int i = 0;
    List<Tab.Val> vals = new ArrayList<>();
    while (iterCol.hasNext() && iterObj.hasNext()) {
      i++;
      Tab.Col col = iterCol.next();
      Object o = iterObj.next();
      Tab.Val val;
      try {
        val = o != null ? Utils.getVal(o) : col.typ().nullVal();
        Utils.inferTyp(col, val, iterCol);
      } catch (Exception e) {
        throw new StructureException("Error at position #" + i, e);
      }
      vals.add(val);
    }
    if (iterCol.hasNext()) {
      throw new StructureException("Missing value at position #" + (i + 1));
    }
    if (iterObj.hasNext()) {
      throw new StructureException("Column not defined for position #" + (i + 1));
    }
    return vals;
  }

  public static boolean hasNamedCols(@NonNull Tab tab) {
    List<Tab.Col> cols = tab.cols();
    for (int i = 0; i < cols.size(); i++) {
      if (!(COL + (i + 1)).equals(cols.get(i).name())) {
        return true;
      }
    }
    return false;
  }

  @NonNull
  public static List<String> colNames(Tab tab) {
    List<String> colNames = new ArrayList<>();
    for (Tab.Col c: tab.cols()) {
      colNames.add(c.name());
    }
    return Collections.unmodifiableList(colNames);
  }

  private TableUtils() {
  }
}
