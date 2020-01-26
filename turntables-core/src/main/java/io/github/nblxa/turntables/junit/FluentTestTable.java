package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;

public class FluentTestTable extends AbstractTestTable<FluentTestTable, FluentTestTable>
                       implements Tab.UnnamedColAdderPart<UnnamedColTestTable>,
                                  Tab.NamedColAdderPart<NamedColTestTable> {

  public FluentTestTable(@NonNull TestDataSource testDataSource, @NonNull String tableName) {
    super(testDataSource, tableName, CleanUpAction.DROP);
  }

  @NonNull
  @Override
  public UnnamedColTestTable col(@NonNull Typ typ) {
    return new UnnamedColTestTable(testDataSource, tableName, cleanUpAction).col(typ);
  }

  @NonNull
  @Override
  public NamedColTestTable col(@NonNull String name, @NonNull Typ typ) {
    return new NamedColTestTable(testDataSource, tableName, cleanUpAction).col(name, typ);
  }

  @NonNull
  @Override
  public UnnamedColTestTable key(@NonNull Typ typ) {
    return new UnnamedColTestTable(testDataSource, tableName, cleanUpAction).key(typ);
  }

  @NonNull
  @Override
  public NamedColTestTable key(@NonNull String name, @NonNull Typ typ) {
    return new NamedColTestTable(testDataSource, tableName, cleanUpAction).key(name, typ);
  }

  @Override
  protected void initEmpty() {
    testDataSource.feed(tableName, Turntables.tab());
  }
}
