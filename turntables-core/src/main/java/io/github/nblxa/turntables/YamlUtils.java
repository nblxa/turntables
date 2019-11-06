package io.github.nblxa.turntables;

import java.util.ArrayList;
import java.util.Iterator;
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
    Iterator<Tab.Row> iter = tab.rows().iterator();
    if (!iter.hasNext()) {
      sb.append(" null");
    }
    while (iter.hasNext()) {
      sb.append(renderRow(iter.next(), indent + 4));
    }
    return sb.toString();
  }

  static String renderRow(Tab.Row row, int indent) {
    Objects.requireNonNull(row, "row");
    StringBuilder sb = new StringBuilder();
    Iterator<Tab.Val> iterVal = row.vals().iterator();
    Iterator<String> colNameIter = rightPaddedColNames(row.cols()).iterator();

    boolean firstCol = true;
    if (iterVal.hasNext()) {
      String itemSpaces = spaces(indent + 2);
      while (iterVal.hasNext()) {
        if (!colNameIter.hasNext()) {
          throw new IllegalStateException();
        }
        sb.append(System.lineSeparator());
        if (firstCol) {
          sb.append(spaces(indent));
          sb.append("- ");
          firstCol = false;
        } else {
          sb.append(itemSpaces);
        }
        Tab.Val val = iterVal.next();
        String paddedColName = colNameIter.next();
        sb.append(paddedColName);
        sb.append(": ");
        sb.append(val.toString());
      }
    }
    return sb.toString();
  }

  static String renderVal(Tab.Val val) {
    if (val.getTyp() == Typ.STRING) {
      return StringUtils.escape(String.valueOf(val.eval()));
    } else {
      return String.valueOf(val.eval());
    }
  }

  private static List<String> rightPaddedColNames(Iterable<Tab.Col> cols) {
    int colIndex = 0;
    int maxLength = 0;
    List<String> colNames = new ArrayList<>();
    for (Tab.Col col : cols) {
      colIndex++;
      String colName;
      if (col instanceof Tab.Named) {
        colName = ((Tab.Named) col).name();
      } else {
        colName = "col" + colIndex;
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
