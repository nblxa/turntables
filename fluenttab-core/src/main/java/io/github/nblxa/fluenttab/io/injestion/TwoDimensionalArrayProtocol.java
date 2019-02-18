package io.github.nblxa.fluenttab.io.injestion;

import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.TableUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Objects;

public class TwoDimensionalArrayProtocol implements InjestionProtocol<Object[][]> {
  @NonNull
  @Override
  public Tab injest(@NonNull Object[][] matrix) {
    Objects.requireNonNull(matrix);
    TableUtils.RowBuilder builder = new TableUtils.Builder()
        .rowBuilder();
    for (Object[] objects: matrix) {
      builder.row(objects);
    }
    return builder.build();
  }
}
