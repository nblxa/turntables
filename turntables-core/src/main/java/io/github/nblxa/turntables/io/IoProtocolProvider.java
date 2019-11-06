package io.github.nblxa.turntables.io;

import io.github.nblxa.turntables.io.injestion.InjestionProtocol;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Map;

public interface IoProtocolProvider {
  @NonNull
  Map<Class<?>, InjestionProtocol<?>> injestionProtocols();
}
