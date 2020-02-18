package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;

public class SimpleTable implements Table {
  @NonNull
  private final Tab tab;
  @NonNull
  private final String name;
  @NonNull
  private final CleanUpAction cleanUpAction;

  public SimpleTable(@NonNull Tab tab, @NonNull String name, @NonNull CleanUpAction cleanUpAction) {
    this.tab = tab;
    this.name = name;
    this.cleanUpAction = cleanUpAction;
  }

  @NonNull
  @Override
  public String getName() {
    return name;
  }

  @NonNull
  @Override
  public CleanUpAction getCleanUpAction() {
    return cleanUpAction;
  }
}
