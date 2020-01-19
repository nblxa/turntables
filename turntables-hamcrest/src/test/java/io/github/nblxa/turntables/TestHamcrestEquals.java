package io.github.nblxa.turntables;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class TestHamcrestEquals {
  @Test
  public void testMatcherVal_equalsContract() {
    EqualsVerifier.forClass(MatcherVal.class)
        .withRedefinedSuperclass()
        .withIgnoredFields("assertionPredicate", "toStringSupplier")
        .verify();
  }
}