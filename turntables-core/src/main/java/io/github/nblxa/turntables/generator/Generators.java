package io.github.nblxa.turntables.generator;

import io.github.nblxa.turntables.DequeThreadLocal;

public class Generators {
  private static final DequeThreadLocal<Coordinate> COORDINATES = new DequeThreadLocal<Coordinate>() {
    @Override
    public Coordinate dequeTopValue() {
      return null;
    }
  };

  private Generators() {
  }
}
