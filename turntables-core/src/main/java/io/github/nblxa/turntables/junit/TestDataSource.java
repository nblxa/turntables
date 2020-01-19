package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import io.github.nblxa.turntables.io.rowstore.RowStore;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestDataSource implements TestRule {
  @NonNull
  private RowStore rowStore;
  @NonNull
  private AtomicBoolean initialized;

  public TestDataSource(@NonNull RowStore rowStore) {
    this.rowStore = Objects.requireNonNull(rowStore, "rowStore is null");
    this.initialized = new AtomicBoolean(false);
  }

  @NonNull
  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        List<Throwable> errors = new ArrayList<>();
        try {
          init();
          base.evaluate();
        } catch (Throwable e) {
          errors.add(e);
        }
        MultipleFailureException.assertEmpty(errors);
      }
    };
  }

  @NonNull
  public TestTable table(@NonNull String tableName) {
    Objects.requireNonNull(tableName, "tableName is null");
    if (initialized.get()) {
      throw new IllegalStateException("Already initialized.");
    }
    return new TestTable(this, tableName);
  }

  public void feed(@NonNull String tableName, @NonNull Tab data) {
    Objects.requireNonNull(tableName, "tableName is null");
    Objects.requireNonNull(data, "data is null");
    checkIfInitialized();
    rowStore.feed(tableName, data);
  }

  @NonNull
  public Tab ingest(@NonNull String tableName) {
    Objects.requireNonNull(tableName, "tableName is null");
    checkIfInitialized();
    return rowStore.ingest(tableName);
  }

  public void cleanUp(@NonNull String tableName, @NonNull CleanUpAction cleanUpAction) {
    Objects.requireNonNull(tableName, "tableName is null");
    Objects.requireNonNull(cleanUpAction, "cleanUpAction is null");
    checkIfInitialized();
    rowStore.cleanUp(tableName, cleanUpAction);
  }

  private void init() {
    initialized.compareAndSet(false, true);
  }

  private void checkIfInitialized() {
    if (!initialized.get()) {
      throw new IllegalStateException("Not initialized");
    }
  }
}
