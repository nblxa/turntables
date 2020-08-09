package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.io.rowstore.JdbcRowStore;
import io.github.nblxa.turntables.io.rowstore.RowStore;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.function.Supplier;

public class TestDataFactory {
  @NonNull
  public static TestDataSource jdbc(@NonNull Supplier<String> url, @NonNull String username, @NonNull String password) {
    RowStore rowStore = new JdbcRowStore(() -> DriverManager.getConnection(url.get(), username, password));
    return new TestDataSource(rowStore);
  }

  @NonNull
  public static TestDataSource jdbc(@NonNull Supplier<String> url, @NonNull Properties jdbcProperties) {
    RowStore rowStore = new JdbcRowStore(() -> DriverManager.getConnection(url.get(), jdbcProperties));
    return new TestDataSource(rowStore);
  }
}
