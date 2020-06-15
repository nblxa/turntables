package io.github.nblxa.turntables;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTypConversion {
  private static final Settings ALLOW_DECIMAL_CONVERSION = new Settings.Builder()
      .decimalMode(Settings.DecimalMode.ALLOW_BIG)
      .build();

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
    typeValues.put(Typ.DECIMAL, BigDecimal.valueOf(100500L));
  }

  @Test
  public void test_allTypesButTestOrAny_acceptThemselves() {
    for (Typ colTyp : allTypesButAny) {
      for (Typ valTyp : allTypesButAny) {
        boolean doesAccept = colTyp.accepts(valTyp);
        if (valTyp == colTyp) {
          assertTrue("Type " + colTyp + " did not accept the same type.", doesAccept);
        } else {
          assertFalse("Type " + colTyp + " accepted type " + valTyp + ".", doesAccept);
        }
      }
    }
  }

  @Test
  public void test_any_acceptsAll() {
    for (Typ valTyp : allTypesButAny) {
      assertTrue("Type " + Typ.ANY + " did not accept a value of type " + valTyp + ".",
          Typ.ANY.accepts(valTyp));
    }
  }

  @Test
  public void testWithDecimalConversion_decimal_acceptsNumericTypes() {
    try (SettingsTransaction ignored = Turntables.setSettings(ALLOW_DECIMAL_CONVERSION)) {
      for (Typ colTyp : allTypesButAny) {
        for (Typ valTyp : allTypesButAny) {
          boolean doesAccept = colTyp.accepts(valTyp);
          if (valTyp == colTyp) {
            assertTrue("Type " + colTyp + " did not accept the same type.", doesAccept);
          } else {
            if ((colTyp == Typ.DECIMAL)
                && (valTyp == Typ.INTEGER || valTyp == Typ.LONG || valTyp == Typ.DOUBLE)) {
              assertTrue("Type " + colTyp + " did not accept the type " + valTyp + ".",
                  doesAccept);
            } else {
              if ((colTyp == Typ.INTEGER || colTyp == Typ.LONG || colTyp == Typ.DOUBLE)
                  && (valTyp == Typ.DECIMAL)) {
                assertTrue("Type " + colTyp + " did not accept the type " + valTyp + ".",
                    doesAccept);
              } else {
                assertFalse("Type " + colTyp + " accepted type " + valTyp + ".",
                    doesAccept);
              }
            }
          }
        }
      }
    }
  }

  @Test
  public void test_allTypesButTestOrAny_convertToThemselves() {
    for (Typ colTyp : allTypesButAny) {
      for (Typ valTyp : allTypesButAny) {
        Tab.Val val = new TableUtils.SimpleVal(valTyp, typeValues.get(valTyp));
        Throwable convertThrowable = catchThrowable(() -> val.evaluateAs(colTyp));
        if (valTyp == colTyp) {
          assertNull("Type " + colTyp + " did not accept the same type.", convertThrowable);
        } else {
          assertNotNull("Type " + colTyp + " accepted type " + valTyp + ".", convertThrowable);
        }
      }
    }
  }

  @Test
  public void test_all_convertToAny() {
    for (Typ valTyp : allTypesButAny) {
      Tab.Val val = new TableUtils.SimpleVal(valTyp, typeValues.get(valTyp));
      Throwable t = catchThrowable(() -> val.evaluateAs(Typ.ANY));
      assertNull("Type " + Typ.ANY + " did not accept a value of type " + valTyp + ".", t);
    }
  }

  @Test
  public void testWithDecimalConversion_testTypesConversion() {
    try (SettingsTransaction ignored = Turntables.setSettings(ALLOW_DECIMAL_CONVERSION)) {
      for (Typ colTyp : allTypesButAny) {
        for (Typ valTyp : allTypesButAny) {
          Tab.Val val = new TableUtils.SimpleVal(valTyp, typeValues.get(valTyp));
          Throwable t = catchThrowable(() -> val.evaluateAs(colTyp));
          if (valTyp == colTyp) {
            assertNull("Value " + val + " did not convert to the same type.", t);
          } else {
            if ((colTyp == Typ.DECIMAL)
                && (valTyp == Typ.INTEGER || valTyp == Typ.LONG || valTyp == Typ.DOUBLE)) {
              assertNull("Value " + colTyp + " did not convert to the type " + colTyp + ".", t);
            } else {
              if ((colTyp == Typ.INTEGER || colTyp == Typ.LONG || colTyp == Typ.DOUBLE)
                  && (valTyp == Typ.DECIMAL)) {
                assertNull("Value " + val + " did not convert to the type " + colTyp + ".", t);
              } else {
                assertNotNull("Value " + val + " converted to type " + colTyp + ".", t);
              }
            }
          }
        }
      }
    }
  }
}
