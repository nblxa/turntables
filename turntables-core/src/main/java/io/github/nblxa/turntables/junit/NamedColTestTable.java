package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;

public class NamedColTestTable extends AbstractTestTable<NamedColTestTable, NamedColTestTable>
    implements Tab.NamedColAdder<NamedColTestTable, NamedColTestTable> {

  @NonNull
  private final TableUtils.NamedColAdderTable colAdder = new TableUtils.NamedColAdderTable();

  public NamedColTestTable(@NonNull TestDataSource testDataSource, @NonNull String tableName,
                           @NonNull CleanUpAction cleanUpAction) {
    super(testDataSource, tableName, cleanUpAction);
  }

  @NonNull
  @Override
  public NamedColTestTable col(@NonNull String name, @NonNull Typ typ) {
    colAdder.col(name, typ);
    return this;
  }

  @NonNull
  @Override
  public NamedColTestTable key(@NonNull String name, @NonNull Typ typ) {
    colAdder.key(name, typ);
    return this;
  }

  @Override
  protected void initEmpty() {
    testDataSource.feed(tableName, colAdder.tab());
  }

  @NonNull
  @Override
  public NamedColTestTable rowAdder() {
    if (rowAdder == null) {
      rowAdder = colAdder.rowAdder();
    }
    return this;
  }
}
