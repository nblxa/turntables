package io.github.nblxa.turntables.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.BitSet;
import java.util.Iterator;
import org.junit.Test;
import nl.jqno.equalsverifier.EqualsVerifier;

public class TestImmutableBitSet {

  @Test
  public void test_iterator_empty() {
    BitSet bitSet = new BitSet(5);
    ImmutableBitSet immutableBitSet = new ImmutableBitSet(bitSet);
    Iterator<Integer> iterator = immutableBitSet.iterator();

    assertThat(iterator)
        .isExhausted();
  }

  @Test
  public void test_iterator_onlyMaxValue() {
    BitSet bitSet = new BitSet(Integer.MAX_VALUE);
    bitSet.set(Integer.MAX_VALUE);
    ImmutableBitSet immutableBitSet = new ImmutableBitSet(bitSet);
    Iterator<Integer> iterator = immutableBitSet.iterator();

    assertThat(iterator)
        .hasNext();
    assertThat(iterator.next())
        .isEqualTo(Integer.MAX_VALUE);
    assertThat(iterator)
        .isExhausted();
  }

  @Test
  public void test_iterator_0andMaxValue() {
    BitSet bitSet = new BitSet(Integer.MAX_VALUE);
    bitSet.set(0);
    bitSet.set(Integer.MAX_VALUE);
    ImmutableBitSet immutableBitSet = new ImmutableBitSet(bitSet);
    Iterator<Integer> iterator = immutableBitSet.iterator();

    assertThat(iterator)
        .hasNext();
    assertThat(iterator.next())
        .isEqualTo(0);
    assertThat(iterator)
        .hasNext();
    assertThat(iterator.next())
        .isEqualTo(Integer.MAX_VALUE);
    assertThat(iterator)
        .isExhausted();
  }

  @Test
  public void test_get() {
    BitSet bitSet = new BitSet(64);
    bitSet.set(1);
    bitSet.set(20);
    bitSet.set(64);
    ImmutableBitSet immutableBitSet = new ImmutableBitSet(bitSet);

    bitSet.set(42);

    assertThat(immutableBitSet.get(0))
        .isFalse();
    assertThat(immutableBitSet.get(1))
        .isTrue();
    assertThat(immutableBitSet.get(20))
        .isTrue();
    assertThat(immutableBitSet.get(64))
        .isTrue();
    for (int i = 2; i < 20; i++) {
      assertThat(immutableBitSet.get(i))
          .withFailMessage("Failed with i=" + i)
          .isFalse();
    }
    for (int i = 21; i < 64; i++) {
      assertThat(immutableBitSet.get(i))
          .withFailMessage("Failed with i=" + i)
          .isFalse();
    }
  }

  @Test
  public void test_get_0() {
    BitSet bitSet = new BitSet(0);
    bitSet.set(0);
    ImmutableBitSet immutableBitSet = new ImmutableBitSet(bitSet);

    assertThat(immutableBitSet.get(0))
        .isTrue();
  }

  @Test
  public void test_get_integerMaxValue() {
    BitSet bitSet = new BitSet(Integer.MAX_VALUE);
    bitSet.set(Integer.MAX_VALUE);
    ImmutableBitSet immutableBitSet = new ImmutableBitSet(bitSet);

    assertThat(immutableBitSet.get(Integer.MAX_VALUE))
        .isTrue();
  }

  @Test
  public void test_equalsHashCode() {
    EqualsVerifier.forClass(ImmutableBitSet.class)
        .verify();
  }
}
