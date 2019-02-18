package io.github.nblxa.fluenttab;

import io.github.nblxa.fluenttab.io.Injestion;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

public final class FluentTab {
  public static final long ROW_PERMUTATION_LIMIT = 10_000L;

  // Table builder

  private FluentTab() {
  }

  // Predicates to generate assertions

  @NonNull
  public static TableUtils.ColAdder tab() {
    return new TableUtils.ColAdder();
  }

  @NonNull
  public static <T> Predicate<T> test(@NonNull Predicate<T> shouldBeTrue) {
    return shouldBeTrue;
  }

  @NonNull
  public static IntPredicate testInt(@NonNull IntPredicate shouldBeTrue) {
    return shouldBeTrue;
  }

  @NonNull
  public static LongPredicate testLong(@NonNull LongPredicate shouldBeTrue) {
    return shouldBeTrue;
  }

  // Data ingestion

  @NonNull
  public static DoublePredicate testDouble(@NonNull DoublePredicate shouldBeTrue) {
    return shouldBeTrue;
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public static <T> Tab from(T object) {
    Objects.requireNonNull(object);
    return Injestion.getInstance()
        .protocolFor((Class<? super T>) object.getClass())
        .injest(object);
  }

  public enum RowMode {
    MATCHES_IN_GIVEN_ORDER, MATCHES_IN_ANY_ORDER, MATCHES_BY_KEY//, CONTAINS_IN_ANY_ORDER
  }

  public enum ColMode {
    MATCHES_IN_GIVEN_ORDER, MATCHES_BY_NAME//, CONTAINS_BY_NAME
  }
}
