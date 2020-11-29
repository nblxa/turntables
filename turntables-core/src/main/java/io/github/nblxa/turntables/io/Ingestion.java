package io.github.nblxa.turntables.io;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.io.ingestion.DefaultProtocol;
import io.github.nblxa.turntables.io.ingestion.IngestionProtocol;
import io.github.nblxa.turntables.io.ingestion.ResultSetProtocol;
import io.github.nblxa.turntables.io.ingestion.TwoDimensionalArrayProtocol;

import java.sql.ResultSet;
import java.util.Map;

public class Ingestion extends ClassTreeHolder<IngestionProtocol<?>> {
  private static final Ingestion INSTANCE = new Ingestion();

  private Ingestion() {
  }

  public static Ingestion getInstance() {
    return INSTANCE;
  }

  @SuppressWarnings("unchecked")
  public <T> IngestionProtocol<T> protocolFor(Class<T> klass) {
    return (IngestionProtocol<T>) getProtocolFor(klass);
  }

  @NonNull
  @Override
  protected ClassTree<IngestionProtocol<?>> defaultProtocols() {
    ClassTree<IngestionProtocol<?>> tree = ClassTree.newInstance(DefaultProtocol.getInstance());
    tree = tree.add(ResultSet.class, new ResultSetProtocol<>());
    tree = tree.add(Object[][].class, new TwoDimensionalArrayProtocol());
    return tree;
  }

  @NonNull
  @Override
  protected Map<Class<?>, IngestionProtocol<?>> getProtocolMap(@NonNull IoProtocolProvider provider) {
    return provider.ingestionProtocols();
  }
}
