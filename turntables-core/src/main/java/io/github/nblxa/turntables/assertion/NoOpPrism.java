package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import java.util.List;

public class NoOpPrism extends Prism {
  @NonNull
  private final List<Tab.Row> rows;

  static Prism of(@NonNull Tab tab) {
    return new NoOpPrism(tab);
  }

  NoOpPrism(@NonNull Tab tab) {
    super(tab.cols());
    this.rows = tab.rows();
  }

  @NonNull
  @Override
  public List<Row> rows() {
    return rows;
  }
}
