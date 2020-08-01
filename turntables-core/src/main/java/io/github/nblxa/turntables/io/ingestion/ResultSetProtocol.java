package io.github.nblxa.turntables.io.ingestion;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.exception.IngestionException;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResultSetProtocol<T extends ResultSet> implements IngestionProtocol<T> {
  private static final Map<String, Typ> CLASS_TYPES = new ConcurrentHashMap<>();

  @Override
  @NonNull
  public Tab ingest(@NonNull T rs) {
    TableUtils.NamedColBuilder builder = TableUtils.Builder.named();
    try {
      ResultSetMetaData md = rs.getMetaData();
      int columnCount = md.getColumnCount();
      for (int i = 1; i <= columnCount; i++) {
        String columnName = md.getColumnName(i);
        Typ typ = getTyp(md, i);
        builder.col(columnName, typ);
      }
      TableUtils.RowBuilder rowBuilder = builder.rowAdder();
      while (rs.next()) {
        Object[] objects = new Object[columnCount];
        for (int i = 1; i <= columnCount; i++) {
          objects[i - 1] = getValue(rs, md, i);
        }
        rowBuilder.row(objects);
      }
      return rowBuilder.tab();
    } catch (SQLException se) {
      throw new IngestionException(se);
    }
  }

  protected Typ getTyp(ResultSetMetaData md, int jdbcIndex) throws SQLException {
    String className = md.getColumnClassName(jdbcIndex);
    return CLASS_TYPES.computeIfAbsent(className, cn -> {
      try {
        Class<?> klass = getClass().getClassLoader().loadClass(cn);
        if (klass == Boolean.class) {
          return Typ.BOOLEAN;
        }
        if (java.sql.Timestamp.class.isAssignableFrom(klass)) {
          return Typ.DATETIME;
        }
        if (java.util.Date.class.isAssignableFrom(klass)) {
          return Typ.DATE;
        }
        if (BigDecimal.class.isAssignableFrom(klass) || BigInteger.class.isAssignableFrom(klass)) {
          return Typ.DECIMAL;
        }
        if (klass == Double.class || klass == Float.class) {
          return Typ.DOUBLE;
        }
        if (klass == Integer.class) {
          return Typ.INTEGER;
        }
        if (klass == Long.class) {
          return Typ.LONG;
        }
        if (CharSequence.class.isAssignableFrom(klass)) {
          return Typ.STRING;
        }
        return Typ.ANY;
      } catch (ClassNotFoundException cnfe) {
        throw new UnsupportedOperationException(cnfe);
      }
    });
  }

  protected Object getValue(ResultSet rs, ResultSetMetaData md, int jdbcIndex)
      throws SQLException {
    Typ typ = getTyp(md, jdbcIndex);
    Object res;
    switch (typ) {
      case BOOLEAN:
        res = rs.getBoolean(jdbcIndex);
        break;

      case DATETIME:
        res = rs.getTimestamp(jdbcIndex);
        break;

      case DATE:
        res = rs.getDate(jdbcIndex);
        break;

      case DECIMAL:
        res = rs.getBigDecimal(jdbcIndex);
        break;

      case DOUBLE:
        res = rs.getDouble(jdbcIndex);
        break;

      case INTEGER:
        res = rs.getInt(jdbcIndex);
        break;

      case LONG:
        res = rs.getLong(jdbcIndex);
        break;

      case STRING:
        res = rs.getString(jdbcIndex);
        break;

      default:
        res = rs.getObject(jdbcIndex);
    }
    if (rs.wasNull()) {
      return null;
    }
    return res;
  }
}
