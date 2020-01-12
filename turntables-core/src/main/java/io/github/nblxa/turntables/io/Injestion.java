package io.github.nblxa.turntables.io;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.io.injestion.DefaultProtocol;
import io.github.nblxa.turntables.io.injestion.InjestionProtocol;
import io.github.nblxa.turntables.io.injestion.ResultSetProtocol;
import io.github.nblxa.turntables.io.injestion.TwoDimensionalArrayProtocol;

import java.sql.ResultSet;
import java.util.Map;

public class Injestion extends ClassTreeHolder<InjestionProtocol<?>> {
  private static final Injestion INSTANCE = new Injestion();

  private Injestion() {
  }

  public static Injestion getInstance() {
    return INSTANCE;
  }

  @SuppressWarnings("unchecked")
  public <T> InjestionProtocol<? super T> protocolFor(Class<T> klass) {
    return (InjestionProtocol<? super T>) getProtocolFor(klass);
  }

  @NonNull
  @Override
  protected ClassTree<InjestionProtocol<?>> defaultProtocols() {
    ClassTree<InjestionProtocol<?>> tree = ClassTree.newInstance(DefaultProtocol.getInstance());
    tree = tree.add(ResultSet.class, new ResultSetProtocol());
    tree = tree.add(Object[][].class, new TwoDimensionalArrayProtocol());
    return tree;
  }

  @NonNull
  @Override
  protected Map<Class<?>, InjestionProtocol<?>> getProtocolMap(@NonNull IoProtocolProvider provider) {
    return provider.injestionProtocols();
  }
}
