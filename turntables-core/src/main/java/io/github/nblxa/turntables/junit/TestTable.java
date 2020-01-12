package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Typ;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import java.util.Objects;

public class TestTable implements Tab, Tab.ColAdder<TestTable>, TestRule {

  @NonNull
  private final TestData testData;
  @NonNull
  private final TableUtils.Builder builder;

  public TestTable(@NonNull TestData testData, @NonNull TableUtils.Builder builder) {
    this.testData = Objects.requireNonNull(testData, "testData is null");
    this.builder = Objects.requireNonNull(builder, "builder is null");
  }

  @NonNull
  @Override
  public Iterable<Col> cols() {
    return null;
  }

  @NonNull
  @Override
  public Iterable<Row> rows() {
    return null;
  }

  @NonNull
  @Override
  public TestTable col(@NonNull Typ typ) {
    return null;
  }

  @NonNull
  @Override
  public TestTable col(@NonNull String name, @NonNull Typ typ) {
    return null;
  }

  @NonNull
  @Override
  public TestTable key(@NonNull String name, @NonNull Typ typ) {
    return null;
  }

  @NonNull
  @Override
  public TestTable key(@NonNull Typ typ) {
    return null;
  }

  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        base.evaluate();
      }
    };
  }
}
