package io.github.nblxa.turntables.assertj;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;

public abstract class AbstractTabAssertColAdder<T extends Tab, A extends TableUtils.AbstractColRowAdderTable>
    implements Tab.RowAdder<TabAssertRowAdder<T>>,
               Tab.ColAdderRowAdderPart<TabAssertRowAdder<T>>,
               AsExpected<T> {
  @NonNull
  private final TabAssert<T> tabAssert;
  @NonNull
  private final A colAdder;

  AbstractTabAssertColAdder(@NonNull TabAssert<T> tabAssert, @NonNull A colAdder) {
    this.tabAssert = tabAssert;
    this.colAdder = colAdder;
  }

  @NonNull
  @Override
  public TabAssertRowAdder<T> row(@Nullable Object first, @NonNull Object... rest) {
    return new TabAssertRowAdder<>(tabAssert, colAdder.row(first, rest));
  }

  @NonNull
  @Override
  public Tab tab() {
    throw new UnsupportedOperationException();
  }

  @NonNull
  @Override
  public TabAssertRowAdder<T> rowAdder() {
    return new TabAssertRowAdder<>(tabAssert, colAdder.rowAdder());
  }

  @NonNull
  @Override
  public TabAssert<T> asExpected() {
    return tabAssert.matchesExpected(colAdder.tab());
  }
}
