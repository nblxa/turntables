package io.github.nblxa.turntables.io;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TestIoProtocolProvider {

  static class TestProvider implements IoProtocolProvider {
  }

  static IoProtocolProvider prov = new TestProvider();

  @Test
  public void testIngestionProtocols() {
    assertThat(prov.ingestionProtocols()).isEmpty();
  }

  @Test
  public void testFeedProtocols() {
    assertThat(prov.feedProtocols()).isEmpty();
  }

  @Test
  public void testSettingsProtocols() {
    assertThat(prov.settingsProtocols()).isEmpty();
  }
}
