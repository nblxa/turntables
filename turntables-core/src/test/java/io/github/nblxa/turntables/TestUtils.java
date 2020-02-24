package io.github.nblxa.turntables;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TestUtils {

  @Test
  public void testWithArrayList() {
    List<String> list = new ArrayList<>(Arrays.asList("Hello", "world"));
    List<String> al = Utils.toArrayList(list);

    assertThat(list == al)
        .isTrue();
  }

  @Test
  public void testWithAnotherIterable() {
    Iterable<String> list = new LinkedHashSet<>(Arrays.asList("Hello", "world"));
    List<String> al = Utils.toArrayList(list);

    assertThat(list == al)
        .isFalse();
    assertThat(al)
        .isInstanceOf(ArrayList.class)
        .containsExactly("Hello", "world");
  }

  @Test
  public void testUnknownTyp() {
    TestUtils obj = new TestUtils();
    Throwable t = catchThrowable(() -> Utils.getTyp(obj));
    assertThat(t)
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unsupported object type: " + obj.getClass());
  }
}
