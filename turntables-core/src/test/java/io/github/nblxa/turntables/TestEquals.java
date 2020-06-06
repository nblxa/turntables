package io.github.nblxa.turntables;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestEquals {

  @Test
  public void testAbstractCol_equalsContract() {
    EqualsVerifier.forClass(AbstractTab.AbstractCol.class)
        .verify();
  }

  @Test
  public void testSimpleCol_equalsContract() {
    EqualsVerifier.forClass(TableUtils.SimpleCol.class)
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
        .withIgnoredFields("suppCalled", "obj", "inited")
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
    EqualsVerifier.forClass(TableUtils.AssertionVal.class)
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
        .withIgnoredFields("mutableCols")
        .verify();
  }

  @Test
  public void testNamedColAdderTable_equalsContract() {
    EqualsVerifier.forClass(TableUtils.NamedColAdderTable.class)
        .withIgnoredFields("mutableCols")
        .verify();
  }

  @Test
  public void testUnnamedColAdderTable_equalsContract() {
    EqualsVerifier.forClass(TableUtils.UnnamedColAdderTable.class)
        .withIgnoredFields("mutableCols", "colIndex")
        .verify();
  }

  @Test
  public void testRowAdderTable_equalsContract() {
    EqualsVerifier.forClass(TableUtils.RowAdderTable.class)
        .withIgnoredFields("mutableCols", "mutableRows")
        .verify();
  }

  @Test
  public void testFixedTable_equalsContract() {
    EqualsVerifier.forClass(TableUtils.FixedTable.class)
        .withIgnoredFields("mutableCols")
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
    List<Tab.Col> cols = new ArrayList<>(tab.cols());
    List<Tab.Row> rows = new ArrayList<>(tab.rows());
    NonAbstractTab nonAbstractTab = new NonAbstractTab(cols, rows);

    EqualsVerifier.forClass(TableUtils.RowAdderTable.class)
        .withPrefabValues(Tab.class, rowAdderTable, nonAbstractTab)
        .withIgnoredFields("mutableCols", "mutableRows")
        .verify();
  }

  @Test
  public void testAbstractTab_equalsContract() {
    AbstractTab tab1 = Turntables.tab()
        .col("A", Typ.STRING)
        .col("B", Typ.DATE)
        .row("abc", LocalDate.of(2020, 1, 1))
        .row("xyz", LocalDate.of(2020, 2, 2));
    List<Tab.Row> rows = tab1.rows();
    List<Tab.Row> rowsReversed = new ArrayList<>(2);
    rowsReversed.add(rows.get(1));
    rowsReversed.add(rows.get(0));

    AbstractTab tab2 = new SubclassAbstractTab(tab1.cols(), rowsReversed);

    EqualsVerifier.forClass(TableUtils.RowAdderTable.class)
        .withPrefabValues(AbstractTab.class, tab1, tab2)
        .withIgnoredFields("mutableCols", "mutableRows")
        .verify();
  }

  static class NonAbstractTab implements Tab {
    @NonNull
    private final List<Col> cols;
    @NonNull
    private final List<Row> rows;

    NonAbstractTab(@NonNull List<Col> cols, @NonNull List<Row> rows) {
      this.cols = cols;
      this.rows = rows;
    }

    @NonNull
    @Override
    public List<Col> cols() {
      return new ArrayList<>(cols);
    }

    @NonNull
    @Override
    public List<Row> rows() {
      return new ArrayList<>(rows);
    }
  }

  public static final class SubclassAbstractTab extends AbstractTab {
    @NonNull
    private final List<Row> rows;

    SubclassAbstractTab(@NonNull List<Col> cols, @NonNull List<Row> rows) {
      super(cols);
      this.rows = rows;
    }

    @NonNull
    @Override
    public List<Row> rows() {
      return rows;
    }
  }

}
