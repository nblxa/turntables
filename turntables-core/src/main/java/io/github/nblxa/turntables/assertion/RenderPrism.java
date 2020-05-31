package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Renderer;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.YamlRenderer;
import java.util.List;
import java.util.Objects;

public class RenderPrism extends Prism {
  @NonNull
  static Prism createFrom(@NonNull Tab tab, @NonNull Asserter asserter) {
    return new RenderPrism(tab, asserter);
  }

  @NonNull
  private final Tab tab;
  @NonNull
  private final Renderer renderer;

  public RenderPrism(@NonNull Tab tab, @NonNull Asserter asserter) {
    super(tab.cols());
    this.tab = Objects.requireNonNull(tab, "tab");
    this.renderer = getRenderer(Objects.requireNonNull(asserter, "asserter"));
  }

  private static Renderer getRenderer(Asserter asserter) {
    if (asserter.getOrCalculateResult().colsMatched() == AssertionResult.MatchResult.MISMATCH) {
      return YamlRenderer.DEFAULT_EXTENDED;
    } else {
      return YamlRenderer.DEFAULT_SIMPLE;
    }
  }

  @NonNull
  @Override
  public List<Row> rows() {
    return tab.rows();
  }

  @NonNull
  @Override
  public String representation() {
    return renderer.renderTab(tab, 0);
  }
}
