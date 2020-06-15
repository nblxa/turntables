package io.github.nblxa.turntables.oracle;

import java.util.Collections;
import java.util.Map;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.io.IoProtocolProvider;
import io.github.nblxa.turntables.io.feed.FeedProtocol;
import oracle.jdbc.OracleConnection;

public class OracleProtocolProvider implements IoProtocolProvider {
  @NonNull
  @Override
  public Map<Class<?>, FeedProtocol<?>> feedProtocols() {
    return Collections.singletonMap(OracleConnection.class, new OracleJdbcFeedProtocol<>());
  }
}
