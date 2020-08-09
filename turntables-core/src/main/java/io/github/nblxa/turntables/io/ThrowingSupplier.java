package io.github.nblxa.turntables.io;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {
  T get() throws E;
}
