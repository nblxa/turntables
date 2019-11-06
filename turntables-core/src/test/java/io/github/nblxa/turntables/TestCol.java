package io.github.nblxa.turntables;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestCol {

  private static List<Typ> allTypesButTestOrAny;
  private static Map<Typ, Object> typeValues;

  @BeforeClass
  public static void setUpClass() {
    allTypesButTestOrAny = Arrays.stream(Typ.values())
        .filter(t -> t != Typ.ANY)
        .collect(Collectors.toList());
    typeValues = new HashMap<>();
    typeValues.put(Typ.INTEGER, 5);
    typeValues.put(Typ.STRING, "test");
    typeValues.put(Typ.BOOLEAN, true);
    typeValues.put(Typ.DOUBLE, -9.9);
    typeValues.put(Typ.LONG, 88d);
    typeValues.put(Typ.DATE, LocalDate.of(2019, 3, 14));
    typeValues.put(Typ.DATETIME, LocalDateTime.of(2019, 3, 14, 20, 2, 0));
  }

  @Test
  public void test_allTypesButTestOrAny_acceptEachOther() {
    for (Typ colTyp : allTypesButTestOrAny) {
      for (Typ valTyp : allTypesButTestOrAny) {
        Tab.Col col = new TableUtils.SimpleCol(colTyp, false);
        Tab.Val val = new TableUtils.SimpleVal(valTyp, typeValues.get(valTyp));
        boolean doesAccept = col.accepts(val);
        if (valTyp == colTyp) {
          assertTrue("Col of type " + colTyp + " did not accept a value "
              + "of the same type.", doesAccept);
        } else {
          assertFalse("Col of type " + colTyp + " accepted a value of type "
              + valTyp + ".", doesAccept);
        }
      }
    }
  }

  @Test
  public void test_any_acceptsAll() {
    Tab.Col col = new TableUtils.SimpleCol(Typ.ANY, false);
    for (Typ valTyp : allTypesButTestOrAny) {
      Tab.Val val = new TableUtils.SimpleVal(valTyp, typeValues.get(valTyp));
      assertTrue("Col of type " + Typ.ANY + " did not accept a value of type "
          + valTyp + ".", col.accepts(val));
    }
  }
}
