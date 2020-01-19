package io.github.nblxa.turntables.io.ingestion;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Objects;

public class TwoDimensionalArrayProtocol implements IngestionProtocol<Object[][]> {
  @NonNull
  @Override
  public Tab ingest(@NonNull Object[][] matrix) {
    Objects.requireNonNull(matrix);
    TableUtils.RowBuilder builder = new TableUtils.Builder()
        .rowAdder();
    for (Object[] objects: matrix) {
      builder.row(objects);
    }
    return builder.tab();
  }
}
