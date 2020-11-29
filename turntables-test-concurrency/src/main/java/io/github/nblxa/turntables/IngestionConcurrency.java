package io.github.nblxa.turntables;

import io.github.nblxa.turntables.io.Ingestion;
import io.github.nblxa.turntables.io.IoProtocolProvider;
import io.github.nblxa.turntables.io.ingestion.IngestionProtocol;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

@JCStressTest
@Outcome(id = "1", expect = Expect.ACCEPTABLE, desc = "Default outcome.")
@State
public class IngestionConcurrency {
  private void work(I_Result res) {
    Ingestion.getInstance().protocolFor(Object.class);
    res.r1 = TestIoProtocolProvider.getCounter();
  }

  @Actor
  public void actor1(I_Result res) {
    work(res);
  }

  @Actor
  public void actor2(I_Result res) {
    work(res);
  }

  public static class TestIoProtocolProvider implements IoProtocolProvider {
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @Override
    @NonNull
    public Map<Class<?>, IngestionProtocol<?>> ingestionProtocols() {
      COUNTER.incrementAndGet();
      return Collections.emptyMap();
    }

    private static int getCounter() {
      return COUNTER.get();
    }
  }
}
