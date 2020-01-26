package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;

public class UnnamedColTestTable extends AbstractTestTable<UnnamedColTestTable, UnnamedColTestTable>
    implements Tab.UnnamedColAdder<UnnamedColTestTable, UnnamedColTestTable> {

  @NonNull
  private final TableUtils.UnnamedColAdderTable colAdder = new TableUtils.UnnamedColAdderTable();

  public UnnamedColTestTable(@NonNull TestDataSource testDataSource, @NonNull String tableName,
                             @NonNull CleanUpAction cleanUpAction) {
    super(testDataSource, tableName, cleanUpAction);
  }

  @NonNull
  @Override
  public UnnamedColTestTable col(@NonNull Typ typ) {
    colAdder.col(typ);
    return this;
  }

  @NonNull
  @Override
  public UnnamedColTestTable key(@NonNull Typ typ) {
    colAdder.key(typ);
    return this;
  }

  @Override
  protected void initEmpty() {
    testDataSource.feed(tableName, colAdder.tab());
  }

  @NonNull
  @Override
  public UnnamedColTestTable rowAdder() {
    if (rowAdder == null) {
      rowAdder = colAdder.rowAdder();
    }
    return this;
  }
}
