package io.github.nblxa.turntables.io.sanitizer;

public class DefaultSanitizer extends NameSanitizer<Object> {
  @Override
  protected boolean nameOk(Object dataAccessObject, String name) {
    return true;
  }
}
