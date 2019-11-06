package io.github.nblxa.turntables.assertion;

import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;

final class ImmutableBitSet implements Iterable<Integer> {
  static final ImmutableBitSet EMPTY = new ImmutableBitSet(new BitSet());

  private final BitSet bitSet;

  ImmutableBitSet(BitSet bitSet) {
    BitSet clone = (BitSet) bitSet.clone();
    this.bitSet = Objects.requireNonNull(clone, "bitSet");
  }

  private ImmutableBitSet(BitSet bitSet, boolean noClone) {
    assert noClone;
    this.bitSet = bitSet;
  }

  public boolean get(final int bit) {
    return bitSet.get(bit);
  }

  public ImmutableBitSet set(final int bit) {
    return newImmutable(bs -> bs.set(bit));
  }

  public ImmutableBitSet andNot(final ImmutableBitSet other) {
    return newImmutable(bs -> bs.andNot(other.bitSet));
  }

  public Iterator<Integer> iterator() {
    return new ImmutableBitSetIterator();
  }

  public int cardinality() {
    return bitSet.cardinality();
  }

  public boolean isEmpty() {
    return bitSet.isEmpty();
  }

  private ImmutableBitSet newImmutable(Consumer<BitSet> bitSetConsumer) {
    BitSet clone = (BitSet) bitSet.clone();
    bitSetConsumer.accept(clone);
    return new ImmutableBitSet(clone, true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ImmutableBitSet)) {
      return false;
    }
    ImmutableBitSet that = (ImmutableBitSet) o;
    return bitSet.equals(that.bitSet);
  }

  @Override
  public int hashCode() {
    return bitSet.hashCode();
  }

  @Override
  public String toString() {
    return bitSet.toString();
  }

  class ImmutableBitSetIterator implements PrimitiveIterator.OfInt {
    int curr = 0;
    int next = bitSet.nextSetBit(0);

    @Override
    public boolean hasNext() {
      return next != -1 && curr != Integer.MAX_VALUE;
    }

    @Override
    public int nextInt() {
      if (next != -1 && curr != Integer.MAX_VALUE) {
        curr = next;
        if (curr != Integer.MAX_VALUE) {
          next = bitSet.nextSetBit(curr + 1);
        }
        return curr;
      } else {
        throw new NoSuchElementException();
      }
    }
  }
}
