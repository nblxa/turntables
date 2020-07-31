package io.github.nblxa.turntables;

import java.util.ArrayDeque;
import java.util.Deque;

public class SettingsThreadLocal extends InheritableThreadLocal<Deque<Settings>> {
  private static final Settings DEFAULT_SETTINGS = Settings.builder().build();

  @Override
  protected Deque<Settings> initialValue() {
    Deque<Settings> s = new ArrayDeque<>();
    s.push(DEFAULT_SETTINGS);
    return s;
  }

  public void reset() {
    set(initialValue());
  }
}
