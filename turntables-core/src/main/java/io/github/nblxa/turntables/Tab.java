package io.github.nblxa.turntables;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface Tab {
  @NonNull
  Iterable<Col> cols();

  @NonNull
  Iterable<Row> rows();

  interface Col {
    @NonNull
    Typ typ();

    @NonNull
    Tab.Val valOf(@Nullable Object o);

    boolean accepts(@NonNull Val val);

    boolean isKey();
  }

  interface Val {
    @NonNull
    Typ getTyp();

    @Nullable
    Object eval();

    boolean matchesActual(@NonNull Val other);
  }

  interface Row {
    @NonNull
    Iterable<Col> cols();

    @NonNull
    Iterable<Val> vals();
  }

  interface ColAdder<U extends UnnamedColAdder<U, R>, N extends NamedColAdder<N, R>,
      R extends RowAdder<R>>
    extends UnnamedColAdderPart<U>, NamedColAdderPart<N>, ColAdderRowAdderPart<R> {
  }

  interface UnnamedColAdder<U extends UnnamedColAdder<U, R>, R extends RowAdder<R>>
    extends UnnamedColAdderPart<U>, ColAdderRowAdderPart<R> {
  }

  interface NamedColAdder<N extends NamedColAdder<N, R>, R extends RowAdder<R>>
    extends NamedColAdderPart<N>, ColAdderRowAdderPart<R> {
  }

  interface UnnamedColAdderPart<U extends UnnamedColAdderPart<U>> {
    @NonNull
    U col(@NonNull Typ typ);

    @NonNull
    U key(@NonNull Typ typ);
  }

  interface NamedColAdderPart<N extends NamedColAdderPart<N>> {
    @NonNull
    N col(@NonNull String name, @NonNull Typ typ);

    @NonNull
    N key(@NonNull String name, @NonNull Typ typ);
  }

  interface ColAdderRowAdderPart<R extends RowAdder<R>> {
    @NonNull
    R rowAdder();
  }

  interface RowAdder<T extends RowAdder<T>> {
    @NonNull
    T row(@Nullable Object first, @NonNull Object... rest);

    @NonNull
    Tab tab();
  }

  interface NamedCol extends Tab.Col {
    @NonNull
    String name();
  }

  interface NamedColTab extends Tab {
    @NonNull
    Iterable<NamedCol> namedCols();
  }
}
