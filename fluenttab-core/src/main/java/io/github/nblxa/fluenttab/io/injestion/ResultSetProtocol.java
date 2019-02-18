package io.github.nblxa.fluenttab.io.injestion;

import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.TableUtils;
import io.github.nblxa.fluenttab.Typ;
import io.github.nblxa.fluenttab.exception.InjestionException;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetProtocol implements InjestionProtocol<ResultSet> {
  @Override
  @NonNull
  public Tab injest(@NonNull ResultSet rs) {
    TableUtils.Builder builder = new TableUtils.Builder();
    try {
      ResultSetMetaData md = rs.getMetaData();
      int columnCount = md.getColumnCount();
      for (int i = 1; i <= columnCount; i++) {
        String columnName = md.getColumnName(i);
        builder.col(columnName, Typ.ANY);
      }
      TableUtils.RowBuilder rowBuilder = builder.rowBuilder();
      while (rs.next()) {
        Object[] objects = new Object[columnCount];
        for (int i = 1; i <= columnCount; i++) {
          objects[i - 1] = rs.getObject(i);
        }
        rowBuilder.row(objects);
      }
      return rowBuilder.build();
    } catch (SQLException se) {
      throw new InjestionException(se);
    }
  }
}
