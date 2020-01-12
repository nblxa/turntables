package io.github.nblxa.turntables.io.rowstore;

import io.github.nblxa.turntables.Tab;

public abstract class RowStore {
  public abstract void feed(String name, Tab tab);
  public abstract Tab injest(String name);
}
