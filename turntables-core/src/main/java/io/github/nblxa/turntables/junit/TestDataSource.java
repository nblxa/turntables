package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import io.github.nblxa.turntables.io.rowstore.RowStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

public class TestDataSource extends AbstractTestRule {
  @NonNull
  private final RowStore rowStore;
  @NonNull
  private final Map<String, TestTable> testRuleTables;
  @NonNull
  private final List<Table> testMethodTables;
  @NonNull
  private final Settings settings;

  public TestDataSource(@NonNull RowStore rowStore) {
    this.rowStore = Objects.requireNonNull(rowStore, "rowStore is null");
    this.testRuleTables = new HashMap<>();
    this.testMethodTables = new ArrayList<>();
    this.settings = rowStore.defaultSettings();
  }

  public TestDataSource(@NonNull RowStore rowStore, @NonNull Settings settings) {
    this.rowStore = Objects.requireNonNull(rowStore, "rowStore is null");
    this.testRuleTables = new HashMap<>();
    this.testMethodTables = new ArrayList<>();
    this.settings = Objects.requireNonNull(settings, "settings is null");
  }

  @NonNull
  public FluentTestTable table(@NonNull String tableName) {
    Objects.requireNonNull(tableName, "tableName is null");
    return new FluentTestTable(this, tableName);
  }

  public void feed(@NonNull String tableName, @NonNull Tab data) {
    Objects.requireNonNull(tableName, "tableName is null");
    Objects.requireNonNull(data, "data is null");
    final Table table;
    if (testRuleTables.containsKey(tableName)) {
      // the table will be cleaned up by its own TestRule
      table = testRuleTables.get(tableName);
    } else {
      // ensure the table is cleaned up after the test
      table = new SimpleTable(data, tableName, CleanUpAction.DROP);
      testMethodTables.add(table);
    }
    feedInternal(table, data);
  }

  @NonNull
  public Tab ingest(@NonNull String tableName) {
    Objects.requireNonNull(tableName, "tableName is null");
    Tab tab = rowStore.ingest(tableName);
    return Objects.requireNonNull(tab, "tab is null");
  }

  public void cleanUp(@NonNull String tableName, @NonNull CleanUpAction cleanUpAction) {
    Objects.requireNonNull(tableName, "tableName is null");
    Objects.requireNonNull(cleanUpAction, "cleanUpAction is null");
    rowStore.cleanUp(tableName, cleanUpAction);
  }

  @NonNull
  public TestDataSource settings(@NonNull Settings settings) {
    return new TestDataSource(rowStore, settings);
  }

  void feedInternal(@NonNull Table table, @NonNull Tab data) {
    rowStore.feed(table.getName(), data);
  }

  void addTestRuleTable(@NonNull TestTable testTable) {
    testRuleTables.compute(testTable.getName(), (name, table) -> {
      if (table != null) {
        throw new IllegalStateException("TestTable " + name + " is already defined!");
      }
      return testTable;
    });
  }

  @Override
  protected void setUp() {
    Turntables.setSettings(settings);
    testRuleTables.clear();
    testMethodTables.clear();
  }

  @Override
  protected void tearDown() {
    ListIterator<Table> iter = testMethodTables.listIterator();
    while (iter.hasNext()) {
      Table testTable = iter.next();
      cleanUp(testTable.getName(), testTable.getCleanUpAction());
      iter.remove();
    }
    Turntables.rollbackSettings();
  }
}
