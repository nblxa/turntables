package io.github.nblxa.turntables.io;

import io.github.nblxa.turntables.io.feed.FeedProtocol;
import io.github.nblxa.turntables.io.ingestion.IngestionProtocol;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.io.sanitizer.NameSanitizer;
import java.util.Collections;
import java.util.Map;

public interface IoProtocolProvider {
  @NonNull
  default Map<Class<?>, IngestionProtocol<?>> ingestionProtocols() {
    return Collections.emptyMap();
  }

  @NonNull
  default Map<Class<?>, FeedProtocol<?>> feedProtocols() {
    return Collections.emptyMap();
  }

  @NonNull
  default Map<Class<?>, NameSanitizer<?>> nameSanitizers() {
    return Collections.emptyMap();
  }
}
