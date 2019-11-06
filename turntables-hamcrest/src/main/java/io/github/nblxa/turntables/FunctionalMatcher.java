package io.github.nblxa.turntables;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class FunctionalMatcher<T> extends TypeSafeDiagnosingMatcher<T> {
  private final BiPredicate<T, Description> matchesSafely;
  private final Consumer<Description> describeTo;

  private FunctionalMatcher(BiPredicate<T, Description> matchesSafely, Consumer<Description> describeTo) {
    this.matchesSafely = Objects.requireNonNull(matchesSafely, "matchesSafely");
    this.describeTo = Objects.requireNonNull(describeTo, "describeTo");
  }

  public static <U> FunctionalMatcher<U> matcher(BiPredicate<U, Description> matchesSafely,
                                                 Consumer<Description> describeTo) {
    return new FunctionalMatcher<>(matchesSafely, describeTo);
  }

  public static <U> FunctionalMatcher<U> matcher(Predicate<U> matchesSafely, BiConsumer<U, Description> describeMismatch,
                                                 Consumer<Description> describeTo) {
    return matcher((item, mismatchDescription) -> {
      if (matchesSafely.test(item)) {
        return true;
      } else {
        describeMismatch.accept(item, mismatchDescription);
        return false;
      }
    }, describeTo);
  }

  @Override
  protected boolean matchesSafely(T item, Description mismatchDescription) {
    return matchesSafely.test(item, mismatchDescription);
  }

  @Override
  public void describeTo(Description description) {
    describeTo.accept(description);
  }
}
