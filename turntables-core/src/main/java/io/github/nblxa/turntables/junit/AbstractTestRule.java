package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTestRule implements TestRule {
  @NonNull
  @Override
  public Statement apply(@NonNull Statement base, @NonNull Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        List<Throwable> errors = new ArrayList<>();
        try {
          setUp();
          base.evaluate();
        } catch (Exception | AssertionError t) {
          errors.add(t);
        } finally {
          try {
            tearDown();
          } catch (Exception e) {
            errors.add(e);
          }
        }
        MultipleFailureException.assertEmpty(errors);
      }
    };
  }

  protected abstract void setUp();

  protected abstract void tearDown();
}
