package io.github.nblxa.turntables.io.rowstore;

import io.github.nblxa.turntables.Tab;

public interface RowStore {
  void feed(String name, Tab tab);
  Tab ingest(String name);
  void cleanUp(String name, CleanUpAction cleanUpAction);
}
