package io.github.nblxa.turntables;

import io.github.nblxa.turntables.assertj.TabAssert;
import io.github.nblxa.turntables.io.Feed;
import io.github.nblxa.turntables.io.Ingestion;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.io.NameSanitizing;
import io.github.nblxa.turntables.junit.TestTable;
import java.util.Deque;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

/**
 * Turntables provides a way to write assertions on tabular data using an easy-to-use fluent API:
 *
 * <pre>{@code
 *   ResultSet rs = conn.executeQuery("select id, name from employees");
 *
 *   Tab actual = Turntables.from(rs);
 *
 *   Tab expected = Turntables.tab()
 *     .row(2, "Bob")
 *     .row(1, "Alice");
 *
 *   Turntables.assertThat(actual)
 *     .rowMode(Settings.RowMode.MATCH_IN_ANY_ORDER)
 *     .matchesExpected(expected);
 * }</pre>
 *
 * The library is based on <a href="https://joel-costigliola.github.io/assertj/" target="_top">
 * AssertJ</a> and can be integrated with JUnit. See {@link TestTable}
 * for integration with JUnit.<p>
 *
 * Turntables can be extended to support various data sources and databases via SPI.
 * See {@link Ingestion}, {@link Feed}, {@link NameSanitizing}.
 */
public final class Turntables {
  /**
   * Maximum number of list permutations for matching actual and expected rows in any order,
   * if expected rows contain Matchers.
   */
  public static final long ROW_PERMUTATION_LIMIT = 10_000L;
  private static final Object[] ARRAY_WITH_NULL = new Object[]{null};
  private static final SettingsThreadLocal SETTINGS_THREAD_LOCAL = new SettingsThreadLocal();

  /**
   * Start creating a new <code>Tab</code> using a fluent API.
   * Example:
   * <pre>{@code
   *   Tab expected = Turntables.tab()
   *     .col("first_name")
   *     .col("last_name")
   *     .row("William", "Shakespeare")
   *     .row("Leo", "Tolstoy");
   * }</pre>
   *
   * @return a new Tab
   */
  @NonNull
  public static TableUtils.ColAdderTable tab() {
    return new TableUtils.ColAdderTable();
  }

  /**
   * Create a new Tab from an external object, such as a, for instance, a <tt>ResultSet</tt>.
   *
   * Example:
   * <pre>{@code
   *   try (ResultSet rs = preparedStatement.executeQuery()) {
   *     Tab actual = Turntables.from(rs);
   *   }
   * }</pre>
   *
   * Various object types can be supported via the extension mechanism based on SPI.
   *
   * @param object input object
   * @param <T> input object type
   * @return a new Tab based on data from the input object
   * @see Ingestion
   */
  @SuppressWarnings("unchecked")
  @NonNull
  public static <T> Tab from(T object) {
    Objects.requireNonNull(object);
    return Ingestion.getInstance()
        .protocolFor((Class<? super T>) object.getClass())
        .ingest(object);
  }

  /**
   * Create assertion for a {@link Tab}.
   *
   * @param actualTab the actual data
   * @param <T> subtype of the actual
   * @return AssertJ assertion API object
   */
  @NonNull
  public static <T extends Tab> TabAssert<T> assertThat(T actualTab) {
    return new TabAssert<>(actualTab);
  }

  /**
   * Wrapper method for using {@link Predicate} directly in the {@link Tab} with expected data.
   *
   * Allows testing data not only for exact matches but with arbitrary predicates as well.
   * <p>Example:
   * <pre>{@code
   *   Tab expected = Turntables.tab()
   *     .row(Turntables.test(o -> true), 3)
   * }</pre>
   * In the above example, <tt>expected</tt> will match any {@link Tab} with one row and two columns
   * where the 2nd value equals 3.
   *
   * @param shouldBeTrue the predicate whose parameter will be taken from the actual data
   * @param <T> type of predicate parameter
   * @return the predicate itself
   */
  @NonNull
  public static <T> Predicate<T> test(@NonNull Predicate<T> shouldBeTrue) {
    return shouldBeTrue;
  }

  /**
   * Wrapper method for using {@link IntPredicate} directly in the {@link Tab} with expected data.
   *
   * @param shouldBeTrue the predicate whose parameter will be taken from the actual data
   * @return the predicate itself
   * @see Turntables#test
   */
  @NonNull
  public static IntPredicate testInt(@NonNull IntPredicate shouldBeTrue) {
    return shouldBeTrue;
  }

  /**
   * Wrapper method for using {@link LongPredicate} directly in the {@link Tab} with expected data.
   *
   * @param shouldBeTrue the predicate whose parameter will be taken from the actual data
   * @return the predicate itself
   * @see Turntables#test
   */
  @NonNull
  public static LongPredicate testLong(@NonNull LongPredicate shouldBeTrue) {
    return shouldBeTrue;
  }

  /**
   * Wrapper method for using {@link DoublePredicate} directly in the {@link Tab} with expected data.
   *
   * @param shouldBeTrue the predicate whose parameter will be taken from the actual data
   * @return the predicate itself
   * @see Turntables#test
   */
  @NonNull
  public static DoublePredicate testDouble(@NonNull DoublePredicate shouldBeTrue) {
    return shouldBeTrue;
  }

  @NonNull
  public static Object[] nul() {
    return ARRAY_WITH_NULL;
  }

  public static SettingsTransaction setSettings(@NonNull Settings settings) {
    SETTINGS_THREAD_LOCAL.get().push(settings);
    return Turntables::rollbackSettings;
  }

  public static void rollbackSettings() {
    Deque<Settings> d = SETTINGS_THREAD_LOCAL.get();
    if (!d.isEmpty()) {
      d.pop();
    }
    if (d.isEmpty()) {
      SETTINGS_THREAD_LOCAL.reset();
    }
  }

  @NonNull
  public static Settings getSettings() {
    return SETTINGS_THREAD_LOCAL.get().peek();
  }

  /**
   * This class is intended as a static API entry point, hence it is not instantiable.
   */
  private Turntables() {
    throw new UnsupportedOperationException();
  }
}
