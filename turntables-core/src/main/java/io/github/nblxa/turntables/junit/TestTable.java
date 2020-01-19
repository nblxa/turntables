package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestTable implements Tab.ColAdder<TestTable, TestTable>, Tab.RowAdder<TestTable>,
                                  TestRule {
  @NonNull
  private final TestDataSource testDataSource;
  @NonNull
  private final String tableName;
  @NonNull
  private final TableUtils.ColAdder colAdder;
  @Nullable
  private TableUtils.RowAdderTable rowAdder = null;
  private CleanUpAction cleanUpAction = CleanUpAction.DROP;

  public TestTable(@NonNull TestDataSource testDataSource, @NonNull String tableName) {
    this.testDataSource = Objects.requireNonNull(testDataSource, "testData is null");
    this.tableName = Objects.requireNonNull(tableName, "tableName is null");
    this.colAdder = new TableUtils.ColAdder();
  }

  @NonNull
  public Tab ingest() {
    return testDataSource.ingest(tableName);
  }

  @NonNull
  public TestTable cleanupAfterTest(@NonNull CleanUpAction cleanUpAction) {
    this.cleanUpAction = Objects.requireNonNull(cleanUpAction, "cleanupAction is null");
    return this;
  }

  @NonNull
  @Override
  public TestTable col(@NonNull Typ typ) {
    colAdder.col(typ);
    return this;
  }

  @NonNull
  @Override
  public TestTable col(@NonNull String name, @NonNull Typ typ) {
    colAdder.col(name, typ);
    return this;
  }

  @NonNull
  @Override
  public TestTable key(@NonNull String name, @NonNull Typ typ) {
    colAdder.key(name, typ);
    return this;
  }

  @NonNull
  @Override
  public TestTable key(@NonNull Typ typ) {
    colAdder.key(typ);
    return this;
  }

  @NonNull
  @Override
  public TestTable rowAdder() {
    if (rowAdder == null) {
      rowAdder = colAdder.rowAdder();
    }
    return this;
  }

  @NonNull
  @Override
  public TestTable row(@Nullable Object first, @NonNull Object... rest) {
    rowAdder();
    rowAdder.row(first, rest);
    return this;
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

  protected void init() {
    if (rowAdder != null) {
      testDataSource.feed(tableName, rowAdder.tab());
    } else {
      testDataSource.feed(tableName, colAdder.tab());
    }
  }

  protected void cleanup() {
    testDataSource.cleanUp(tableName, cleanUpAction);
  }
}
