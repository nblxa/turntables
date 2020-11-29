package io.github.nblxa.turntables.ojdbc8;

import java.util.Collections;
import java.util.Map;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.io.IoProtocolProvider;
import io.github.nblxa.turntables.io.feed.FeedProtocol;
import io.github.nblxa.turntables.io.ingestion.IngestionProtocol;
import io.github.nblxa.turntables.io.settings.SettingsProtocol;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleResultSet;

public class OracleProtocolProvider implements IoProtocolProvider {
  @NonNull
  @Override
  public Map<Class<?>, FeedProtocol<?>> feedProtocols() {
    return Collections.singletonMap(OracleConnection.class, new OracleJdbcFeedProtocol<>());
  }

  @NonNull
  @Override
  public Map<Class<?>, SettingsProtocol<?>> settingsProtocols() {
    return Collections.singletonMap(OracleConnection.class, new OracleJdbcSettingsProtocol<>());
  }

  @NonNull
  @Override
  public Map<Class<?>, IngestionProtocol<?>> ingestionProtocols() {
    return Collections.singletonMap(OracleResultSet.class, new OracleResultSetProtocol<>());
  }
}
