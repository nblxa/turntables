package io.github.nblxa.turntables.io.ingestion;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.exception.IngestionException;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetProtocol implements IngestionProtocol<ResultSet> {
  @Override
  @NonNull
  public Tab ingest(@NonNull ResultSet rs) {
    TableUtils.NamedColBuilder builder = TableUtils.Builder.named();
    try {
      ResultSetMetaData md = rs.getMetaData();
      int columnCount = md.getColumnCount();
      for (int i = 1; i <= columnCount; i++) {
        String columnName = md.getColumnName(i);
        builder.col(columnName, Typ.ANY);
      }
      TableUtils.RowBuilder rowBuilder = builder.rowAdder();
      while (rs.next()) {
        Object[] objects = new Object[columnCount];
        for (int i = 1; i <= columnCount; i++) {
          objects[i - 1] = rs.getObject(i);
        }
        rowBuilder.row(objects);
      }
      return rowBuilder.tab();
    } catch (SQLException se) {
      throw new IngestionException(se);
    }
  }
}
