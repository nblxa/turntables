package io.github.nblxa.fluenttab.io.injestion;

import io.github.nblxa.fluenttab.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;

public interface InjestionProtocol<T> {
  @NonNull
  Tab injest(@NonNull T object);
}
