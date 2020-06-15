package io.github.nblxa.turntables;

/**
 * Automatically rolls back the changes to {@link Settings}.
 */
interface SettingsTransaction extends AutoCloseable {
  @Override
  void close();
}
