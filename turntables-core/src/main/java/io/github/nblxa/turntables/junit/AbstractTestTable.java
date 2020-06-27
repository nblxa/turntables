package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import java.util.Objects;

public abstract class AbstractTestTable<SELF extends AbstractTestTable<SELF, T>,
                                        T extends Tab.RowAdder<SELF>>
    extends AbstractTestRule
    implements Tab.ColAdderRowAdderPart<SELF>,
               Tab.RowAdder<SELF>,
               TestTable {
  @NonNull
  protected final TestDataSource testDataSource;
  @NonNull
  protected final String tableName;
  @NonNull
  private final SELF self;
  @NonNull
  protected CleanUpAction cleanUpAction;
  @Nullable
  protected TableUtils.RowAdderTable rowAdder = null;

  @SuppressWarnings("unchecked")
  public AbstractTestTable(@NonNull TestDataSource testDataSource, @NonNull String tableName,
                           @NonNull CleanUpAction cleanUpAction) {
    this.testDataSource = Objects.requireNonNull(testDataSource, "testData is null");
    this.tableName = Objects.requireNonNull(tableName, "tableName is null");
    this.cleanUpAction = Objects.requireNonNull(cleanUpAction, "cleanUpAction is null");
    this.self = (SELF) this;
  }

  /**
   * Set the clean-up action to be performed for this table.
   * @param cleanUpAction the clean-up action to set
   * @return the assertion object
   */
  @NonNull
  public SELF cleanUpAfterTest(@NonNull CleanUpAction cleanUpAction) {
    this.cleanUpAction = Objects.requireNonNull(cleanUpAction, "cleanUpAction is null");
    return self;
  }

  @NonNull
  @Override
  public Tab ingest() {
    return testDataSource.ingest(tableName);
  }

  @NonNull
  @Override
  public SELF row(@Nullable Object first, @NonNull Object... rest) {
    getOrCreateRowAdderTable().row(first, rest);
    return self;
  }

  @NonNull
  @Override
  public Tab tab() {
    return getOrCreateRowAdderTable().tab();
  }

  @NonNull
  @Override
  public CleanUpAction getCleanUpAction() {
    return cleanUpAction;
  }

  @NonNull
  @Override
  public String getName() {
    return tableName;
  }

  @NonNull
  @Override
  public SELF rowAdder() {
    getOrCreateRowAdderTable();
    return self;
  }

  void setUpEmpty() {
    testDataSource.addTestRuleTable(this);
    testDataSource.feedInternal(this, colRowAdderTable().tab());
  }

  @NonNull
  TableUtils.RowAdderTable getOrCreateRowAdderTable() {
    if (rowAdder == null) {
      rowAdder = colRowAdderTable().rowAdder();
    }
    return rowAdder;
  }

  @NonNull
  abstract TableUtils.AbstractColRowAdderTable colRowAdderTable();

  @Override
  protected void setUp() {
    if (rowAdder != null) {
      testDataSource.addTestRuleTable(this);
      testDataSource.feedInternal(this, rowAdder.tab());
    } else {
      setUpEmpty();
    }
  }

  @Override
  protected void tearDown() {
    testDataSource.cleanUp(tableName, cleanUpAction);
  }
}
