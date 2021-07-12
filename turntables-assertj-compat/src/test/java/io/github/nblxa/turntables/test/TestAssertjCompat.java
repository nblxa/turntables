package io.github.nblxa.turntables.test;

import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestAssertjCompat {
  @Test
  public void test() {
    Tab actual = Turntables.tab()
        .col("name").key("id")
        .row("Elise", 1)
        .row("Bob", 2);

    Throwable t = Assertions.catchThrowable(() -> Turntables.assertThat(actual)
        .colMode(Settings.ColMode.MATCH_BY_NAME)
        .rowMode(Settings.RowMode.MATCH_BY_KEY)
        .matches()
        .key("id").col("name")
        .row(1, "Alice")
        .row(2, "Bob")
        .asExpected());

    assertNotNull(t);
    if (t.getClass() != AssertionError.class) {
      throw new AssertionError("Expected AssertionError but got Throwable: ", t);
    }
    assertEquals("\n" +
        "EXPECTED: Table:\n" +
        "    - id   : 1\n" +
        "      name : Alice\n" +
        "    - id   : 2\n" +
        "      name : Bob\n" +
        "BUT: WAS Table:\n" +
        "    - id   : 1\n" +
        "      name : Elise\n" +
        "    - id   : 2\n" +
        "      name : Bob", t.getMessage());
  }
}
