package io.github.nblxa.turntables.io.settings;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Settings;

public interface SettingsProtocol<T> {
  @NonNull
  Settings settings(@NonNull T object);
}
