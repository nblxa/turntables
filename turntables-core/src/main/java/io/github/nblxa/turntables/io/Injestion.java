package io.github.nblxa.turntables.io;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.io.injestion.DefaultProtocol;
import io.github.nblxa.turntables.io.injestion.InjestionProtocol;
import io.github.nblxa.turntables.io.injestion.ResultSetProtocol;
import io.github.nblxa.turntables.io.injestion.TwoDimensionalArrayProtocol;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Injestion {
  private static final Injestion INSTANCE = new Injestion();

  private volatile ClassTree<InjestionProtocol<?>> protocolTree;
  private final Map<Class<?>, InjestionProtocol<?>> protocolCache = new ConcurrentHashMap<>();
  private final ReentrantLock treeLock = new ReentrantLock(true);

  private Injestion() {
  }

  public static Injestion getInstance() {
    return INSTANCE;
  }

  @SuppressWarnings("unchecked")
  public <T> InjestionProtocol<? super T> protocolFor(Class<T> klass) {
    return (InjestionProtocol<? super T>) protocolCache.computeIfAbsent(klass, kl -> {
      Set<InjestionProtocol<?>> protocolSet = getProtocolTree().findValueForClass(kl);
      Iterator<InjestionProtocol<?>> iter = protocolSet.iterator();
      if (iter.hasNext()) {
        InjestionProtocol<?> protocol = iter.next();
        if (iter.hasNext()) {
          String protocolClasses = protocolSet.stream()
              .map(p -> p.getClass().getCanonicalName())
              .collect(Collectors.joining(", "));
          throw new IllegalStateException("Too many injestion protocols found for class: "
              + kl.getCanonicalName() + ". Expected exactly one, found: " + protocolClasses);
        }
        return protocol;
      } else {
        throw new NoSuchElementException("No injestion protocol found for class: "
            + kl.getCanonicalName());
      }
    });
  }

  /**
   * Initializes the protocol tree lazily.
   */
  @NonNull
  private ClassTree<InjestionProtocol<?>> getProtocolTree() {
    if (treeLock.isHeldByCurrentThread()) {
      throw new UnsupportedOperationException();
    }
    ClassTree<InjestionProtocol<?>> tree = protocolTree;
    if (tree == null) {
      treeLock.lock();
      try {
        tree = protocolTree;
        if (tree == null) {
          tree = defaultProtocols();
          tree = externalProtocols(tree);
          protocolTree = tree;
        }
      } finally {
        treeLock.unlock();
      }
    }
    return tree;
  }

  @NonNull
  private static ClassTree<InjestionProtocol<?>> defaultProtocols() {
    ClassTree<InjestionProtocol<?>> tree = ClassTree.newInstance(DefaultProtocol.getInstance());
    tree = tree.add(ResultSet.class, new ResultSetProtocol());
    tree = tree.add(Object[][].class, new TwoDimensionalArrayProtocol());
    return tree;
  }

  @NonNull
  private static ClassTree<InjestionProtocol<?>> externalProtocols(
      @NonNull ClassTree<InjestionProtocol<?>> tree) {
    ClassTree<InjestionProtocol<?>> updatedTree = tree;
    for (IoProtocolProvider provider : ServiceLoader.load(IoProtocolProvider.class)) {
      Map<Class<?>, InjestionProtocol<?>> protocolMap = provider.injestionProtocols();
      protocolMap = new HashMap<>(Objects.requireNonNull(protocolMap));
      for (Map.Entry<Class<?>, InjestionProtocol<?>> entry : protocolMap.entrySet()) {
        updatedTree = updatedTree.add(entry.getKey(), entry.getValue());
      }
    }
    return updatedTree;
  }
}
