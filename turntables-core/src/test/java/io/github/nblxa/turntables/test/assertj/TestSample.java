package io.github.nblxa.turntables.test.assertj;

import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import org.junit.Ignore;
import org.junit.Test;

public class TestSample {
  @Test
  @Ignore("Sample test designed to demonstrate the assertion failure message")
  public void test() {
    Tab actual = Turntables.tab()
        .col("name").key("id")
        .row("Elise", 1)
        .row("Bob", 2);

    Turntables.assertThat(actual)
        .colMode(Settings.ColMode.MATCH_BY_NAME)
        .rowMode(Settings.RowMode.MATCH_BY_KEY)
        .matches()
        .key("id").col("name")
        .row(1, "Alice")
        .row(2, "Bob")
        .asExpected();
  }
}
