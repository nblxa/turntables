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
    public SimpleCol(Typ typ, boolean isKey) {
      super(typ, isKey);
    }
  }

  static final class SimpleNamedCol extends AbstractTab.AbstractCol implements Tab.NamedCol {
    @NonNull
    private final String name;

    SimpleNamedCol(String name, Typ typ, boolean isKey) {
      super(typ, isKey);
      this.name = Objects.requireNonNull(name, "name is null");
    }

    @NonNull
    @Override
    public String name() {
      return name;
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
          && Objects.equals(name(), ((Tab.NamedCol) abstractCol).name());
    }

    @Override
    public int hashCode() {
      return Objects.hash(typ(), isKey(), name);
    }

    @Override
    public boolean canEqual(Object o) {
      return o instanceof AbstractTab.AbstractCol && o instanceof Tab.NamedCol;
    }

    @NonNull
    @Override
    public String toString() {
      if (isKey()) {
        return String.format("[%s KEY %s]", name, typ());
      } else {
        return String.format("[%s %s]", name, typ());
      }
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
    public Typ typ() {
      return typ;
    }

    @Override
    @NonNull
    public Object eval() {
      return obj;
    }

    @Override
    public boolean matchesActual(@NonNull Tab.Val actual) {
      Objects.requireNonNull(actual, "other");
      return eval().equals(actual.eval());
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
      return renderer().renderVal(this);
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

    SuppVal(Typ typ, Supplier<?> supp) {
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
    public boolean matchesActual(@NonNull Tab.Val actual) {
      return Objects.equals(eval(), actual.eval());
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
    public Typ typ() {
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

  public static final class UnnamedColAdderTable extends AbstractColRowAdderTable
      implements Tab.UnnamedColAdder<UnnamedColAdderTable, RowAdderTable> {

    @Override
    @NonNull
    public UnnamedColAdderTable col(@NonNull Typ typ) {
      Objects.requireNonNull(typ, "typ");
      mutableCols().add(new SimpleCol(typ, false));
      return this;
    }

    @Override
    @NonNull
    public UnnamedColAdderTable key(@NonNull Typ typ) {
      Objects.requireNonNull(typ, "typ");
      mutableCols().add(new SimpleCol(typ, true));
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
      mutableCols().add(new SimpleNamedCol(name, typ, false));
      return this;
    }

    @Override
    @NonNull
    public NamedColAdderTable key(@NonNull String name, @NonNull Typ typ) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(typ, "typ");
      mutableCols().add(new SimpleNamedCol(name, typ, true));
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
      cols.add(new SimpleCol(typ, false));
      return this;
    }

    @NonNull
    @Override
    public UnnamedColBuilder key(@NonNull Typ typ) {
      Objects.requireNonNull(typ);
      cols.add(new SimpleCol(typ, true));
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
      cols.add(new SimpleNamedCol(name, typ, false));
      return this;
    }

    @NonNull
    @Override
    public NamedColBuilder key(@NonNull String name, @NonNull Typ typ) {
      Objects.requireNonNull(name);
      Objects.requireNonNull(typ);
      cols.add(new SimpleNamedCol(name, typ, true));
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
    for (Object o : objects) {
      Typ typ = Utils.getTyp(o);
      cols.add(new SimpleCol(typ, false));
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
        val = Utils.getVal(o, col.typ());
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
    Objects.requireNonNull(tab, "tab is null");
    List<Tab.Col> cols = Objects.requireNonNull(tab.cols(), "cols is null");
    Iterator<Tab.Col> iter = Objects.requireNonNull(cols.iterator(), "iter is null");
    boolean processedFirstCol = false;
    boolean hasNamedCols = false;
    while (iter.hasNext()) {
      Tab.Col col = Objects.requireNonNull(iter.next(), "col is null");
      if (processedFirstCol) {
        if (hasNamedCols != col instanceof Tab.NamedCol) {
          throw new IllegalStateException("Mixture of named and unnamed cols!");
        }
      } else {
        hasNamedCols = col instanceof Tab.NamedCol;
      }
      processedFirstCol = true;
    }
    return hasNamedCols;
  }

  @NonNull
  public static Tab.NamedColTab wrapWithNamedCols(@NonNull Tab tab) {
    Objects.requireNonNull(tab, "tab is null");
    if (tab instanceof Tab.NamedColTab) {
      return (Tab.NamedColTab) tab;
    } else {
      return new NamedColWrapper(tab);
    }
  }

  @NonNull
  public static List<String> colNames(Tab tab) {
    List<String> colNames = new ArrayList<>();
    if (tab instanceof Tab.NamedColTab) {
      for (Tab.NamedCol c: ((Tab.NamedColTab) tab).namedCols()) {
        colNames.add(c.name());
      }
    } else {
      int i = 1;
      for (Tab.Col c: tab.cols()) {
        if (c instanceof Tab.NamedCol) {
          colNames.add(((Tab.NamedCol) c).name());
        } else {
          colNames.add("col" + i++);
        }
      }
    }
    return Collections.unmodifiableList(colNames);
  }

  public interface NamedColsMixin extends Tab.NamedColTab {
    @NonNull
    @Override
    default List<Tab.NamedCol> namedCols() {
      int i = 1;
      List<Tab.NamedCol> result = new ArrayList<>();
      for (Col c: cols()) {
        if (c instanceof Tab.NamedCol) {
          result.add((Tab.NamedCol) c);
        } else {
          result.add(new TableUtils.SimpleNamedCol("col" + i++, c.typ(), c.isKey()));
        }
      }
      return Collections.unmodifiableList(result);
    }
  }

  public static class NamedColWrapper extends AbstractTab implements NamedColsMixin {
    @NonNull
    private final Tab tab;

    private NamedColWrapper(Tab tab) {
      super(tab.cols());
      this.tab = tab;
    }

    @NonNull
    @Override
    public List<Row> rows() {
      return tab.rows();
    }
  }

  private TableUtils() {
  }
}
