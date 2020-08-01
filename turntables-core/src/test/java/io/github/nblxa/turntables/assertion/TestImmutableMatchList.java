package io.github.nblxa.turntables.assertion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.Test;

public class TestImmutableMatchList {

  @Test
  public void empty_toString_isEmpty() {
    String actual = ImmutableMatchList.EMPTY.toString();
    assertThat(actual).isEqualTo("{}");
  }

  @Test
  public void empty_size_yields0() {
    int actual = ImmutableMatchList.EMPTY.size();
    assertThat(actual).isZero();
  }

  @Test
  public void empty_asList_isEmpty() {
    ImmutableMatchList actual = ImmutableMatchList.EMPTY;
    assertThat(actual)
        .isEmpty();
    assertThat(actual.size()).isZero();
  }

  @Test
  public void empty_add_yieldsSize1() {
    ImmutableMatchList empty = ImmutableMatchList.EMPTY;
    ImmutableMatchList size1 = empty.add(42, 15);

    assertThat(size1).hasSize(1);
    assertThat(size1.size()).isEqualTo(1);
    assertThat(empty).isEmpty();
    assertThat(empty.size()).isZero();
  }

  @Test
  public void size1_toString_yieldsPair() {
    ImmutableMatchList size1 = ImmutableMatchList.EMPTY.add(42, 15);

    assertThat(size1).hasToString("{42=15}");
  }

  @Test
  public void size2_toString_yieldsTwoPairs() {
    ImmutableMatchList size1 = ImmutableMatchList.EMPTY
        .add(42, 15)
        .add(0, 1);

    assertThat(size1).hasToString("{0=1, 42=15}");
  }

  @Test
  public void empty_concatEmpty_yieldsEmpty() {
    ImmutableMatchList empty = ImmutableMatchList.EMPTY;
    ImmutableMatchList actual = empty.concat(empty);

    assertThat(empty).isEmpty();
    assertThat(empty.size()).isZero();
    assertThat(actual).isEmpty();
    assertThat(actual.size()).isZero();
    assertThat(actual).hasToString("{}");
  }

  @Test
  public void size1_concatSize1_yieldsSize2() {
    ImmutableMatchList a = ImmutableMatchList.EMPTY.add(1, 1);
    ImmutableMatchList b = ImmutableMatchList.EMPTY.add(2, 2);

    ImmutableMatchList actual = a.concat(b);

    assertThat(a).hasSize(1);
    assertThat(a.size()).isEqualTo(1);
    assertThat(b).hasSize(1);
    assertThat(b.size()).isEqualTo(1);
    assertThat(actual).hasSize(2);
    assertThat(actual.size()).isEqualTo(2);
  }

  @Test
  public void empty_concatSize1_yieldsSize1() {
    ImmutableMatchList empty = ImmutableMatchList.EMPTY;
    ImmutableMatchList size1 = empty.add(42, 15);
    ImmutableMatchList actual = empty.concat(size1);

    assertThat(empty).isEmpty();
    assertThat(empty.size()).isZero();
    assertThat(actual).isEqualTo(size1);
  }

  @Test
  public void size1_addAnother_yieldsSize2() {
    ImmutableMatchList size1 = ImmutableMatchList.EMPTY.add(42, 15);
    ImmutableMatchList size2 = size1.add(100, 100);

    assertThat(size1).hasSize(1);
    assertThat(size1.size()).isEqualTo(1);
    assertThat(size2).hasSize(2);
    assertThat(size2.size()).isEqualTo(2);
  }

  @Test
  public void size2_addAnother_yieldsSize3() {
    ImmutableMatchList size2 = ImmutableMatchList.EMPTY.add(42, 15)
        .add(100, 100);
    ImmutableMatchList size3 = size2.add(200, 200);

    assertThat(size2).hasSize(2);
    assertThat(size2.size()).isEqualTo(2);
    assertThat(size3).hasSize(3);
    assertThat(size3.size()).isEqualTo(3);
  }

  @Test
  public void size2_addMatchingExpected_yieldsException() {
    ImmutableMatchList size2 = ImmutableMatchList.EMPTY.add(42, 15)
        .add(100, 100);

    Throwable throwable = catchThrowable(() -> size2.add(42, 500));
    assertThat(throwable)
        .isExactlyInstanceOf(UnsupportedOperationException.class)
        .hasNoCause();
  }

  @Test
  public void size2_addMatchingActual_yieldsException() {
    ImmutableMatchList size2 = ImmutableMatchList.EMPTY.add(42, 15)
        .add(100, 100);

    Throwable throwable = catchThrowable(() -> size2.add(500, 100));
    assertThat(throwable)
        .isExactlyInstanceOf(UnsupportedOperationException.class)
        .hasNoCause();
  }
}
