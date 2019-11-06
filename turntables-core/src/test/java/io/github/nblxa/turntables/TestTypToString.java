package io.github.nblxa.turntables;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.Test;

public class TestTypToString {

  @Test
  public void test_integerToString() {
    Tab.Val val = new TableUtils.SimpleVal(Typ.INTEGER, 1);
    String act = val.toString();
    assertEquals("1", act);
  }

  @Test
  public void test_longToString() {
    Tab.Val val = new TableUtils.SimpleVal(Typ.LONG, 1L);
    String act = val.toString();
    assertEquals("1", act);
  }

  @Test
  public void test_stringToString() {
    Tab.Val val = new TableUtils.SimpleVal(Typ.STRING, "1");
    String act = val.toString();
    assertEquals("1", act);
  }

  @Test
  public void test_stringToStringEscaped() {
    Tab.Val val = new TableUtils.SimpleVal(Typ.STRING, "1\n2");
    String act = val.toString();
    assertEquals("\"1\\n2\"", act);
  }

  @Test
  public void test_booleanToString() {
    Tab.Val val = new TableUtils.SimpleVal(Typ.BOOLEAN, true);
    String act = val.toString();
    assertEquals("true", act);
  }

  @Test
  public void test_dateToString() {
    Tab.Val val = new TableUtils.SimpleVal(Typ.DATE, LocalDate.of(2019, 1, 31));
    String act = val.toString();
    assertEquals("2019-01-31", act);
  }

  @Test
  public void test_datetimeToString() {
    Tab.Val val = new TableUtils.SimpleVal(Typ.DATETIME, LocalDateTime.of(2019, 1, 31, 13, 37, 59));
    String act = val.toString();
    assertEquals("2019-01-31T13:37:59", act);
  }

  @Test
  public void test_doubleToString() {
    Tab.Val val = new TableUtils.SimpleVal(Typ.DOUBLE, 0.33);
    String act = val.toString();
    assertEquals("0.33", act);
  }

  @Test
  public void test_nullValInteger() {
    Tab.Val val = Typ.INTEGER.nullVal();
    String act = val.toString();
    assertEquals("null", act);
  }

  @Test
  public void test_nullValLong() {
    Tab.Val val = Typ.LONG.nullVal();
    String act = val.toString();
    assertEquals("null", act);
  }

  @Test
  public void test_nullValBoolean() {
    Tab.Val val = Typ.BOOLEAN.nullVal();
    String act = val.toString();
    assertEquals("null", act);
  }

  @Test
  public void test_nullValString() {
    Tab.Val val = Typ.STRING.nullVal();
    String act = val.toString();
    assertEquals("null", act);
  }

  @Test
  public void test_nullValDate() {
    Tab.Val val = Typ.DATE.nullVal();
    String act = val.toString();
    assertEquals("null", act);
  }

  @Test
  public void test_nullValDatetime() {
    Tab.Val val = Typ.DATETIME.nullVal();
    String act = val.toString();
    assertEquals("null", act);
  }

  @Test
  public void test_nullValDouble() {
    Tab.Val val = Typ.DOUBLE.nullVal();
    String act = val.toString();
    assertEquals("null", act);
  }

  @Test
  public void test_nullValAny() {
    Tab.Val val = Typ.ANY.nullVal();
    String act = val.toString();
    assertEquals("null", act);
  }

  @Test
  public void test_nullValAll() {
    Tab.Val val = Typ.ANY.nullVal();
    String act = val.toString();
    assertEquals("null", act);
  }

  @Test
  public void testIntSupplier() {
    Tab.Val val = new TableUtils.SuppVal(Typ.INTEGER, () -> 1);
    String act = val.toString();
    assertEquals("1", act);
  }
}
