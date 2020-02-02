package io.github.nblxa.turntables;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TestEquals {

  @Test
  public void testAbstractCol_equalsContract() {
    EqualsVerifier.forClass(AbstractTab.AbstractCol.class)
        .withRedefinedSubclass(TableUtils.SimpleNamedCol.class)
        .verify();
  }

  @Test
  public void testSimpleCol_equalsContract() {
    EqualsVerifier.forClass(TableUtils.SimpleCol.class)
        .verify();
  }

  @Test
  public void testNamedCol_equalsContract() {
    EqualsVerifier.forClass(TableUtils.SimpleNamedCol.class)
        .withRedefinedSuperclass()
        .verify();
  }

  @Test
  public void testInferredTypDecoratorCol_equalsContract() {
    EqualsVerifier.forClass(Utils.InferredTypDecoratorCol.class)
        .verify();
  }

  @Test
  public void testInferredTypDecoratorNamedCol_equalsContract() {
    EqualsVerifier.forClass(Utils.InferredTypDecoratorNamedCol.class)
        .withRedefinedSuperclass()
        .verify();
  }

  @Test
  public void testSimpleVal_equalsContract() {
    EqualsVerifier.forClass(TableUtils.SimpleVal.class)
        .withRedefinedSuperclass()
        .verify();
  }

  @Test
  public void testSuppVal_equalsContract() {
    EqualsVerifier.forClass(TableUtils.SuppVal.class)
        .withRedefinedSuperclass()
        .verify();
  }

  @Test
  public void testNullVal_equalsContract() {
    EqualsVerifier.forClass(Typ.NullVal.class)
        .withRedefinedSuperclass()
        .verify();
  }

  @Test
  public void testSimpleAssertionVal_equalsContract() {
    EqualsVerifier.forClass(TableUtils.SimpleAssertionVal.class)
        .withRedefinedSuperclass()
        .verify();
  }

  @Test
  public void testSimpleRow_equalsContract() {
    EqualsVerifier.forClass(TableUtils.SimpleRow.class)
        .verify();
  }

  @Test
  public void testColAdderTable_equalsContract() {
    EqualsVerifier.forClass(TableUtils.ColAdderTable.class)
        .verify();
  }

  @Test
  public void testNamedColAdderTable_equalsContract() {
    EqualsVerifier.forClass(TableUtils.NamedColAdderTable.class)
        .verify();
  }

  @Test
  public void testUnnamedColAdderTable_equalsContract() {
    EqualsVerifier.forClass(TableUtils.UnnamedColAdderTable.class)
        .verify();
  }

  @Test
  public void testRowAdderTable_equalsContract() {
    EqualsVerifier.forClass(TableUtils.RowAdderTable.class)
        .withIgnoredFields("rows")
        .verify();
  }

  @Test
  public void testIterableListDecorator_equalsContract() {
    EqualsVerifier.forClass(IterableListDecorator.class)
        .suppress(Warning.NONFINAL_FIELDS, Warning.TRANSIENT_FIELDS)
        .withIgnoredFields("iterable")
        .verify();
  }

  @Test
  public void testFixedTable_equalsContract() {
    EqualsVerifier.forClass(TableUtils.FixedTable.class)
        .verify();
  }

  @Test
  public void testTab_equalsContract() {
    TableUtils.RowAdderTable rowAdderTable = Turntables.tab()
        .col("A", Typ.STRING)
        .col("B", Typ.DATE)
        .row("abc", LocalDate.of(2020, 1, 1))
        .row("xyz", LocalDate.of(2020, 2, 2));

    Tab tab = Turntables.tab()
        .col(Typ.ANY).col(Typ.ANY)
        .row(1, "abc")
        .row(2, "def");
    List<Tab.Col> cols = new LinkedList<>();
    tab.cols().forEach(cols::add);
    List<Tab.Row> rows = new LinkedList<>();
    tab.rows().forEach(rows::add);
    NonAbstractTab nonAbstractTab = new NonAbstractTab(cols, rows);

    EqualsVerifier.forClass(TableUtils.RowAdderTable.class)
        .withPrefabValues(Tab.class, rowAdderTable, nonAbstractTab)
        .withIgnoredFields("rows")
        .verify();
  }

  @Test
  public void testAbstractTab_equalsContract() {
    AbstractTab tab1 = Turntables.tab()
        .col("A", Typ.STRING)
        .col("B", Typ.DATE)
        .row("abc", LocalDate.of(2020, 1, 1))
        .row("xyz", LocalDate.of(2020, 2, 2));
    List<Tab.Row> rows = Utils.toArrayList(tab1.rows());
    List<Tab.Row> rowsReversed = new ArrayList<>(2);
    rowsReversed.add(rows.get(1));
    rowsReversed.add(rows.get(0));

    AbstractTab tab2 = new SubclassAbstractTab(tab1.cols(), rowsReversed);

    EqualsVerifier.forClass(TableUtils.RowAdderTable.class)
        .withPrefabValues(AbstractTab.class, tab1, tab2)
        .withIgnoredFields("rows")
        .verify();
  }

  static class NonAbstractTab implements Tab {
    @NonNull
    private final Iterable<Col> cols;
    @NonNull
    private final Iterable<Row> rows;

    NonAbstractTab(@NonNull Iterable<Col> cols, @NonNull Iterable<Row> rows) {
      this.cols = cols;
      this.rows = rows;
    }

    @NonNull
    @Override
    public Iterable<Col> cols() {
      return new Iterable<Col>() {
        @NonNull
        @Override
        public Iterator<Col> iterator() {
          return cols.iterator();
        }
      };
    }

    @NonNull
    @Override
    public Iterable<Row> rows() {
      return new Iterable<Row>() {
        @NonNull
        @Override
        public Iterator<Row> iterator() {
          return rows.iterator();
        }
      };
    }
  }

  public static final class SubclassAbstractTab extends AbstractTab {
    @NonNull
    private final Iterable<Row> rows;

    SubclassAbstractTab(@NonNull Iterable<Col> cols, @NonNull Iterable<Row> rows) {
      super(cols);
      this.rows = rows;
    }

    @NonNull
    @Override
    public Iterable<Row> rows() {
      return rows;
    }
  }

}
