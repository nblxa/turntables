package io.github.nblxa.turntables.test.oracle;

import java.util.Arrays;
import org.apache.commons.lang3.RandomStringUtils;

public class RandomPassword {
  private static final String ALL_CHARS;
  private static final String SMALL;
  private static final String CAPITAL;
  private static final String DIGIT;
  private static final String SPECIAL;
  static {
    final char[] allChars = new char[75];
    final char[] small = new char[26];
    final char[] capital = new char[26];
    final char[] digit = new char[10];
    final char[] special = new char[13];
    int i = 0;
    int j = 0;
    for (char c = 'a'; c <= 'z'; c++) {
      allChars[i++] = c;
      small[j++] = c;
    }
    j = 0;
    for (char c = 'A'; c <= 'Z'; c++) {
      allChars[i++] = c;
      capital[j++] = c;
    }
    j = 0;
    for (char c = '0'; c <= '9'; c++) {
      allChars[i++] = c;
      digit[j++] = c;
    }
    j = 0;
    for (char c : Arrays.asList('-', '_', '+', '=', '(', ')', '%', '#', '^', '[', ']', '{', '}')) {
      allChars[i++] = c;
      special[j++] = c;
    }
    ALL_CHARS = new String(allChars);
    SMALL = new String(small);
    CAPITAL = new String(capital);
    DIGIT = new String(digit);
    SPECIAL = new String(special);
  }

  static String get() {
    return RandomStringUtils.random(2, SMALL)
        + RandomStringUtils.random(2, CAPITAL)
        + RandomStringUtils.random(2, DIGIT)
        + RandomStringUtils.random(2, SPECIAL)
        + RandomStringUtils.random(12, ALL_CHARS);
  }
}
