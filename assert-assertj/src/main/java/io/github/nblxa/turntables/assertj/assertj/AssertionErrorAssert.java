package io.github.nblxa.turntables.assertj.assertj;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.WritableAssertionInfo;
import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.internal.Objects;

/**
 * Assertion for instances of {@link AssertionError} that enable comparing the expected and actual
 * assertion error messages in the IDE.
 *
 * <p>This is done by amending the assertion keywords such as "EXPECTED" and "BUT: WAS"
 * in the compared messages so that the IDE does not confuse the keywords that appear in the original
 * messages being tested and the ones from the test itself.
 */
@SuppressWarnings("java:S2160")
public final class AssertionErrorAssert extends AbstractThrowableAssert<AssertionErrorAssert, Throwable> {
  private static final Pattern EXPECTED_BUT_WAS = Pattern.compile(
      "\\n(EXPECTED: ).*?\\n(BUT: WAS ).*", Pattern.DOTALL);

  public AssertionErrorAssert(Throwable actual) {
    super(actual, AssertionErrorAssert.class);
  }

  /**
   * Asserts that the given {@link Throwable} is an instance of {@link AssertionError} and that
   * its message is equal to the given expected one.
   *
   * <p>Assertion keywords in both the expected and the actual error messages are replaced so that
   * they do not confuse the IDEs when viewing the diff in case of test failure. This ensures
   * a clean diff that allows the developer to focus on the real difference.
   *
   * @param expectedMessage the exact expected message
   * @return the assertion object itself for method chaining
   */
  public AssertionErrorAssert isAssertionErrorWithMessage(CharSequence expectedMessage) {
    WritableAssertionInfo wrAsInfo = getWritableAssertionInfo();
    Objects.instance().assertNotNull(wrAsInfo, actual); // NOSONAR using actual as intended for assertj extensions
    objects.assertIsInstanceOf(wrAsInfo, actual, AssertionError.class);
    String expectedReplaced = replaceAssertionKeywords(expectedMessage);
    String actualReplaced = replaceAssertionKeywords(actual.getMessage());
    if (java.util.Objects.equals(expectedReplaced, actualReplaced)) return this;
    throw new AssertionError(
        new ShouldHaveAssertionMessage(expectedReplaced, actualReplaced).create());
  }

  public static class ShouldHaveAssertionMessage extends BasicErrorMessageFactory {
    private ShouldHaveAssertionMessage(CharSequence expectedMessage, CharSequence actualMessage) {
      super("%nAssertion error message mismatch.%n" +
              "Expected: %s%n" +
              "But was: %s%n", expectedMessage, actualMessage);
    }
  }

  static String replaceAssertionKeywords(CharSequence message) {
    Matcher m = EXPECTED_BUT_WAS.matcher(message);
    String result = message.toString();
    if (m.find()) {
      result = new StringBuilder(result).replace(m.start(2), m.end(2), "{{MSG_WAS}}").toString();
      result = new StringBuilder(result).replace(m.start(1), m.end(1), "{{MSG_EXP}}").toString();
    }
    return result;
  }
}
