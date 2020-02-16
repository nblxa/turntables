package io.github.nblxa.turntables.assertj;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Typ;

public class TabAssertUnnamedColAdder<T extends Tab>
    extends AbstractTabAssertColAdder<T, TableUtils.UnnamedColAdderTable>
    implements Tab.UnnamedColAdder<TabAssertUnnamedColAdder<T>, TabAssertRowAdder<T>>,
               Tab.RowAdder<TabAssertRowAdder<T>>,
               AsExpected<T> {
  @NonNull
  private final TableUtils.UnnamedColAdderTable colAdder;

  TabAssertUnnamedColAdder(@NonNull TabAssert<T> tabAssert,
                           @NonNull TableUtils.UnnamedColAdderTable colAdder) {
    super(tabAssert, colAdder);
    this.colAdder = colAdder;
  }

  @NonNull
  @Override
  public TabAssertUnnamedColAdder<T> col(@NonNull Typ typ) {
    colAdder.col(typ);
    return this;
  }

  @NonNull
  @Override
  public TabAssertUnnamedColAdder<T> key(@NonNull Typ typ) {
    colAdder.key(typ);
    return this;
  }
}
