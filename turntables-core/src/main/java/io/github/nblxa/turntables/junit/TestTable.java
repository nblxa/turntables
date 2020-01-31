package io.github.nblxa.turntables.junit;

import io.github.nblxa.turntables.Tab;
import org.junit.rules.TestRule;

/**
 * TestTable is a JUnit {@link TestRule} that provides a way to set up test data
 * in an external location (e.g. database table) and load results from it.
 *
 * When creating a TestTable, it is possible to use the fluent API to define the initial data
 * to be made available before testing:
 *
 * <pre>{@code
 *     private TestTable testTab = testDataSource.table("employees")
 *       .col("id", Typ.INTEGER).col("name", Typ.STRING).col("dept", Typ.STRING)
 *       .row(1, "Alice", "Dev")
 *       .row(2, "Bob", "Ops")
 *       .cleanupAfterTest(CleanUpAction.DROP);
 * }</pre>
 *
 * in the end, a clean-up action can be defined to be executed after testing.
 */
public interface TestTable extends TestRule {
  Tab ingest();
}
