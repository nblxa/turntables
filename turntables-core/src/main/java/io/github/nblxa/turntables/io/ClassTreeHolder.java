package io.github.nblxa.turntables.io;

import edu.umd.cs.findbugs.annotations.NonNull;
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

public abstract class ClassTreeHolder<T> {
  private ClassTree<T> protocolTree;
  private final Map<Class<?>, T> protocolCache = new ConcurrentHashMap<>();
  private final ReentrantLock treeLock = new ReentrantLock(true);

  protected T getProtocolFor(Class<?> klass) {
    return protocolCache.computeIfAbsent(klass, kl -> {
      Set<T> protocolSet = getProtocolTree().findValueForClass(kl);
      Iterator<T> iter = protocolSet.iterator();
      if (iter.hasNext()) {
        T protocol = iter.next();
        if (iter.hasNext()) {
          String protocolClasses = protocolSet.stream()
              .map(p -> p.getClass().getCanonicalName())
              .collect(Collectors.joining(", "));
          throw new IllegalStateException("Too many protocols found for class: "
              + kl.getCanonicalName() + ". Expected exactly one, found: " + protocolClasses);
        }
        return protocol;
      } else {
        throw new NoSuchElementException("No protocol found for class: "
            + kl.getCanonicalName());
      }
    });
  }

  /**
   * Initializes the protocol tree lazily.
   */
  @NonNull
  private ClassTree<T> getProtocolTree() {
    if (treeLock.isHeldByCurrentThread()) {
      throw new UnsupportedOperationException();
    }
    if (protocolTree == null) {
      treeLock.lock();
      try {
        protocolTree = defaultProtocols();
        protocolTree = externalProtocols(protocolTree);
      } finally {
        treeLock.unlock();
      }
    }
    return protocolTree;
  }

  @NonNull
  public final ClassTree<T> externalProtocols(@NonNull ClassTree<T> tree) {
    ClassTree<T> updatedTree = tree;
    for (IoProtocolProvider provider : ServiceLoader.load(IoProtocolProvider.class)) {
      Map<Class<?>, T> protocolMap = getProtocolMap(provider);
      protocolMap = new HashMap<>(Objects.requireNonNull(protocolMap, "protocol map is null"));
      for (Map.Entry<Class<?>, T> entry : protocolMap.entrySet()) {
        Class<?> key = Objects.requireNonNull(entry.getKey(), "key is null");
        T value = Objects.requireNonNull(entry.getValue(), "protocol is null");
        updatedTree = updatedTree.add(key, value);
      }
    }
    return updatedTree;
  }

  @NonNull
  protected abstract ClassTree<T> defaultProtocols();

  @NonNull
  protected abstract Map<Class<?>, T> getProtocolMap(@NonNull IoProtocolProvider provider);
}
