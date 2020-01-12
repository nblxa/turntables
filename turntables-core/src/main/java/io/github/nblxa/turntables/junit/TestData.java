package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.io.rowstore.RowStore;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestData implements TestRule {
  @NonNull
  private RowStore rowStore;
  @NonNull
  private Map<String, Tab> feeds;
  @NonNull
  private AtomicBoolean initialized;

  public TestData(@NonNull RowStore rowStore) {
    this.rowStore = Objects.requireNonNull(rowStore, "rowStore is null");
    this.feeds = new LinkedHashMap<>();
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
  public TestData table(@NonNull String tableName, @NonNull Tab data) {
    Objects.requireNonNull(tableName, "tableName is null");
    Objects.requireNonNull(data, "data is null");
    if (initialized.get()) {
      throw new IllegalStateException("Already initialized.");
    }
    feeds.put(tableName, data);
    return this;
  }

  public void feed(@NonNull String tableName, @NonNull Tab data) {
    Objects.requireNonNull(tableName, "tableName is null");
    Objects.requireNonNull(data, "data is null");
    if (!initialized.get()) {
      throw new IllegalStateException("Not initialized");
    }
    rowStore.feed(tableName, data);
  }

  @NonNull
  public Tab injest(@NonNull String tableName) {
    Objects.requireNonNull(tableName, "tableName is null");
    if (!initialized.get()) {
      throw new IllegalStateException("Not initialized");
    }
    return rowStore.injest(tableName);
  }

  public void init() {
    if (!initialized.compareAndSet(false, true)) {
      return;
    }
    for (Map.Entry<String, Tab> feedEntry : feeds.entrySet()) {
      feed(feedEntry.getKey(), feedEntry.getValue());
    }
  }
}
