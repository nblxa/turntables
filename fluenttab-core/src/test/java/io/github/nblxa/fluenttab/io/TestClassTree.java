package io.github.nblxa.fluenttab.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Before;
import org.junit.Test;

public class TestClassTree {
  private ClassTree<String> tree;

  @Before
  public void setUp() {
    tree = ClassTree.newInstance("v");
  }

  @Test
  public void equalsHashCode_contract() {
    Map<Class<?>, ClassTree<?>> red = new HashMap<>();
    red.put(Red.class, ClassTree.newInstance("red"));
    Map<Class<?>, ClassTree<?>> black = new HashMap<>();
    black.put(Black.class, ClassTree.newInstance("black"));
    EqualsVerifier.forClass(ClassTree.class)
        .withPrefabValues(Map.class, red, black)
        .verify();
  }

  @Test
  public void withValue_getValue_yieldsValue() {
    assertThat(tree.getValue()).hasValue("v");
  }

  @Test
  public void withoutValue_getValue_yieldsEmpty() {
    tree = ClassTree.newInstanceWithoutValue();
    assertThat(tree.getValue()).isEmpty();
  }

  @Test
  public void toString_emptyBranches() {
    assertThat(tree.toString()).isEqualTo("ClassTree{value=v, branches={}}");
  }

  @Test
  public void getClassLineages_ofObject() {
    Set<List<Class<?>>> classLineages = ClassTree.getClassLineages(Object.class);
    assertThat(classLineages)
        .isNotEmpty()
        .hasSize(1)
        .containsExactly(Collections.singletonList(Object.class));
  }

  @Test
  public void getClassLineages_ofClassExtendsObject() {
    Set<List<Class<?>>> hierarchy = ClassTree.getClassLineages(Red.class);
    assertThat(hierarchy)
        .isNotEmpty()
        .hasSize(1)
        .containsExactly(Arrays.asList(Object.class, Red.class));
  }

  @Test
  public void getClassLineages_ofClassExtendsAnother() {
    Set<List<Class<?>>> hierarchy = ClassTree.getClassLineages(Crimson.class);
    assertThat(hierarchy)
        .isNotEmpty()
        .hasSize(1)
        .containsExactly(Arrays.asList(Object.class, Red.class, Crimson.class));
  }

  @Test
  public void getClassLineages_ofClassExtendsAontherImplementsInterface() {
    Set<List<Class<?>>> hierarchy = ClassTree.getClassLineages(FordColor.class);
    assertThat(hierarchy)
        .isNotEmpty()
        .hasSize(2)
        .containsExactlyInAnyOrder(
            Arrays.asList(Object.class, Black.class, FordColor.class),
            Arrays.asList(Object.class, Serializable.class, FordColor.class));
  }

  @Test
  public void getClassLineages_ofClassImplementsInterface() {
    Set<List<Class<?>>> hierarchy = ClassTree.getClassLineages(PluckedChicken.class);
    assertThat(hierarchy)
        .isNotEmpty()
        .hasSize(1)
        .containsExactlyInAnyOrder(
            Arrays.asList(Object.class, Featherless.class, PluckedChicken.class));
  }

  @Test
  public void getClassLineages_multilevelImplementation() {
    Set<List<Class<?>>> hierarchy = ClassTree.getClassLineages(Human.class);
    assertThat(hierarchy)
        .isNotEmpty()
        .hasSize(3)
        .containsExactlyInAnyOrder(
            Arrays.asList(Object.class, Featherless.class, Primate.class, Human.class),
            Arrays.asList(Object.class, Featherless.class, FeatherlessBiped.class, Human.class),
            Arrays.asList(Object.class, Biped.class, FeatherlessBiped.class, Human.class));
  }

  @Test
  public void getClassLineages_ofInterface() {
    Set<List<Class<?>>> classLineages = ClassTree.getClassLineages(Featherless.class);
    assertThat(classLineages)
        .isNotEmpty()
        .hasSize(1)
        .containsExactly(Arrays.asList(Object.class, Featherless.class));
  }

