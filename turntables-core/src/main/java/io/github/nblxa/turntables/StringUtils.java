package io.github.nblxa.turntables;

import java.util.function.Predicate;
import java.util.regex.Pattern;

final class StringUtils {
  private static final Predicate<String> MATCHES_WORD = Pattern.compile("^\\w+$")
      .asPredicate();
  private static final String DOUBLE_QUOTE = "\"";

  static String escape(String text) {
    if (MATCHES_WORD.test(text)) {
      return text;
    }
    return DOUBLE_QUOTE + text
        .replace("\\", "\\\\")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
        .replace("\"", "\\\"")
        + DOUBLE_QUOTE;
  }

  private StringUtils() {
    throw new UnsupportedOperationException();
  }
}
