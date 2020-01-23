package io.github.nblxa.turntables;

import io.github.nblxa.turntables.assertj.TabAssert;
import io.github.nblxa.turntables.io.Ingestion;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

public final class Turntables {
  /**
   * Maximum number of list permutations for matching actual and expected rows in any order,
   * if expected rows contain Matchers.
   */
  public static final long ROW_PERMUTATION_LIMIT = 10_000L;

  /**
   * Start creating a new <code>Tab</code>.
   *
   * @return
   */
  @NonNull
  public static TableUtils.ColAdderTable tab() {
    return new TableUtils.ColAdderTable();
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public static <T> Tab from(T object) {
    Objects.requireNonNull(object);
    return Ingestion.getInstance()
        .protocolFor((Class<? super T>) object.getClass())
        .ingest(object);
  }

  @NonNull
  public static <T extends Tab> TabAssert<T> assertThat(T actualTab) {
    return new TabAssert<>(actualTab);
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

  @NonNull
  public static DoublePredicate testDouble(@NonNull DoublePredicate shouldBeTrue) {
    return shouldBeTrue;
  }

  public enum RowMode {
    MATCHES_IN_GIVEN_ORDER, MATCHES_IN_ANY_ORDER, MATCHES_BY_KEY
  }

  public enum ColMode {
    MATCHES_IN_GIVEN_ORDER, MATCHES_BY_NAME
  }

  /**
   * This class is intended as a static API entry point, hence it is not instantiable.
   */
  private Turntables() {
    throw new UnsupportedOperationException();
  }
}