  @Test
  public void getClassLineages_ofPrimitive() {
    Throwable t = catchThrowable(() -> ClassTree.getClassLineages(int.class));
    assertThat(t)
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  public void getClassLineages_ofTwoDimensionalObjectArray() {
    Set<List<Class<?>>> classLineages = ClassTree.getClassLineages(Object[][].class);
    assertThat(classLineages)
        .isNotEmpty()
        .hasSize(4)
        .containsExactlyInAnyOrder(
            Arrays.asList(Object.class, Cloneable.class, Object[].class, Cloneable[].class,
                Object[][].class),
            Arrays.asList(Object.class, Cloneable.class, Object[].class, Serializable[].class,
                Object[][].class),
            Arrays.asList(Object.class, Serializable.class, Object[].class, Cloneable[].class,
                Object[][].class),
            Arrays.asList(Object.class, Serializable.class, Object[].class, Serializable[].class,
                Object[][].class));
  }

  @Test
  public void getClassLineages_ofPrimitiveArray() {
    Set<List<Class<?>>> classLineages = ClassTree.getClassLineages(int[].class);
    assertThat(classLineages)
        .isNotEmpty()
        .hasSize(2)
        .containsExactlyInAnyOrder(
            Arrays.asList(Object.class, Serializable.class, int[].class),
            Arrays.asList(Object.class, Cloneable.class, int[].class));
  }

  @Test
  public void getClassLineages_ofExtendsObjectArray() {
    Set<List<Class<?>>> classLineages = ClassTree.getClassLineages(Primate[].class);
    assertThat(classLineages)
        .isNotEmpty()
        .hasSize(2)
        .containsExactlyInAnyOrder(
            Arrays.asList(Object.class, Serializable.class, Object[].class, Featherless[].class,
                Primate[].class),
            Arrays.asList(Object.class, Cloneable.class, Object[].class, Featherless[].class,
                Primate[].class));
  }

  @Test
  public void getClassLineages_ofObjectArray() {
    Set<List<Class<?>>> classLineages = ClassTree.getClassLineages(Object[].class);
    assertThat(classLineages)
        .isNotEmpty()
        .hasSize(2)
        .containsExactlyInAnyOrder(
            Arrays.asList(Object.class, Serializable.class, Object[].class),
            Arrays.asList(Object.class, Cloneable.class, Object[].class));
  }

  @Test
  public void mapBranch_producesNew() {
    ClassTree<String> foo = ClassTree.newInstance("foo");
    ClassTree<String> bar = ClassTree.newInstance("bar");
    ClassTree<String> fooBar = foo.mapBranch(Red.class, b -> bar.builder());
    assertThat(bar)
        .isNotSameAs(foo);
    assertThat(fooBar)
        .isNotSameAs(foo)
        .isNotSameAs(bar);
  }

  @Test
  public void mapBranch_producesNoCycle() {
    ClassTree<String> foo = ClassTree.newInstance("foo");
    ClassTree<String> fooBar = foo.mapBranch(Primate.class, b -> foo.builder());
    assertThat(fooBar)
        .isNotSameAs(foo);
    assertThat(foo.getBranches())
        .hasSize(0);
    assertThat(fooBar.getBranches())
        .hasSize(1);
  }

  @Test
  public void add_forClassExtendsObject() {
    ClassTree<String> t = tree.add(Red.class, "red");
    assertThat(t).isNotNull();
    assertThat(t.getValue()).hasValue("v");
    assertThat(t.getBranches()).containsOnlyKeys(Object.class);

    ClassTree<String> o = t.getBranches().get(Object.class);
    assertThat(o).isNotNull();
    assertThat(o.getValue()).isEmpty();
    assertThat(o.getBranches()).containsOnlyKeys(Red.class);

    ClassTree<String> r = o.getBranches().get(Red.class);
    assertThat(r).isNotNull();
    assertThat(r.getValue()).hasValue("red");
    assertThat(r.getBranches()).isEmpty();
  }

  @Test
  public void add_sameClassTwice_withSameValue() {
    ClassTree<String> t1 = tree.add(Red.class, "red")
        .add(Black.class, "black");
    ClassTree<String> t2 = t1.add(Red.class, "red");

    assertThat(t1).isEqualTo(t2);
  }

  @Test
  public void add_sameClassTwice_withDifferentValue() {
    ClassTree<String> t0 = tree.add(Red.class, "not red")
        .add(Black.class, "black");
    ClassTree<String> t = t0.add(Red.class, "red");

    assertThat(t).isNotEqualTo(t0);

    assertThat(t).isNotNull();
    assertThat(t.getValue()).hasValue("v");
    assertThat(t.getBranches()).containsOnlyKeys(Object.class);

    ClassTree<String> o = t.getBranches().get(Object.class);
    assertThat(o).isNotNull();
    assertThat(o.getValue()).isEmpty();
    assertThat(o.getBranches()).containsOnlyKeys(Red.class, Black.class);

    ClassTree<String> r = o.getBranches().get(Red.class);
    assertThat(r).isNotNull();
    assertThat(r.getValue()).hasValue("red");
    assertThat(r.getBranches()).isEmpty();
  }

  @Test
  public void add_forClassImplementsInterface() {
    ClassTree<String> t = tree.add(Red[].class, "red array");
    assertThat(t).isNotNull();
    assertThat(t.getValue()).hasValue("v");
    assertThat(t.getBranches()).containsOnlyKeys(Object.class);

    ClassTree<String> o = t.getBranches().get(Object.class);
    assertThat(o).isNotNull();
    assertThat(o.getValue()).isEmpty();
    assertThat(o.getBranches()).containsOnlyKeys(Cloneable.class, Serializable.class);

    ClassTree<String> c = o.getBranches().get(Cloneable.class);
    assertThat(c).isNotNull();
    assertThat(c.getValue()).isEmpty();
    assertThat(c.getBranches()).containsOnlyKeys(Object[].class);

    ClassTree<String> s = o.getBranches().get(Serializable.class);
    assertThat(s).isEqualTo(c);

    ClassTree<String> a = c.getBranches().get(Object[].class);
    assertThat(a).isNotNull();
    assertThat(a.getValue()).isEmpty();
    assertThat(a.getBranches()).containsOnlyKeys(Red[].class);

    ClassTree<String> r = a.getBranches().get(Red[].class);
    assertThat(r).isNotNull();
    assertThat(r.getValue()).hasValue("red array");
    assertThat(r.getBranches()).isEmpty();
  }

  @Test
  public void valueForClass_classExtendsObject() {
    ClassTree<String> r = tree.add(Red.class, "red");
    assertThat(r.findValueForClass(Red.class)).containsExactlyInAnyOrder("red");
    assertThat(r.findValueForClass(Object.class)).containsExactlyInAnyOrder("v");
    assertThat(r.findValueForClass(Black.class)).containsExactlyInAnyOrder("v");
    assertThat(r.findValueForClass(Crimson.class)).containsExactlyInAnyOrder("red");
  }

  @Test
  public void valueForClass_classExtendsTwoDimentionalArray() {
    ClassTree<String> r = tree.add(Primate[][].class, "primates");
    assertThat(r.findValueForClass(Human[][].class)).containsExactlyInAnyOrder("primates");
    assertThat(r.findValueForClass(Primate[][].class)).containsExactlyInAnyOrder("primates");
    assertThat(r.findValueForClass(Featherless[][].class)).containsExactlyInAnyOrder("v");
    assertThat(r.findValueForClass(Object.class)).containsExactlyInAnyOrder("v");
  }

  @Test
  public void valueForClass_withRefinedSubclass() {
    ClassTree<String> r = tree.add(Primate[][].class, "primates");
    r = r.add(Object[][].class, "objects");
    assertThat(r.findValueForClass(Human[][].class)).containsExactlyInAnyOrder("primates");
    assertThat(r.findValueForClass(Primate[][].class)).containsExactlyInAnyOrder("primates");
    assertThat(r.findValueForClass(Featherless[][].class)).containsExactlyInAnyOrder("objects");
    assertThat(r.findValueForClass(Object[][].class)).containsExactlyInAnyOrder("objects");
    assertThat(r.findValueForClass(Object.class)).containsExactlyInAnyOrder("v");
  }

  @Test
  public void valueForClass_noValueDefined() {
    tree = ClassTree.newInstanceWithoutValue();
    assertThat(tree.findValueForClass(Object.class)).isEmpty();
  }

  @Test
  public void valueForClass_tooManyValuesFound() {
    tree = tree.add(Featherless.class, "featherless");
    tree = tree.add(Biped.class, "biped");
    assertThat(tree.findValueForClass(Human.class))
        .containsExactlyInAnyOrder("featherless", "biped");
  }

  private interface Biped {
  }

  private interface Featherless {
  }

  private interface FeatherlessBiped extends Featherless, Biped {
  }

  private static class Red {
  }

  private static class Black {
  }

  private static class Crimson extends Red {
  }

  private static class FordColor extends Black implements Serializable {
  }

  private static class Primate implements Featherless {
  }

  private static class Human extends Primate implements FeatherlessBiped {
  }

  private static class PluckedChicken implements Featherless {
  }
}
