package io.github.nblxa.turntables.ojdbc8;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.io.ingestion.ResultSetProtocol;
import oracle.jdbc.OracleResultSet;

public class OracleResultSetProtocol<T extends OracleResultSet> extends ResultSetProtocol<T> {
  @Override
  protected boolean isBoolean(ResultSetMetaData md, int jdbcIndex, Class<?> klass) {
    return false;
  }

  @Override
  protected boolean isDateTime(ResultSetMetaData md, int jdbcIndex, Class<?> klass) {
    return oracle.sql.TIMESTAMP.class.isAssignableFrom(klass);
  }

  @Override
  protected boolean isDate(ResultSetMetaData md, int jdbcIndex, Class<?> klass) {
    return java.sql.Timestamp.class.isAssignableFrom(klass);
  }

  @Override
  protected Object getDate(T rs, ResultSetMetaData md, int jdbcIndex, Typ typ) throws SQLException {
    Timestamp ts = rs.getTimestamp(jdbcIndex);
    if (ts == null) {
      return null;
    }
    return ts.toLocalDateTime().toLocalDate();
  }
}
