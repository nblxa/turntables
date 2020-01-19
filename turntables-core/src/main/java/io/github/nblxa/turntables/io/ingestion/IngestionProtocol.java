package io.github.nblxa.turntables.io.ingestion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;

public interface IngestionProtocol<T> {
  @NonNull
  Tab ingest(@NonNull T object);
}
