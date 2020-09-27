package io.github.nblxa.turntables;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.nblxa.turntables.io.rowstore.CleanUpAction;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TestTable {

  /**
   * Alias for {@link #name}.
   */
  String value() default "";

  /**
   * Table name.
   */
  String name() default "";

  /**
   * Action to perform to clean up the data between tests.
   */
  CleanUpAction cleanUpAction() default CleanUpAction.DROP;
}
