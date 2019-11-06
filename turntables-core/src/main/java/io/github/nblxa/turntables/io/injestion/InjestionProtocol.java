package io.github.nblxa.turntables.io.injestion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;

public interface InjestionProtocol<T> {
  @NonNull
  Tab injest(@NonNull T object);
}
