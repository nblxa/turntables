package io.github.nblxa.turntables;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class TestEquals {

  @Test
  public void testAbstractCol_equalsContract() {
    EqualsVerifier.forClass(AbstractTab.AbstractCol.class)
        .withRedefinedSubclass(TableUtils.NamedCol.class)
        .verify();
  }

  @Test
  public void testSimpleCol_equalsContract() {
    EqualsVerifier.forClass(TableUtils.SimpleCol.class)
        .verify();
  }

  @Test
  public void testNamedCol_equalsContract() {
    EqualsVerifier.forClass(TableUtils.NamedCol.class)
        .withRedefinedSuperclass()
        .verify();
  }

  @Test
  public void testInferredTypDecoratorCol_equalsContract() {
    EqualsVerifier.forClass(Utils.InferredTypDecoratorCol.class)
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