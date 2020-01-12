package io.github.nblxa.turntables.io.injestion;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Objects;

public class TwoDimensionalArrayProtocol implements InjestionProtocol<Object[][]> {
  @NonNull
  @Override
  public Tab injest(@NonNull Object[][] matrix) {
    Objects.requireNonNull(matrix);
    TableUtils.RowBuilder builder = new TableUtils.Builder()
        .rowAdder();
    for (Object[] objects: matrix) {
      builder.row(objects);
    }
    return builder.tab();
  }
}
