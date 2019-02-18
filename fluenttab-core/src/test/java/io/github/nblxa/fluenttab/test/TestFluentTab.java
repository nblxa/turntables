package io.github.nblxa.fluenttab.test;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.nblxa.fluenttab.AbstractTab;
import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.TableUtils;
import io.github.nblxa.fluenttab.FluentTab;
import io.github.nblxa.fluenttab.Typ;
import io.github.nblxa.fluenttab.exception.StructureException;
import java.time.LocalDate;
import java.util.Iterator;
import org.junit.Test;

public class TestFluentTab {

  @Test
  public void test_row() {
    Tab tab = FluentTab.tab()
        .row(1, "text");
    assertThat(tab).isNotNull();
    Iterator<Tab.Row> rowRef = tab.rows().iterator();
    assertThat(rowRef).hasNext();
    Tab.Row row = rowRef.next();
    assertThat(row).isNotNull();
    assertThat(row.vals()).hasSize(2);
  }

  @Test
  public void test_nullInHeaderRow() {
    Tab tab = FluentTab.tab()
        .row(null, "text")
        .row(2, "lorem ipsum");

    assertThat(tab).isNotNull();
    assertThat(tab.cols()).hasSize(2);
    assertThat(tab.cols().iterator())
        .isNotNull()
        .hasNext();
    Typ typ = tab.cols().iterator().next().typ();
    assertThat(typ).isEqualTo(Typ.INTEGER);
  }

  @Test
  public void test_nullInFirstRowWithTypedColumns() {
    FluentTab.tab()
        .col("id", Typ.INTEGER)
        .col("desc", Typ.STRING)
        .row(null, "text")
        .row(2, "lorem ipsum");
  }

  @Test
  public void test_nullInBody() {
    FluentTab.tab()
        .row(1, "text")
        .row(null, "lorem ipsum");
  }

  @Test
  public void test_colsDefined() {
    FluentTab.tab()
        .col("a", Typ.INTEGER)
        .col("b", Typ.STRING)
        .row(1, "text")
        .row(2, "lorem ipsum");
  }

  @Test(expected = StructureException.class)
  public void test_colsDefinedTypeMismatch() {
    // todo assert better
    FluentTab.tab()
        .col("a", Typ.INTEGER)
        .col("b", Typ.STRING)
        .row(1, "text")
        .row(2, LocalDate.of(2001, 1, 1));
  }

  @Test
  public void test_colsDuplicate() {
    FluentTab.tab()
        .col("a", Typ.INTEGER)
        .col("a", Typ.STRING)
        .row(1, "text")
        .row(2, "lorem ipsum");
  }

  @Test
  public void test_rowIterator() {
    Tab tab = FluentTab.tab()
        .row(1)
        .row(2);

    assertThat(tab).isNotNull();
    assertThat(tab.rows()).hasSize(2);

    Iterator<Tab.Row> rows = tab.rows().iterator();
    assertThat(rows).hasNext();

    Tab.Row row = rows.next();
    assertThat(row).isNotNull();
    assertThat(row.vals()).isNotNull();
    assertThat(row.vals().iterator()).hasNext();

    Tab.Val val = row.vals().iterator().next();
    assertThat(val).isNotNull();
    assertThat(val.eval()).isEqualTo(1);

    assertThat(rows).hasNext();

    row = rows.next();
    assertThat(row.vals()).isNotNull();
    assertThat(row.vals().iterator()).hasNext();

    val = row.vals().iterator().next();
    assertThat(val).isNotNull();
    assertThat(val.eval()).isEqualTo(2);
    assertThat(rows.hasNext()).isFalse();
  }

  @Test
  public void test_colIterator() {
    Tab tab = FluentTab.tab()
        .row(1, 2);

    assertThat(tab).isNotNull();
    assertThat(tab.rows()).hasSize(1);

    Tab.Row row = tab.rows().iterator().next();
    assertThat(row).isNotNull();
    assertThat(row.vals()).hasSize(2);

    Iterator<Tab.Val> vals = row.vals().iterator();
    assertThat(vals.next().eval()).isEqualTo(1);
    assertThat(vals.next().eval()).isEqualTo(2);
  }

  @Test
  public void test_predicate() {
    Tab tab = FluentTab.tab()
        .row(FluentTab.testInt(i -> i > 20));

    assertThat(tab).isNotNull();
    assertThat(tab.rows()).hasSize(1);

    Tab.Row row = tab.rows().iterator().next();
    assertThat(row).isNotNull();
    assertThat(row.vals()).hasSize(1);

    Tab.Val val = row.vals().iterator().next();
    assertThat(val).isInstanceOf(AbstractTab.AbstractAssertionVal.class);
  }

  @Test
  public void test_rowBuilder() {
    Tab tabFixed = new TableUtils.Builder().rowBuilder()
        .row(1, 2, "a")
        .row(3, 4, "b")
        .build();

    Tab tab = FluentTab.tab()
        .row(1,2, "a")
        .row(3, 4, "b");

    assertThat(tabFixed).isEqualTo(tab);
  }
}
