package io.github.nblxa.turntables;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestHamcrestCol {
  private static List<Typ> allTypesButAny;
  private static Map<Typ, Object> typeValues;

  @BeforeClass
  public static void setUpClass() {
    allTypesButAny = Arrays.stream(Typ.values())
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
  public void test_allTypesButTestOrAny_acceptsMatcher() {
    int i = 0;
    for (Typ colTyp : allTypesButAny) {
      Tab.Col col = new TableUtils.SimpleCol("col" + (++i), colTyp, false);
      Tab.Val val = new MatcherVal(Matchers.anything());
      assertTrue(col.typ().accepts(val.typ()));
    }
  }
}
