package io.github.nblxa.turntables.generator;

import edu.umd.cs.findbugs.annotations.NonNull;

public class Coordinate {
  private final int row;
  private final int col;
  @NonNull
  private final String colName;

  public Coordinate(int row, int col, @NonNull String colName) {
    this.row = row;
    this.col = col;
    this.colName = colName;
  }

  public int row() {
    return row;
  }

  public int col() {
    return col;
  }

  @NonNull
  public String colName() {
    return colName;
  }
}
