package io.github.nblxa.turntables.io.injestion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;

public class DefaultProtocol implements InjestionProtocol<Object> {
  private static final DefaultProtocol INSTANCE = new DefaultProtocol();

  private DefaultProtocol() {
  }

  @NonNull
  public static DefaultProtocol getInstance() {
    return INSTANCE;
  }

  @NonNull
  @Override
  public Tab injest(@NonNull Object object) {
    throw new UnsupportedOperationException("An injestion protocol could not be found for class: "
        + object.getClass().getCanonicalName());
  }
}
