package io.github.nblxa.turntables.junit;

import io.github.nblxa.turntables.Tab;
import org.junit.rules.TestRule;

public interface TestTable extends TestRule {
  Tab ingest();
}
