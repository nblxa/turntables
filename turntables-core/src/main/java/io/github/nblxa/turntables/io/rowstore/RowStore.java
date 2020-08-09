package io.github.nblxa.turntables.io.rowstore;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;

public interface RowStore {
  void feed(@NonNull String name, @NonNull Tab tab);

  @NonNull
  Tab ingest(@NonNull String name);

  void cleanUp(@NonNull String name, @NonNull CleanUpAction cleanUpAction);

  @NonNull
  default Settings defaultSettings() {
    return Turntables.getSettings();
  }
}
