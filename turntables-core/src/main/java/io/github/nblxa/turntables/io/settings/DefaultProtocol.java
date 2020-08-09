package io.github.nblxa.turntables.io.settings;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Turntables;

public class DefaultProtocol implements SettingsProtocol<Object> {
  private static final DefaultProtocol INSTANCE = new DefaultProtocol();

  private DefaultProtocol() {
  }

  @NonNull
  public static DefaultProtocol getInstance() {
    return INSTANCE;
  }

  @NonNull
  @Override
  public Settings settings(@NonNull Object object) {
    return Turntables.getSettings();
  }
}
