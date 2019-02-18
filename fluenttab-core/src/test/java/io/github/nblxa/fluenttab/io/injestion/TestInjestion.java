package io.github.nblxa.fluenttab.io.injestion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.FluentTab;
import io.github.nblxa.fluenttab.io.Injestion;
import org.junit.Test;

public class TestInjestion {
  @Test
  public void test1() {
    InjestionProtocol<? super TestProtocolProvider.Payload1> protocol = Injestion.getInstance()
        .protocolFor(TestProtocolProvider.Payload1.class);
    assertThat(protocol).isInstanceOf(TestProtocolProvider.TestProtocol1.class);
    Tab tab = protocol.injest(new TestProtocolProvider.Payload1());
    assertThat(tab).isEqualTo(FluentTab.tab().row(1));
  }

  @Test
  public void test2() {
    InjestionProtocol<? super TestProtocolProvider.Payload2> protocol = Injestion.getInstance()
        .protocolFor(TestProtocolProvider.Payload2.class);
    assertThat(protocol).isInstanceOf(TestProtocolProvider.TestProtocol2.class);
    Tab tab = protocol.injest(new TestProtocolProvider.Payload2());
    assertThat(tab).isEqualTo(FluentTab.tab().row(2));
  }

  @Test
  public void test3() {
    InjestionProtocol<? super TestProtocolProvider.Payload3> protocol = Injestion.getInstance()
        .protocolFor(TestProtocolProvider.Payload3.class);
    assertThat(protocol).isNotNull();
    Throwable t = catchThrowable(() -> protocol.injest(new TestProtocolProvider.Payload3()));
    assertThat(t).isInstanceOf(UnsupportedOperationException.class);
  }
}
