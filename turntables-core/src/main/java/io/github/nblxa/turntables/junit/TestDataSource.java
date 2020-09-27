package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TestTable;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;
import io.github.nblxa.turntables.io.rowstore.RowStore;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.runners.model.FrameworkMethod;

public class TestDataSource extends AbstractMethodRule {
  @NonNull
  private final RowStore rowStore;
  @NonNull
  private final Map<String, TestTableDetail> tablesFromAnnotations;
  @NonNull
  private final List<TestTableDetail> tablesFromMethods;
  @NonNull
  private final Settings settings;

  public TestDataSource(@NonNull RowStore rowStore) {
    this(Objects.requireNonNull(rowStore, "rowStore is null"), rowStore.defaultSettings());
  }

  public TestDataSource(@NonNull RowStore rowStore, @NonNull Settings settings) {
    this.rowStore = Objects.requireNonNull(rowStore, "rowStore is null");
    this.tablesFromAnnotations = new LinkedHashMap<>();
    this.tablesFromMethods = new ArrayList<>();
    this.settings = Objects.requireNonNull(settings, "settings is null");
  }

  public void feed(@NonNull String tableName, @NonNull Tab data) {
    Objects.requireNonNull(tableName, "tableName is null");
    Objects.requireNonNull(data, "data is null");
    final TestTableDetail testTableDetail;
    if (tablesFromAnnotations.containsKey(tableName)) {
      testTableDetail = tablesFromAnnotations.get(tableName);
      rowStore.cleanUp(testTableDetail.getName(), CleanUpAction.TRUNCATE);
    } else {
      // ensure the table is cleaned up after the test
      testTableDetail = new TestTableDetail(data, tableName, CleanUpAction.DROP);
      tablesFromMethods.add(testTableDetail);
    }
    rowStore.feed(testTableDetail.getName(), data);
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

  void addTableFromAnnotations(@NonNull TestTableDetail testTable) {
    tablesFromAnnotations.compute(testTable.getName(), (name, table) -> {
      if (table != null) {
        throw new IllegalStateException("TestTable " + name + " is already defined!");
      }
      return testTable;
    });
  }

  @Override
  protected void setUp(@NonNull FrameworkMethod method, @NonNull Object target) {
    Turntables.setSettings(settings);
    addTablesFromFields(target);
    for (TestTableDetail testTableDetail: tablesFromAnnotations.values()) {
      rowStore.feed(testTableDetail.getName(), testTableDetail.getTab());
    }
    tablesFromMethods.clear();
  }

  @Override
  protected void tearDown(@NonNull FrameworkMethod method, @NonNull Object target) {
    Iterator<TestTableDetail> iter = tablesFromMethods.iterator();
    while (iter.hasNext()) {
      TestTableDetail testTableDetail = iter.next();
      cleanUp(testTableDetail.getName(), testTableDetail.getCleanUpAction());
      iter.remove();
    }
    Iterator<Map.Entry<String, TestTableDetail>> entryIter = tablesFromAnnotations.entrySet()
        .iterator();
    while (entryIter.hasNext()) {
      TestTableDetail testTableDetail = entryIter.next().getValue();
      cleanUp(testTableDetail.getName(), testTableDetail.getCleanUpAction());
      entryIter.remove();
    }
    Turntables.rollbackSettings();
  }

  private void addTablesFromFields(@NonNull Object target) {
    for (Field f : target.getClass().getFields()) {
      for (TestTable testTable: f.getAnnotationsByType(TestTable.class)) {
        try {
          addTab(f.get(target), testTable);
        } catch (IllegalAccessException e) {
          // should not end up here as getFields() returns only accessible ones
        }
      }
    }
  }

  private void addTab(Object o, TestTable testTab) {
    if (o instanceof Tab) {
      Tab tab = (Tab) o;
      TestTableDetail d = new TestTableDetail(tab, getTestTableName(testTab), testTab.cleanUpAction());
      addTableFromAnnotations(d);
    }
  }

  private static String getTestTableName(TestTable testData) {
    if (!testData.name().equals("")) {
      return testData.name();
    } else {
      return testData.value();
    }
  }
}
