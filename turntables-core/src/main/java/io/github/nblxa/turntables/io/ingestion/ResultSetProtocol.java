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

public class ResultSetProtocol<T extends ResultSet> implements IngestionProtocol<T> {
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
    try {
      Class<?> klass = getClass().getClassLoader().loadClass(className);
      if (isBoolean(md, jdbcIndex, klass)) {
        return Typ.BOOLEAN;
      }
      if (isDateTime(md, jdbcIndex, klass)) {
        return Typ.DATETIME;
      }
      if (isDate(md, jdbcIndex, klass)) {
        return Typ.DATE;
      }
      if (isDecimal(md, jdbcIndex, klass)) {
        return Typ.DECIMAL;
      }
      if (isDouble(md, jdbcIndex, klass)) {
        return Typ.DOUBLE;
      }
      if (isInteger(md, jdbcIndex, klass)) {
        return Typ.INTEGER;
      }
      if (isLong(md, jdbcIndex, klass)) {
        return Typ.LONG;
      }
      if (isString(md, jdbcIndex, klass)) {
        return Typ.STRING;
      }
      return Typ.ANY;
    } catch (ClassNotFoundException cnfe) {
      throw new UnsupportedOperationException(cnfe);
    }
  }

  protected Object getValue(T rs, ResultSetMetaData md, int jdbcIndex)
      throws SQLException {
    Typ typ = getTyp(md, jdbcIndex);
    Object res;
    switch (typ) {
      case BOOLEAN:
        res = getBoolean(rs, md, jdbcIndex, typ);
        break;

      case DATETIME:
        res = getDateTime(rs, md, jdbcIndex, typ);
        break;

      case DATE:
        res = getDate(rs, md, jdbcIndex, typ);
        break;

      case DECIMAL:
        res = getDecimal(rs, md, jdbcIndex, typ);
        break;

      case DOUBLE:
        res = getDouble(rs, md, jdbcIndex, typ);
        break;

      case INTEGER:
        res = getInteger(rs, md, jdbcIndex, typ);
        break;

      case LONG:
        res = getLong(rs, md, jdbcIndex, typ);
        break;

      case STRING:
        res = getString(rs, md, jdbcIndex, typ);
        break;

      default:
        res = getObject(rs, md, jdbcIndex, typ);
    }
    if (rs.wasNull()) {
      return null;
    }
    return res;
  }

  @SuppressWarnings("unused")
  protected boolean isBoolean(ResultSetMetaData md, int jdbcIndex, Class<?> klass) {
    return klass == Boolean.class;
  }

  @SuppressWarnings("unused")
  protected boolean isDateTime(ResultSetMetaData md, int jdbcIndex, Class<?> klass) {
    return java.sql.Timestamp.class.isAssignableFrom(klass);
  }

  @SuppressWarnings("unused")
  protected boolean isDate(ResultSetMetaData md, int jdbcIndex, Class<?> klass) {
    return java.util.Date.class.isAssignableFrom(klass) && !isDateTime(md, jdbcIndex, klass);
  }

  @SuppressWarnings("unused")
  protected boolean isDecimal(ResultSetMetaData md, int jdbcIndex, Class<?> klass) {
    return BigDecimal.class.isAssignableFrom(klass) || BigInteger.class.isAssignableFrom(klass);
  }

  @SuppressWarnings("unused")
  protected boolean isDouble(ResultSetMetaData md, int jdbcIndex, Class<?> klass) {
    return klass == Double.class || klass == Float.class;
  }

  @SuppressWarnings("unused")
  protected boolean isInteger(ResultSetMetaData md, int jdbcIndex, Class<?> klass) {
    return klass == Integer.class;
  }

  @SuppressWarnings("unused")
  protected boolean isLong(ResultSetMetaData md, int jdbcIndex, Class<?> klass) {
    return klass == Long.class;
  }

  @SuppressWarnings("unused")
  protected boolean isString(ResultSetMetaData md, int jdbcIndex, Class<?> klass) {
    return CharSequence.class.isAssignableFrom(klass);
  }

  @SuppressWarnings("unused")
  protected Object getBoolean(T rs, ResultSetMetaData md, int jdbcIndex, Typ typ) throws SQLException {
    return rs.getBoolean(jdbcIndex);
  }

  @SuppressWarnings("unused")
  protected Object getDateTime(T rs, ResultSetMetaData md, int jdbcIndex, Typ typ) throws SQLException {
    return rs.getTimestamp(jdbcIndex);
  }

  @SuppressWarnings("unused")
  protected Object getDate(T rs, ResultSetMetaData md, int jdbcIndex, Typ typ) throws SQLException {
    return rs.getDate(jdbcIndex);
  }

  @SuppressWarnings("unused")
  protected Object getDecimal(T rs, ResultSetMetaData md, int jdbcIndex, Typ typ) throws SQLException {
    return rs.getBigDecimal(jdbcIndex);
  }

  @SuppressWarnings("unused")
  protected Object getDouble(T rs, ResultSetMetaData md, int jdbcIndex, Typ typ) throws SQLException {
    return rs.getDouble(jdbcIndex);
  }

  @SuppressWarnings("unused")
  protected Object getInteger(T rs, ResultSetMetaData md, int jdbcIndex, Typ typ) throws SQLException {
    return rs.getInt(jdbcIndex);
  }

  @SuppressWarnings("unused")
  protected Object getLong(T rs, ResultSetMetaData md, int jdbcIndex, Typ typ) throws SQLException {
    return rs.getLong(jdbcIndex);
  }

  @SuppressWarnings("unused")
  protected Object getString(T rs, ResultSetMetaData md, int jdbcIndex, Typ typ) throws SQLException {
    return rs.getString(jdbcIndex);
  }

  @SuppressWarnings("unused")
  protected Object getObject(T rs, ResultSetMetaData md, int jdbcIndex, Typ typ) throws SQLException {
    return rs.getObject(jdbcIndex);
  }
}
