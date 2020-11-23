package io.github.nblxa.turntables;

public class SettingsThreadLocal extends DequeThreadLocal<Settings> {
  @Override
  public Settings dequeTopValue() {
    return Settings.builder().build();
  }
}
