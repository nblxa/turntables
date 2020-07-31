package io.github.nblxa.turntables.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;
import org.junit.Test;
import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.SettingsTransaction;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;

public class TestDecimalMode {
  private static AssertionProxy.Builder assertionProxy(Tab expected, Tab actual, Settings settings) {
    return AssertionProxy.builder()
        .expected(expected)
        .actual(actual)
        .settings(settings);
  }

  private static boolean match(Tab expected, Tab actual, Settings settings) {
    return assertionProxy(expected, actual, settings)
        .buildOrGetActualProxy()
        .matchesExpected();
  }

  private static void shouldMatchOnlyWhen(Tab exp, Tab act, Predicate<Settings> settingsPredicate) {
    for (Settings.DecimalMode dm : Settings.DecimalMode.values()) {
      Settings s = Settings.builder()
          .decimalMode(dm)
          .build();
      try (SettingsTransaction ignored = Turntables.setSettings(s)) {
        boolean expectedMatch = settingsPredicate.test(s);
        boolean actualMatch = match(exp, act, s);
        assertThat(actualMatch)
            .withFailMessage("Expected a=%s to %smatch e=%s using %s",
                act.cols(), (expectedMatch ? "" : "not "), exp.cols(), s)
            .isEqualTo(expectedMatch);
      }
    }
  }

  @Test
  public void test() {
    Map<Typ, Tab> m = new EnumMap<>(Typ.class);
    m.put(Typ.INTEGER, Turntables.tab().col(Typ.INTEGER).row(10));
    m.put(Typ.LONG, Turntables.tab().col(Typ.LONG).row(10L));
    m.put(Typ.DOUBLE, Turntables.tab().col(Typ.DOUBLE).row(10.0d));
    m.put(Typ.DECIMAL, Turntables.tab().col(Typ.DECIMAL).row(BigDecimal.valueOf(10L)));
    for (Map.Entry<Typ, Tab> expEntry : m.entrySet()) {
      for (Map.Entry<Typ, Tab> actEntry : m.entrySet()) {
        Typ expTyp = expEntry.getKey();
        Typ actTyp = actEntry.getKey();
        Tab exp = expEntry.getValue();
        Tab act = actEntry.getValue();
        shouldMatchOnlyWhen(exp, act,
            s -> expTyp == actTyp
                 || (s.decimalMode == Settings.DecimalMode.CONVERT
                     && (expTyp == Typ.DECIMAL || actTyp == Typ.DECIMAL)
                    )
        );
      }
    }
  }
}
