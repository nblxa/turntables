package io.github.nblxa.turntables.io.rowstore;

import io.github.nblxa.turntables.Tab;

public abstract class RowStore {
  public abstract void feed(String name, Tab tab);
  public abstract Tab ingest(String name);
  public abstract void cleanUp(String name, CleanUpAction cleanUpAction);
}
