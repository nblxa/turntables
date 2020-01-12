package io.github.nblxa.turntables.io;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.io.sanitizer.JdbcNameSanitizer;
import io.github.nblxa.turntables.io.sanitizer.NameSanitizer;
import java.util.Map;

public class NameSanitizing extends ClassTreeHolder<NameSanitizer<?>> {
  private static final NameSanitizing INSTANCE = new NameSanitizing();

  public static NameSanitizing getInstance() {
    return INSTANCE;
  }

  private NameSanitizing() {
  }

  @NonNull
  public static String sanitizeName(@NonNull Object dataAccessObject, @NonNull String name) {
    return getInstance()
        .sanitizerFor(dataAccessObject.getClass())
        .sanitizeName(dataAccessObject, name);
  }

  @NonNull
  @Override
  protected ClassTree<NameSanitizer<?>> defaultProtocols() {
    return ClassTree.newInstance(JdbcNameSanitizer.getInstance());
  }

  @NonNull
  @Override
  protected Map<Class<?>, NameSanitizer<?>> getProtocolMap(@NonNull IoProtocolProvider provider) {
    return provider.nameSanitizers();
  }

  @SuppressWarnings("unchecked")
  @NonNull
  private <T> NameSanitizer<? super T> sanitizerFor(Class<T> klass) {
    return (NameSanitizer<? super T>) getProtocolFor(klass);
  }
}
