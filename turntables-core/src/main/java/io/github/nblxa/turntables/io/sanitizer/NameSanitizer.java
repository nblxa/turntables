package io.github.nblxa.turntables.io.sanitizer;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class NameSanitizer<T> {
  @NonNull
  @SuppressWarnings("unchecked")
  public final String sanitizeName(@NonNull Object dataAccessObject, @NonNull String name) {
    if (nameOk((T) dataAccessObject, name)) {
      return name;
    } else {
      throw new IllegalArgumentException("Unsupported name: " + name);
    }
  }

  protected abstract boolean nameOk(T dataAccessObject, String name);
}
