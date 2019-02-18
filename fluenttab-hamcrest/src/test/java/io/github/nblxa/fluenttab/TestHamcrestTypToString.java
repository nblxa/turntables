package io.github.nblxa.fluenttab;

import static org.junit.Assert.assertEquals;

import org.hamcrest.Matchers;
import org.junit.Test;

public class TestHamcrestTypToString {
  @Test
  public void testMatcher() {
    Tab.Val val = new MatcherVal(Matchers.anything());
    String act = val.toString();
    assertEquals("org.hamcrest.core.IsAnything: ANYTHING", act);
  }
}
