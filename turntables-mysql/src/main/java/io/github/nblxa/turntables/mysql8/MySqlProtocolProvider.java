package io.github.nblxa.turntables.mysql8;

import java.util.Collections;
import java.util.Map;
import com.mysql.cj.jdbc.JdbcConnection;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.io.IoProtocolProvider;
import io.github.nblxa.turntables.io.feed.FeedProtocol;

public class MySqlProtocolProvider implements IoProtocolProvider {
  @NonNull
  @Override
  public Map<Class<?>, FeedProtocol<?>> feedProtocols() {
    return Collections.singletonMap(JdbcConnection.class, new MySqlJdbcFeedProtocol<>());
  }
}
