package io.github.nblxa.turntables;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TestUtils {
  @Test
  public void testUnknownTyp() {
    TestUtils obj = new TestUtils();
    Throwable t = catchThrowable(() -> Utils.getTyp(obj));
    assertThat(t)
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unsupported object type: " + obj.getClass());
  }
}
