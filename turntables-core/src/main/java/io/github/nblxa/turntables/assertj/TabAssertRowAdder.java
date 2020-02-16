package io.github.nblxa.turntables.assertj;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.nblxa.turntables.Tab;

public class TabAssertRowAdder<T extends Tab>
    implements Tab.RowAdder<TabAssertRowAdder<T>>,
               AsExpected<T> {
  @NonNull
  private final TabAssert<T> tabAssert;
  @NonNull
  private final Tab.RowAdder<?> rowAdder;

  TabAssertRowAdder(@NonNull TabAssert<T> tabAssert, @NonNull Tab.RowAdder<?> rowAdder) {
    this.tabAssert = tabAssert;
    this.rowAdder = rowAdder;
  }

  @NonNull
  @Override
  public TabAssertRowAdder<T> row(@Nullable Object first, @NonNull Object... rest) {
    rowAdder.row(first, rest);
    return this;
  }

  @NonNull
  @Override
  public Tab tab() {
    throw new UnsupportedOperationException();
  }

  @NonNull
  @Override
  public TabAssert<T> asExpected() {
    return tabAssert.matchesExpected(rowAdder.tab());
  }
}
