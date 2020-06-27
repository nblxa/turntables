package io.github.nblxa.turntables;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class YamlRenderer implements Renderer {
  @NonNull
  public static final Renderer DEFAULT_SIMPLE = new YamlRenderer(false, "Table");
  @NonNull
  public static final Renderer DEFAULT_EXTENDED = new YamlRenderer(true, "Table");
  @NonNull
  private static final YamlRenderer COLS_DETAIL = new YamlRenderer(false, "cols");
  @NonNull
  private static final String LS = System.lineSeparator();

  private final boolean extended;
  @NonNull
  private final String title;

  YamlRenderer(boolean extended, @NonNull String title) {
    this.extended = extended;
    this.title = Objects.requireNonNull(title, "title");
  }

  @Override
  @NonNull
  public String renderTab(@NonNull Tab tab, int indent) {
    Objects.requireNonNull(tab, "tab");
    StringBuilder sb = new StringBuilder();
    renderTab(sb, tab, indent);
    return sb.toString();
  }

  private void renderTab(@NonNull StringBuilder sb, @NonNull Tab tab, int indent) {
    sb.append(title);
    sb.append(":");
    renderCols(sb, tab.cols(), indent);
    renderRows(sb, tab.rows(), indent);
  }

  void renderCols(@NonNull StringBuilder sb, @NonNull List<Tab.Col> cols, int indent) {
    if (extended) {
      TableUtils.RowAdderTable colsTab = new TableUtils.ColAdderTable()
          .col("name", Typ.STRING)
          .col("type", Typ.STRING)
          .col("key", Typ.BOOLEAN)
          .rowAdder();
      for (Tab.Col col: cols) {
        colsTab.row(col.name(), col.typ().toString(), col.isKey());
      }
      sb.append(LS);
      spaces(sb, indent + 2);
      COLS_DETAIL.renderTab(sb, colsTab, indent + 2);
    }
  }

  void renderRows(@NonNull StringBuilder sb, @NonNull List<Tab.Row> rows, int indent) {
    int rowIndent = 4;
    if (extended) {
      sb.append(LS);
      spaces(sb, indent + 2);
      sb.append("rows:");
      rowIndent += 2;
    }
    if (rows.isEmpty()) {
      sb.append(" null");
    }
    for (Tab.Row row: rows) {
      renderRow(sb, row, indent + rowIndent);
    }
  }

  @Override
  @NonNull
  public String renderRow(@NonNull Tab.Row row, int indent) {
    Objects.requireNonNull(row, "row");
    StringBuilder sb = new StringBuilder();
    sb.append("Row:");
    renderRow(sb, row, indent + 4);
    return sb.toString();
  }

  private void renderRow(@NonNull StringBuilder sb, @NonNull Tab.Row row, int indent) {
    List<Tab.Val> vals = row.vals();
    List<String> paddedColNames = rightPaddedColNames(row.cols());
    if (vals.size() != paddedColNames.size()) {
      throw new IllegalStateException();
    }
    if (!vals.isEmpty()) {
      String itemSpaces = spaces(indent + 2);
      for (int i = 0; i < vals.size(); i++) {
        sb.append(LS);
        if (i == 0) {
          sb.append(spaces(indent));
          sb.append("- ");
        } else {
          sb.append(itemSpaces);
        }
        sb.append(paddedColNames.get(i));
        sb.append(": ");
        sb.append(vals.get(i).toString());
      }
    }
  }

  @Override
  @NonNull
  public String renderVal(@NonNull Tab.Val val) {
    if (val.typ() == Typ.STRING) {
      return StringUtils.escape(String.valueOf(val.evaluate()));
    } else {
      return String.valueOf(val.evaluate());
    }
  }

  @NonNull
  private List<String> rightPaddedColNames(@NonNull List<Tab.Col> cols) {
    int maxLength = 0;
    List<String> colNames = new ArrayList<>();
    for (Tab.Col col : cols) {
      String colName = col.name();
      String colEscaped = StringUtils.escape(colName);
      if (colEscaped.length() > maxLength) {
        maxLength = colEscaped.length();
      }
      colNames.add(colEscaped);
    }
    final int finalMaxLength = maxLength;
    return colNames.stream()
        .map(c -> c + spaces(finalMaxLength + 1 - c.length()))
        .collect(Collectors.toList());
  }

  private static void spaces(@NonNull StringBuilder sb, int indent) {
    for (int i = 0; i < indent; i++) {
      sb.append(' ');
    }
  }

  @NonNull
  private static String spaces(int indent) {
    if (indent == 0) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < indent; i++) {
      sb.append(' ');
    }
    return sb.toString();
  }
}
