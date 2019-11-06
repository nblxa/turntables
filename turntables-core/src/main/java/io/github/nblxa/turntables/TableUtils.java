package io.github.nblxa.turntables;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
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
  static class SimpleCol extends AbstractTab.AbstractCol {
    public SimpleCol(Typ typ, boolean isKey) {
      super(typ, isKey);
    }
  }

  static final class NamedCol extends SimpleCol implements Tab.Named {
    @NonNull
    private final String name;

    private NamedCol(String name, Typ typ, boolean isKey) {
      super(typ, isKey);
      this.name = Objects.requireNonNull(name);
    }

    @Override
    @NonNull
    public String name() {
      return name;
    }

    @Override
    public boolean canEqual(Object o) {
      return (o instanceof NamedCol);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof NamedCol)) {
        return false;
      }
      if (!super.equals(o)) {
        return false;
      }
      NamedCol namedCol = (NamedCol) o;
      return namedCol.canEqual(this) && name.equals(namedCol.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), name);
    }

    @Override
    public String toString() {
      return name + ": " + typ();
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

    @Override
    @NonNull
    public Typ getTyp() {
      return typ;
    }

    @Override
    @NonNull
    public Object eval() {
      return obj;
    }

    @Override
    public boolean matchesActual(@NonNull Tab.Val other) {
      Objects.requireNonNull(other, "other");
      return eval().equals(other.eval());
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

    @Override
    public String toString() {
      return YamlUtils.renderVal(this);
    }
  }

  static class SimpleRow extends AbstractTab.AbstractRow {
    SimpleRow(Iterable<Tab.Col> cols, Iterable<Tab.Val> vals) {
      super(cols, vals);
    }

    @Override
    public final boolean equals(Object o) {
      return super.equals(o);
    }

    @Override
    public final int hashCode() {
      return super.hashCode();
    }

    @Override
    public final boolean canEqual(Object o) {
      return super.canEqual(o);
    }
  }

  static final class SuppVal extends AbstractTab.AbstractVal {
    @NonNull
    private final Typ typ;
    @NonNull
    private final Supplier supp;
    private final transient AtomicBoolean suppCalled;
    private transient volatile Object obj;
    private transient volatile boolean inited;

    SuppVal(Typ typ, Supplier supp) {
      this.typ = Objects.requireNonNull(typ, "typ");
      this.supp = Objects.requireNonNull(supp, "supp");
      this.obj = null;
      this.inited = false;
      this.suppCalled = new AtomicBoolean(false);
    }

    @Override
    @NonNull
    public Typ getTyp() {
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
    public Object eval() {
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
    public boolean matchesActual(@NonNull Tab.Val other) {
      return Objects.equals(eval(), other.eval());
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
      return Objects.hash(getTyp(), supp);
    }

    @Override
    public boolean canEqual(Object o) {
      return o instanceof SuppVal;
    }

    @Override
    public String toString() {
      return YamlUtils.renderVal(this);
    }
  }

  static final class SimpleAssertionVal extends AbstractTab.AbstractAssertionVal {
    @NonNull
    private final Typ typ;

    SimpleAssertionVal(Typ typ, Predicate<Object> assertionPredicate,
                       Supplier<String> toStringSupplier) {
      super(assertionPredicate, toStringSupplier);
      this.typ = Objects.requireNonNull(typ, "typ");
    }

    @Override
    @NonNull
    public Typ getTyp() {
      return typ;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof SimpleAssertionVal)) {
        return false;
      }
      if (!super.equals(o)) {
        return false;
      }
      SimpleAssertionVal that = (SimpleAssertionVal) o;
      return that.canEqual(this) && typ == that.typ && super.equals(that);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), typ);
    }

    @Override
    public boolean canEqual(Object o) {
      return o instanceof SimpleAssertionVal;
    }
  }

  public static final class ColAdder extends AbstractTab
      implements Tab.ColAdder, Tab.RowAdder<RowAdderTable> {

    public ColAdder() {
      super();
    }

    @Override
    @NonNull
    public TableUtils.ColAdder col(@NonNull String name, @NonNull Typ typ) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(typ, "typ");
      colsList().add(new NamedCol(name, typ, false));
      return this;
    }

    @Override
    @NonNull
    public TableUtils.ColAdder col(@NonNull Typ typ) {
      Objects.requireNonNull(typ, "typ");
      colsList().add(new SimpleCol(typ, false));
      return this;
    }

    @Override
    @NonNull
    public TableUtils.ColAdder key(@NonNull String name, @NonNull Typ typ) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(typ, "typ");
      colsList().add(new NamedCol(name, typ, true));
      return this;
    }

    @Override
    @NonNull
    public TableUtils.ColAdder key(@NonNull Typ typ) {
      Objects.requireNonNull(typ, "typ");
      colsList().add(new SimpleCol(typ, true));
      return this;
    }

    @Override
    @NonNull
    public RowAdderTable row(@Nullable Object first, @NonNull Object... rest) {
      Object[] objects = Utils.prepend(first, rest);
      Utils.checkObjects(objects);
      List<Col> schema;
      if (cols().iterator().hasNext()) {
        schema = colsList();
      } else {
        schema = makeCols(objects);
      }
      return new RowAdderTable(schema).row(first, rest);
    }

    @Override
    @NonNull
    public Iterable<Row> rows() {
      return Collections.emptyList();
    }
  }

  public static final class RowAdderTable extends AbstractTab
      implements Tab.RowAdder<RowAdderTable> {

    @NonNull
    private final List<Tab.Row> rows = new ArrayList<>();
    @NonNull
    private final List<Tab.Row> unmodifiableRows = Collections.unmodifiableList(rows);

    public RowAdderTable(Iterable<Col> cols) {
      super(cols);
      Utils.checkCols(cols);
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
    public Iterable<Row> rows() {
      return unmodifiableRows;
    }

    private List<Tab.Val> getVals(Object[] objects) {
      return inferCols(colsList(), objects);
    }

    private void addRow(Object[] objects) {
      Utils.checkObjects(objects);
      List<Tab.Val> vals = getVals(objects);
      this.rows.add(new SimpleRow(cols(), vals));
    }
  }

  public static final class Builder implements Tab.ColAdder {
    private final List<Tab.Col> cols;

    public Builder() {
      cols = new ArrayList<>();
    }

    @NonNull
    @Override
    public Builder col(@NonNull Typ typ) {
      Objects.requireNonNull(typ);
      cols.add(new SimpleCol(typ, false));
      return this;
    }

    @NonNull
    @Override
    public Builder col(@NonNull String name, @NonNull Typ typ) {
      Objects.requireNonNull(name);
      Objects.requireNonNull(typ);
      cols.add(new NamedCol(name, typ, false));
      return this;
    }

    @NonNull
    @Override
    public Builder key(@NonNull String name, @NonNull Typ typ) {
      Objects.requireNonNull(name);
      Objects.requireNonNull(typ);
      cols.add(new NamedCol(name, typ, true));
      return this;
    }

    @NonNull
    @Override
    public Builder key(@NonNull Typ typ) {
      Objects.requireNonNull(typ);
      cols.add(new SimpleCol(typ, true));
      return this;
    }

    @NonNull
    public RowBuilder rowBuilder() {
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
    public Tab build() {
      return new FixedTable(cols, rows);
    }
  }

  public static final class FixedTable extends AbstractTab {
    @NonNull
    private final Iterable<Row> rows;

    private FixedTable(@NonNull Iterable<Col> cols, @NonNull Iterable<Row> rows) {
      super(cols);
      this.rows = Collections.unmodifiableList(Utils.toArrayList(Objects.requireNonNull(rows)));
    }

    @NonNull
    @Override
    public Iterable<Row> rows() {
      return rows;
    }
  }

  @NonNull
  private static List<Tab.Col> makeCols(@NonNull Object[] objects) {
    List<Tab.Col> cols = new ArrayList<>();
    for (Object o : objects) {
      Typ typ = Utils.getTyp(o);
      cols.add(new SimpleCol(typ, false));
    }
    return cols;
  }

  private static List<Tab.Val> inferCols(List<Tab.Col> colsList, Object[] objects) {
    Objects.requireNonNull(objects);
    Iterator<Object> iterObj = Arrays.asList(objects).iterator();
    ListIterator<Tab.Col> iterCol = colsList.listIterator();
    int i = 0;
    List<Tab.Val> vals = new ArrayList<>();
    while (iterCol.hasNext() && iterObj.hasNext()) {
      i++;
      Tab.Col col = iterCol.next();
      Object o = iterObj.next();
      Tab.Val val;
      try {
        val = col.valOf(o);
        Utils.inferTyp(col, val, iterCol);
      } catch (Exception e) {
        throw new StructureException("Error at position #" + i, e);
      }
      vals.add(val);
    }
    if (iterCol.hasNext()) {
      throw new StructureException("Missing object at position #" + (i + 1));
    }
    if (iterObj.hasNext()) {
      throw new StructureException("Column not defined for position #" + (i + 1));
    }
    return vals;
  }

  private TableUtils() {
    throw new UnsupportedOperationException();
  }
}
