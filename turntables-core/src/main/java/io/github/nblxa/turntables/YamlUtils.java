package io.github.nblxa.turntables;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class YamlUtils {
  static String renderTab(Tab tab, int indent) {
    Objects.requireNonNull(tab, "tab");
    StringBuilder sb = new StringBuilder();
    String spaces = spaces(indent);
    sb.append(spaces);
    sb.append("Table:");
    List<Tab.Row> rows = tab.rows();
    if (rows.isEmpty()) {
      sb.append(" null");
    }
    for (Tab.Row row: rows) {
      sb.append(renderRow(row, indent + 4));
    }
    return sb.toString();
  }

  static String renderRow(Tab.Row row, int indent) {
    Objects.requireNonNull(row, "row");
    StringBuilder sb = new StringBuilder();
    List<Tab.Val> vals = row.vals();
    List<String> paddedColNames = rightPaddedColNames(row.cols());
    if (vals.size() != paddedColNames.size()) {
      throw new IllegalStateException();
    }
    if (!vals.isEmpty()) {
      String itemSpaces = spaces(indent + 2);
      for (int i = 0; i < vals.size(); i++) {
        sb.append(System.lineSeparator());
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
    return sb.toString();
  }

  static String renderVal(Tab.Val val) {
    if (val.typ() == Typ.STRING) {
      return StringUtils.escape(String.valueOf(val.eval()));
    } else {
      return String.valueOf(val.eval());
    }
  }

  private static List<String> rightPaddedColNames(List<Tab.Col> cols) {
    int colIndex = 0;
    int maxLength = 0;
    List<String> colNames = new ArrayList<>();
    for (Tab.Col col : cols) {
      String colName;
      if (col instanceof Tab.NamedCol) {
        colName = ((Tab.NamedCol) col).name();
      } else {
        colName = "col" + (++colIndex);
      }
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

  private YamlUtils() {
    throw new UnsupportedOperationException();
  }
}
