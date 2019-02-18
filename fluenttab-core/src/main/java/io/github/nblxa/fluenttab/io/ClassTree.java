package io.github.nblxa.fluenttab.io;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

@Immutable
@ThreadSafe
final class ClassTree<V> {
  @Nullable
  private final V value;
  @NonNull
  private final Map<Class<?>, ClassTree<V>> branches;

  private ClassTree() {
    this.value = null;
    this.branches = Collections.emptyMap();
  }

  private ClassTree(@NonNull V value) {
    this(Objects.requireNonNull(value), Collections.emptyMap());
  }

  private ClassTree(@Nullable V value, @NonNull Map<Class<?>, ClassTree<V>> branches) {
    this.value = value;
    this.branches = Collections.unmodifiableMap(new HashMap<>(Objects.requireNonNull(branches)));
  }

  @NonNull
  static <V> ClassTree<V> newInstance(@NonNull V value) {
    return new ClassTree<>(value);
  }

  @NonNull
  static <V> ClassTree<V> newInstanceWithoutValue() {
    return new ClassTree<>();
  }

  @NonNull
  static Set<List<Class<?>>> getClassLineages(Class<?> klass) {
    Objects.requireNonNull(klass);
    if (klass.isPrimitive()) {
      throw new UnsupportedOperationException();
    }
    if (klass == Object.class) {
      return Collections.singleton(Collections.singletonList(Object.class));
    } else {
      final Set<List<Class<?>>> set = new HashSet<>();
      for (Class<?> superClass : immediateSuperClasses(klass)) {
        final Set<List<Class<?>>> superSet = getClassLineages(superClass);
        for (List<Class<?>> superClassList : superSet) {
          final List<Class<?>> classList = new ArrayList<>(superClassList.size() + 1);
          classList.addAll(superClassList);
          classList.add(klass);
          set.add(Collections.unmodifiableList(classList));
        }
      }
      return Collections.unmodifiableSet(set);
    }
  }

  @NonNull
  private static <U> ClassTree<U> addSubtree(@NonNull final ClassTree<U> tree,
                                             @NonNull final List<Class<?>> classList,
                                             @NonNull final U value) {
    if (classList.isEmpty()) {
      return tree;
    }
    final Class<?> klass = classList.get(0);
    final ClassTree<U> branch = tree.getBranches()
        .getOrDefault(klass, newInstanceWithoutValue());
    final ClassTree<U> updatedBranch;
    if (classList.size() == 1) {
      updatedBranch = new ClassTree<>(value, branch.getBranches());
    } else {
      updatedBranch = addSubtree(branch, classList.subList(1, classList.size()), value);
    }
    return tree.mapBranch(klass, b -> updatedBranch.builder());
  }

  /**
   * Get a set of immediate superclasses for the given {@code Class}.
   *
   * <p>Every element of the resulting set is a class X such that a variable declared with type X
   * can be assigned a value of the given class {@code klass}.
   *
   * <p>For each kind of class @{code C} the set includes:
   * <ul>
   *   <li>C is an array of class P extending Object: all array types of immediate superclasses
   *       of P</li>
   *   <li>C is an array of Object: interfaces implemented by Object[], or Object if none</li>
   *   <li>C is an primitive array: interfaces implemented by the primitive array, or Object
   *       if none</li>
   *   <li>C extends another class P: the class P plus all interfaces implemented by C</li>
   *   <li>C extends Object directly and implements interfaces: only the interfaces</li>
   *   <li>C extends Object and implements no interfaces: Object.class</li>
   *   <li>C is Object: empty set</li>
   *   <li>C is a primitive: {@code UnsupportedOperationException} is thrown</li>
   * </ul>
   *
   * @param klass the current class
   * @return the set of immediate superclasses in terms of the assignment operation
   */
  @NonNull
  private static Set<Class<?>> immediateSuperClasses(@NonNull Class<?> klass) {
    if (klass == Object.class) {
      return Collections.emptySet();
    }
    Objects.requireNonNull(klass);
    if (klass.isPrimitive()) {
      throw new UnsupportedOperationException("Primitive types are not supported");
    }
    Set<Class<?>> allSuper = new HashSet<>();
    if (klass.isArray()) {
      Class<?> componentType = klass.getComponentType();
      if (componentType.isPrimitive() || componentType == Object.class) {
        allSuper.addAll(interfaceSet(klass));
      } else {
        Set<Class<?>> componentSuperClasses = immediateSuperClasses(componentType);
        for (Class<?> componentSuperClass : componentSuperClasses) {
          allSuper.add(Array.newInstance(componentSuperClass, 0).getClass());
        }
      }
    } else {
      allSuper.addAll(interfaceSet(klass));
      Class<?> superClass = klass.getSuperclass();
      if (superClass == null) {
        superClass = Object.class;
      }
      if (superClass != Object.class || allSuper.isEmpty()) {
        allSuper.add(superClass);
      }
    }
    return Collections.unmodifiableSet(allSuper);
  }

