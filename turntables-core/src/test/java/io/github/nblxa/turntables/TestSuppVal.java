package io.github.nblxa.turntables;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import org.junit.Test;

public class TestSuppVal {
  @Test
  public void test() throws Exception {
    CompletableFuture<Integer> cf = new CompletableFuture<>();
    Supplier<Integer> is = () -> {
      try {
        return cf.get();
      } catch (InterruptedException e) {
        return -1;
      } catch (ExecutionException e) {
        return -2;
      }
    };
    Tab.Val val = new TableUtils.SuppVal(Typ.INTEGER, is);
    CompletableFuture<Object> valCf1 = CompletableFuture.supplyAsync(val::evaluate);
    assertThat(valCf1)
        .isNotDone();
    CompletableFuture<Object> valCf2 = CompletableFuture.supplyAsync(val::evaluate);
    assertThat(valCf2)
        .isNotDone();

    cf.complete(42);

    await().atMost(Duration.ofSeconds(1L)).until(valCf1::isDone);

    assertThat(valCf1)
        .isCompletedWithValue(42);
    assertThat(valCf2)
        .isCompletedWithValue(42);
  }
}
