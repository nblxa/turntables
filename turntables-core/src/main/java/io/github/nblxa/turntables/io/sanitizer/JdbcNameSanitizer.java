package io.github.nblxa.turntables.io.sanitizer;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.sql.Connection;
import java.util.regex.Pattern;

public class JdbcNameSanitizer extends NameSanitizer<Connection> {
  private static final JdbcNameSanitizer INSTANCE = new JdbcNameSanitizer();
  private static final Pattern DEFAULT_NAME_REGEX = Pattern.compile("^[a-z_][0-9a-z_$#]*$",
      Pattern.CASE_INSENSITIVE);

  public static JdbcNameSanitizer getInstance() {
    return INSTANCE;
  }

  private JdbcNameSanitizer() {
  }

  @Override
  protected boolean nameOk(@NonNull Connection conn, @NonNull String name){
    return DEFAULT_NAME_REGEX.matcher(name).find();
  }
}
