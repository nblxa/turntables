package io.github.nblxa.fluenttab;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class TestEquals {

  @Test
  public void testAbstractCol_equalsContract() {
    EqualsVerifier.forClass(AbstractTab.AbstractCol.class)
        .withRedefinedSubclass(TableUtils.NamedCol.class)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

  @Test
  public void testNamedCol_equalsContract() {
    EqualsVerifier.forClass(TableUtils.NamedCol.class)
        .withRedefinedSuperclass()
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

  @Test
  public void testSimpleVal_equalsContract() {
    EqualsVerifier.forClass(TableUtils.SimpleVal.class)
        .verify();
  }

  @Test
  public void testSuppVal_equalsContract() {
    EqualsVerifier.forClass(TableUtils.SuppVal.class)
        .verify();
  }

  @Test
  public void testNullVal_equalsContract() {
    EqualsVerifier.forClass(Typ.NullVal.class)
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
    EqualsVerifier.forClass(TableUtils.ColAdder.class)
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
}