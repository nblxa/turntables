package io.github.nblxa.turntables.io.ingestion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.io.Ingestion;
import org.junit.Test;

public class TestIngestion {
  @Test
  public void test1() {
    IngestionProtocol<? super TestProtocolProvider.Payload1> protocol = Ingestion.getInstance()
        .protocolFor(TestProtocolProvider.Payload1.class);
    assertThat(protocol).isInstanceOf(TestProtocolProvider.TestProtocol1.class);
    Tab tab = protocol.ingest(new TestProtocolProvider.Payload1());
    assertThat(tab).isEqualTo(Turntables.tab().row(1));
  }

  @Test
  public void test2() {
    IngestionProtocol<? super TestProtocolProvider.Payload2> protocol = Ingestion.getInstance()
        .protocolFor(TestProtocolProvider.Payload2.class);
    assertThat(protocol).isInstanceOf(TestProtocolProvider.TestProtocol2.class);
    Tab tab = protocol.ingest(new TestProtocolProvider.Payload2());
    assertThat(tab).isEqualTo(Turntables.tab().row(2));
  }

  @Test
  public void test3() {
    IngestionProtocol<? super TestProtocolProvider.Payload3> protocol = Ingestion.getInstance()
        .protocolFor(TestProtocolProvider.Payload3.class);
    assertThat(protocol).isNotNull();
    Throwable t = catchThrowable(() -> protocol.ingest(new TestProtocolProvider.Payload3()));
    assertThat(t).isInstanceOf(UnsupportedOperationException.class);
  }
}
