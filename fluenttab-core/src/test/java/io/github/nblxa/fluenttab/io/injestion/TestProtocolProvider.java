package io.github.nblxa.fluenttab.io.injestion;

import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.FluentTab;
import io.github.nblxa.fluenttab.io.IoProtocolProvider;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.HashMap;
import java.util.Map;

public class TestProtocolProvider implements IoProtocolProvider {
  public static class Payload1 {
  }

  public static class Payload2 {
  }

  public static class Payload3 {
  }

  public static class TestProtocol1 implements InjestionProtocol<Payload1> {
    @NonNull
    @Override
    public Tab injest(@NonNull Payload1 object) {
      return FluentTab.tab().row(1);
    }
  }

  public static class TestProtocol2 implements InjestionProtocol<Payload2> {
    @NonNull
    @Override
    public Tab injest(@NonNull Payload2 object) {
      return FluentTab.tab().row(2);
    }
  }

  @NonNull
  @Override
  public Map<Class<?>, InjestionProtocol<?>> injestionProtocols() {
    Map<Class<?>, InjestionProtocol<?>> map = new HashMap<>();
    map.put(Payload1.class, new TestProtocol1());
    map.put(Payload2.class, new TestProtocol2());
    return map;
  }
}
