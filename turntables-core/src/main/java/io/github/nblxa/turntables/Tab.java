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

  interface Named {
    @NonNull
    String name();
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

  interface ColAdder<T extends ColAdder<T>> {
    @NonNull
    T col(@NonNull Typ typ);

    @NonNull
    T col(@NonNull String name, @NonNull Typ typ);

    @NonNull
    T key(@NonNull String name, @NonNull Typ typ);

    @NonNull
    T key(@NonNull Typ typ);
  }

  interface RowAdder<T extends RowAdder<T>> {
    @NonNull
    T row(@Nullable Object first, @NonNull Object... rest);
  }
}
