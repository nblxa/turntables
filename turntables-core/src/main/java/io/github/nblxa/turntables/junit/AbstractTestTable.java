package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractTestTable<SELF extends AbstractTestTable<SELF, T>, T extends Tab.RowAdder<SELF>>
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

  @NonNull
  @Override
  public Tab ingest() {
    return testDataSource.ingest(tableName);
  }

  @NonNull
  public SELF cleanupAfterTest(@NonNull CleanUpAction cleanUpAction) {
    this.cleanUpAction = Objects.requireNonNull(cleanUpAction, "cleanupAction is null");
    return self;
  }

  @NonNull
  @Override
  public SELF row(@Nullable Object first, @NonNull Object... rest) {
    rowAdder();
    rowAdder.row(first, rest);
    return self;
  }

  @NonNull
  @Override
  public Tab tab() {
    return rowAdder.tab();
  }

  @NonNull
  @Override
  public Statement apply(@NonNull Statement base, @NonNull Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        List<Throwable> errors = new ArrayList<>();
        try {
          init();
          base.evaluate();
        } catch (Throwable e) {
          errors.add(e);
        } finally {
          cleanup();
        }
        MultipleFailureException.assertEmpty(errors);
      }
    };
  }

  @NonNull
  @Override
  public SELF rowAdder() {
    throw new IllegalStateException();
  }

  protected void init() {
    if (rowAdder != null) {
      testDataSource.feed(tableName, rowAdder.tab());
    } else {
      initEmpty();
    }
  }

  protected abstract void initEmpty();

  protected void cleanup() {
    testDataSource.cleanUp(tableName, cleanUpAction);
  }
}
