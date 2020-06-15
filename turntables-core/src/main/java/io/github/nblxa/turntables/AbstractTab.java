package io.github.nblxa.turntables;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractTab implements Tab {
  @NonNull
  private final List<Col> mutableCols;
  @NonNull
  private final List<Col> cols;

  AbstractTab() {
    this(Collections.emptyList());
  }

  public AbstractTab(List<Col> cols) {
    this.mutableCols = new ArrayList<>(Objects.requireNonNull(cols, "cols"));
    this.cols = Collections.unmodifiableList(this.mutableCols);
  }

  @Override
  @NonNull
  public List<Col> cols() {
    return cols;
  }

  List<Col> mutableCols() {
    return mutableCols;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Tab)) {
      return false;
    }
    final Tab tab;
    if (o instanceof AbstractTab) {
      AbstractTab abstractTab = (AbstractTab) o;
      if (!abstractTab.canEqual(this)) {
        return false;
      }
      tab = abstractTab;
    } else {
      tab = (Tab) o;
    }
    for (boolean eq: Utils.paired(this.cols(), tab.cols(), Objects::equals)) {
      if (!eq) {
        return false;
      }
    }
    for (boolean eq: Utils.paired(this.rows(), tab.rows(), Objects::equals)) {
      if (!eq) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(cols(), rows());
  }

  @Override
  public String toString() {
    return renderer().renderTab(this, 0);
  }

  public boolean canEqual(Object other) {
    return other instanceof AbstractTab;
  }

  protected Renderer renderer() {
    return YamlRenderer.DEFAULT_SIMPLE;
  }

  public abstract static class AbstractRow implements Row {
    @NonNull
    private final List<Tab.Col> cols;

    public AbstractRow(@NonNull List<Tab.Col> cols) {
      this.cols = Collections.unmodifiableList(
          new ArrayList<>(Objects.requireNonNull(cols, "cols")));
    }

    @Override
    @NonNull
    public List<Tab.Col> cols() {
      return cols;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof AbstractRow)) {
        return false;
      }
      AbstractRow abstractRow = (AbstractRow) o;
      return abstractRow.canEqual(this) && cols().equals(abstractRow.cols())
          && vals().equals(abstractRow.vals());
    }

    @Override
    public int hashCode() {
      return Objects.hash(cols(), vals());
    }

    public boolean canEqual(Object o) {
      return o instanceof AbstractRow;
    }

    @Override
    public String toString() {
      return renderer().renderRow(this, 0);
    }

    protected Renderer renderer() {
      return YamlRenderer.DEFAULT_SIMPLE;
    }
  }

  public abstract static class AbstractVal implements Tab.Val {
    public boolean canEqual(Object o) {
      return o instanceof AbstractVal;
    }

    protected Renderer renderer() {
      return YamlRenderer.DEFAULT_SIMPLE;
    }

    @Nullable
    @Override
    public Object evaluateAs(@NonNull Typ typ) {
      Object obj = evaluate();
      if (typ == typ() || obj == null) {
        return obj;
      } else {
        return typ.convert(obj);
      }
    }

    @Override
    public boolean matchesActual(@NonNull Tab.Val actual) {
      Objects.requireNonNull(actual, "actual is null");
      return Objects.equals(evaluate(), actual.evaluateAs(typ()));
    }

    @Override
    public String toString() {
      return renderer().renderVal(this);
    }
  }

  public abstract static class AbstractCol implements Col {
    @NonNull
    private final String name;
    @NonNull
    private final Typ typ;
    private final boolean isKey;

    public AbstractCol(@NonNull String name, @NonNull Typ typ, boolean isKey) {
      this.name = Objects.requireNonNull(name, "name is null");
      this.typ = Objects.requireNonNull(typ, "typ is null");
      this.isKey = isKey;
    }

    @NonNull
    @Override
    public String name() {
      return name;
    }

    @NonNull
    @Override
    public Typ typ() {
      return typ;
    }

    @Override
    public boolean isKey() {
      return isKey;
    }

    public boolean canEqual(Object o) {
      return o instanceof AbstractCol;
    }

    @Override
    public final boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof AbstractCol)) {
        return false;
      }
      AbstractCol abstractCol = (AbstractCol) o;
      return abstractCol.canEqual(this)
          && Objects.equals(name(), abstractCol.name())
          && typ() == abstractCol.typ()
          && isKey() == abstractCol.isKey();
    }

    @Override
    public final int hashCode() {
      return Objects.hash(name(), typ(), isKey());
    }

    @NonNull
    @Override
    public String toString() {
      if (isKey()) {
        return String.format("[%s KEY %s]", name(), typ());
      } else {
        return String.format("[%s %s]", name(), typ());
      }
    }
  }
}
