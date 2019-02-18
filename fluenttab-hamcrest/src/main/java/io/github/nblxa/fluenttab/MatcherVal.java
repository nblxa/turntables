package io.github.nblxa.fluenttab;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Objects;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

public final class MatcherVal extends AbstractTab.AbstractAssertionVal {
  @NonNull
  private final Matcher matcher;

  MatcherVal(Matcher<?> matcher) {
    super(matcher::matches, () -> {
      Description description = new StringDescription();
      matcher.describeTo(description);
      return matcher.getClass().getCanonicalName() + ": "
          + StringUtils.escape(description.toString());
    });
    this.matcher = Objects.requireNonNull(matcher, "matcher");
  }

  @NonNull
  public Matcher getMatcher() {
    return matcher;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MatcherVal)) {
      return false;
    }
    MatcherVal that = (MatcherVal) o;
    return that.canEqual(this) && matcher.equals(that.matcher);
  }

  @Override
  public int hashCode() {
    return Objects.hash(matcher);
  }

  @Override
  public boolean canEqual(Object o) {
    return o instanceof MatcherVal;
  }
}