  @NonNull
  private static Set<Class<?>> interfaceSet(Class<?> klass) {
    Set<Class<?>> interfaceSet = new HashSet<>();
    Class<?>[] interfaces = klass.getInterfaces();
    if (interfaces != null) {
      for (Class<?> interf : interfaces) {
        if (interf != null) {
          interfaceSet.add(interf);
        }
      }
    }
    return interfaceSet;
  }

  @NonNull
  Optional<V> getValue() {
    return Optional.ofNullable(value);
  }

  @NonNull
  Map<Class<?>, ClassTree<V>> getBranches() {
    return branches;
  }

  @NonNull
  ClassTree<V> mapBranch(@NonNull Class<?> klass,
                         @NonNull Function<ClassTree<V>, Builder<V>> branchMapper) {
    Map<Class<?>, ClassTree<V>> mappedBranches = new HashMap<>(branches);
    mappedBranches.compute(klass, (k, v) -> {
      Builder<V> builder = branchMapper.apply(v);
      Objects.requireNonNull(builder);
      ClassTree<V> mappedBranch = builder.build();
      Objects.requireNonNull(mappedBranch);
      return mappedBranch;
    });
    return new ClassTree<>(value, mappedBranches);
  }

  @NonNull
  ClassTree<V> add(@NonNull Class<?> klass, @NonNull V value) {
    Set<List<Class<?>>> classLineages = getClassLineages(klass);
    ClassTree<V> updatedTree = this;
    for (List<Class<?>> lineage : classLineages) {
      updatedTree = addSubtree(updatedTree, lineage, value);
    }
    return updatedTree;
  }

  @NonNull
  Set<V> findValueForClass(@NonNull Class<?> klass) {
    Objects.requireNonNull(klass);
    Set<V> values = new HashSet<>();
    for (Class<?> branchClass : branches.keySet()) {
      if (branchClass.isAssignableFrom(klass)) {
        ClassTree<V> branch = branches.get(branchClass);
        values.addAll(branch.findValueForClass(klass));
      }
    }
    if (values.isEmpty()) {
      if (value != null) {
        return Collections.singleton(value);
      } else {
        return Collections.emptySet();
      }
    }
    return Collections.unmodifiableSet(values);
  }

  @NonNull
  Builder<V> builder() {
    return new Builder<>(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClassTree<?> that = (ClassTree<?>) o;
    return Objects.equals(value, that.value)
        && branches.equals(that.branches);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, branches);
  }

  @Override
  public String toString() {
    return "ClassTree{"
        + "value=" + value
        + ", branches=" + branches
        + '}';
  }

  static class Builder<V> {
    @Nullable
    private V value;
    @NonNull
    private Map<Class<?>, ClassTree<V>> branches;

    private Builder(@NonNull ClassTree<V> tree) {
      this.value = tree.value;
      this.branches = Objects.requireNonNull(tree.branches);
    }

    @NonNull
    private ClassTree<V> build() {
      return new ClassTree<>(value, branches);
    }
  }
}
