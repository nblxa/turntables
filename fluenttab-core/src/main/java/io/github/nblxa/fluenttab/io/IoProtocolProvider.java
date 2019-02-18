package io.github.nblxa.fluenttab.io;

import io.github.nblxa.fluenttab.io.injestion.InjestionProtocol;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Map;

public interface IoProtocolProvider {
  @NonNull
  Map<Class<?>, InjestionProtocol<?>> injestionProtocols();
}
