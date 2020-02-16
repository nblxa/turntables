package io.github.nblxa.turntables.assertj;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Typ;

public class TabAssertColAdder<T extends Tab>
    extends AbstractTabAssertColAdder<T, TableUtils.ColAdderTable>
    implements Tab.ColAdder<TabAssertUnnamedColAdder<T>, TabAssertNamedColAdder<T>, TabAssertRowAdder<T>>,
               Tab.RowAdder<TabAssertRowAdder<T>>,
               AsExpected<T> {
  @NonNull
  private final TabAssert<T> tabAssert;
  @NonNull
  private final TableUtils.ColAdderTable colAdder;

  TabAssertColAdder(@NonNull TabAssert<T> tabAssert, @NonNull TableUtils.ColAdderTable colAdder) {
    super(tabAssert, colAdder);
    this.tabAssert = tabAssert;
    this.colAdder = colAdder;
  }

  @NonNull
  @Override
  public TabAssertUnnamedColAdder<T> col(@NonNull Typ typ) {
    return new TabAssertUnnamedColAdder<>(tabAssert, colAdder.col(typ));
  }

  @NonNull
  @Override
  public TabAssertUnnamedColAdder<T> key(@NonNull Typ typ) {
    return new TabAssertUnnamedColAdder<>(tabAssert, colAdder.key(typ));
  }

  @NonNull
  @Override
  public TabAssertNamedColAdder<T> col(@NonNull String name, @NonNull Typ typ) {
    return new TabAssertNamedColAdder<>(tabAssert, colAdder.col(name, typ));
  }

  @NonNull
  @Override
  public TabAssertNamedColAdder<T> key(@NonNull String name, @NonNull Typ typ) {
    return new TabAssertNamedColAdder<>(tabAssert, colAdder.key(name, typ));
  }
}
