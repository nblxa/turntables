package io.github.nblxa.turntables.junit;

import java.util.ArrayList;
import java.util.List;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractMethodRule implements MethodRule {
  @NonNull
  @Override
  public Statement apply(@NonNull Statement base, @NonNull FrameworkMethod method,
                         @NonNull Object target) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        List<Throwable> errors = new ArrayList<>();
        try {
          setUp(method, target);
          base.evaluate();
        } catch (Exception | AssertionError t) {
          errors.add(t);
        } finally {
          try {
            tearDown(method, target);
          } catch (Exception e) {
            errors.add(e);
          }
        }
        MultipleFailureException.assertEmpty(errors);
      }
    };
  }

  protected abstract void setUp(@NonNull FrameworkMethod method, @NonNull Object target);

  protected abstract void tearDown(@NonNull FrameworkMethod method, @NonNull Object target);
}
