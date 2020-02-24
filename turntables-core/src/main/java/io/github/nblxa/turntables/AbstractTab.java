package io.github.nblxa.turntables;

import io.github.nblxa.turntables.exception.AssertionEvaluationException;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbstractTab implements Tab {
  @NonNull
  private final List<Col> cols;

  AbstractTab() {
    this.cols = new ArrayList<>();
  }

  public AbstractTab(Iterable<Col> cols) {
    this.cols = Objects.requireNonNull(Utils.toArrayList(cols), "cols");
  }

  @Override
  @NonNull
  public Iterable<Col> cols() {
    return cols;
  }

  List<Col> colsList() {
    return cols;
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
    return YamlUtils.renderTab(this, 0);
  }

  public boolean canEqual(Object other) {
    return other instanceof AbstractTab;
  }

  public abstract static class AbstractRow implements Row {
    @NonNull
    private final Iterable<Tab.Col> cols;

    public AbstractRow(Iterable<Tab.Col> cols) {
      this.cols = Objects.requireNonNull(cols, "cols");
    }

    @Override
    @NonNull
    public Iterable<Tab.Col> cols() {
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
      return YamlUtils.renderRow(this, 0);
    }
  }

  public abstract static class AbstractVal implements Tab.Val {
    public boolean canEqual(Object o) {
      return o instanceof AbstractVal;
    }
  }

  public abstract static class AbstractCol implements Col {
    @NonNull
    private final Typ typ;
    private final boolean isKey;

    public AbstractCol(Typ typ, boolean isKey) {
      this.typ = Objects.requireNonNull(typ, "typ is null");
      this.isKey = isKey;
    }

    @Override
    @NonNull
    public Typ typ() {
      return typ;
    }

    @Override
    public boolean isKey() {
      return isKey;
    }

    @Override
    @NonNull
    public Val valOf(@Nullable Object o) {
      return Utils.getVal(o, typ);
    }

    @Override
    public boolean accepts(@NonNull Val val) {
      return typ.acceptsTyp(val.getTyp());
    }

    public boolean canEqual(Object o) {
      return o instanceof AbstractCol;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof AbstractCol)) {
        return false;
      }
      AbstractCol abstractCol = (AbstractCol) o;
      return abstractCol.canEqual(this)
          && typ() == abstractCol.typ()
          && isKey() == abstractCol.isKey();
    }

    @Override
    public int hashCode() {
      return Objects.hash(typ(), isKey());
    }

    @NonNull
    @Override
    public String toString() {
      if (isKey()) {
        return String.format("[KEY %s]", typ());
      } else {
        return String.format("[%s]", typ());
      }
    }
  }

  public abstract static class AbstractAssertionVal extends AbstractVal {
    static final Typ TYP = Typ.ANY;

    @NonNull
    private final Predicate<Object> assertionPredicate;
    @NonNull
    private final Supplier<String> toStringSupplier;

    AbstractAssertionVal(Predicate<Object> assertionPredicate, Supplier<String> toStringSupplier) {
      this.assertionPredicate =
          Objects.requireNonNull(assertionPredicate, "assertionPredicate");
      this.toStringSupplier =
          Objects.requireNonNull(toStringSupplier, "toStringSupplier");
    }

    @Override
    @NonNull
    public Typ getTyp() {
      return TYP;
    }

    @Override
    @Nullable
    public Object eval() {
      throw new AssertionEvaluationException();
    }

    @Override
    public boolean matchesActual(@NonNull Val actual) {
      Objects.requireNonNull(actual, "other");
      return assertionPredicate.test(actual.eval());
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof AbstractAssertionVal)) {
        return false;
      }
      AbstractAssertionVal that = (AbstractAssertionVal) o;
      return that.canEqual(this) && assertionPredicate.equals(that.assertionPredicate)
          && toStringSupplier.equals(that.toStringSupplier);
    }

    @Override
    public int hashCode() {
      return Objects.hash(assertionPredicate, toStringSupplier);
    }

    @Override
    public boolean canEqual(Object o) {
      return o instanceof AbstractAssertionVal;
    }

    @Override
    public String toString() {
      return toStringSupplier.get();
    }
  }
}
