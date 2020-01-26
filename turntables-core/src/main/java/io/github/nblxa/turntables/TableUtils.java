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
  static final class SimpleCol extends AbstractTab.AbstractCol {
    public SimpleCol(Typ typ, boolean isKey, int index) {
      super(typ, isKey, "col" + (index + 1));
    }
  }

  static final class NamedCol extends AbstractTab.AbstractCol implements Tab.Named {
    private NamedCol(String name, Typ typ, boolean isKey) {
      super(typ, isKey, name);
    }

    @NonNull
    @Override
    public String givenName() {
      return name();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // checks via the canEqual method
    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!canEqual(o)) {
        return false;
      }
      AbstractTab.AbstractCol abstractCol = (AbstractTab.AbstractCol) o;
      return abstractCol.canEqual(this)
          && typ() == abstractCol.typ()
          && isKey() == abstractCol.isKey()
          && Objects.equals(name(), abstractCol.name());
    }

    @Override
    public boolean canEqual(Object o) {
      return o instanceof AbstractTab.AbstractCol && o instanceof Tab.Named;
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
    private final Supplier<?> supp;
    private final transient AtomicBoolean suppCalled;
    private transient volatile Object obj;
    private transient volatile boolean inited;

    SuppVal(Typ typ, Supplier<?> supp) {
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

  public static abstract class AbstractColRowAdderTable extends AbstractTab
      implements Tab.RowAdder<RowAdderTable>, Tab.ColAdderRowAdderPart<RowAdderTable> {
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

  public static final class UnnamedColAdderTable extends AbstractColRowAdderTable
      implements Tab.UnnamedColAdder<UnnamedColAdderTable, RowAdderTable> {

    @Override
    @NonNull
    public UnnamedColAdderTable col(@NonNull Typ typ) {
      Objects.requireNonNull(typ, "typ");
      colsList().add(new SimpleCol(typ, false, colsList().size()));
      return this;
    }

    @Override
    @NonNull
    public UnnamedColAdderTable key(@NonNull Typ typ) {
      Objects.requireNonNull(typ, "typ");
      colsList().add(new SimpleCol(typ, true, colsList().size()));
      return this;
    }

    @NonNull
    @Override
    public RowAdderTable rowAdder() {
      return new RowAdderTable(cols());
    }
  }

  public static final class NamedColAdderTable extends AbstractColRowAdderTable
      implements Tab.NamedColAdder<NamedColAdderTable, RowAdderTable> {

    @Override
    @NonNull
    public NamedColAdderTable col(@NonNull String name, @NonNull Typ typ) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(typ, "typ");
      colsList().add(new NamedCol(name, typ, false));
      return this;
    }

    @Override
    @NonNull
    public NamedColAdderTable key(@NonNull String name, @NonNull Typ typ) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(typ, "typ");
      colsList().add(new NamedCol(name, typ, true));
      return this;
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

    @Override
    @NonNull
    public Tab tab() {
      return this;
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

  public static final class Builder {
    @NonNull
    public static NamedColBuilder named() {
      return new NamedColBuilder();
    }

    @NonNull
    public static UnnamedColBuilder unnamed() {
      return new UnnamedColBuilder();
    }

    @NonNull
    public static RowBuilder rowBuilder() {
      return new RowBuilder(Collections.emptyList());
    }

    private Builder() {
    }
  }

  public static final class UnnamedColBuilder implements Tab.UnnamedColAdder<UnnamedColBuilder,
      RowBuilder> {
    private final List<Tab.Col> cols;

    public UnnamedColBuilder() {
      cols = new ArrayList<>();
    }

    @NonNull
    @Override
    public UnnamedColBuilder col(@NonNull Typ typ) {
      Objects.requireNonNull(typ);
      cols.add(new SimpleCol(typ, false, cols.size()));
      return this;
    }

    @NonNull
    @Override
    public UnnamedColBuilder key(@NonNull Typ typ) {
      Objects.requireNonNull(typ);
      cols.add(new SimpleCol(typ, true, cols.size()));
      return this;
    }

    @NonNull
    @Override
    public RowBuilder rowAdder() {
      return new RowBuilder(cols);
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
      cols.add(new NamedCol(name, typ, false));
      return this;
    }

    @NonNull
    @Override
    public NamedColBuilder key(@NonNull String name, @NonNull Typ typ) {
      Objects.requireNonNull(name);
      Objects.requireNonNull(typ);
      cols.add(new NamedCol(name, typ, true));
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
    for (int i = 0; i < objects.length; i++) {
      Object o = objects[i];
      Typ typ = Utils.getTyp(o);
      cols.add(new SimpleCol(typ, false, i));
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

  public static boolean hasNamedCols(@NonNull Tab tab) {
    Objects.requireNonNull(tab, "tab is null");
    Iterable<Tab.Col> cols = Objects.requireNonNull(tab.cols(), "cols is null");
    Iterator<Tab.Col> iter = Objects.requireNonNull(cols.iterator(), "iter is null");
    Tab.Col col = Objects.requireNonNull(iter.next(), "col is null");
    return col instanceof Tab.Named;
  }

  private TableUtils() {
    throw new UnsupportedOperationException();
  }
}
