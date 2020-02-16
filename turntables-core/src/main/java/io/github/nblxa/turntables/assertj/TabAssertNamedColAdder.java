package io.github.nblxa.turntables.assertj;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Typ;

public class TabAssertNamedColAdder<T extends Tab>
    extends AbstractTabAssertColAdder<T, TableUtils.NamedColAdderTable>
    implements Tab.NamedColAdder<TabAssertNamedColAdder<T>, TabAssertRowAdder<T>>,
               Tab.RowAdder<TabAssertRowAdder<T>>,
               AsExpected<T> {
  @NonNull
  private final TableUtils.NamedColAdderTable colAdder;

  TabAssertNamedColAdder(@NonNull TabAssert<T> tabAssert,
                         @NonNull TableUtils.NamedColAdderTable colAdder) {
    super(tabAssert, colAdder);
    this.colAdder = colAdder;
  }

  @NonNull
  @Override
  public TabAssertNamedColAdder<T> col(@NonNull String name, @NonNull Typ typ) {
    colAdder.col(name, typ);
    return this;
  }

  @NonNull
  @Override
  public TabAssertNamedColAdder<T> key(@NonNull String name, @NonNull Typ typ) {
    colAdder.key(name, typ);
    return this;
  }
}
