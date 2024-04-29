/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.runtime.truffle.ast.io.jdbc;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.nodes.Node;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import raw.client.api.LocationDescription;
import raw.runtime.truffle.runtime.exceptions.rdbms.JdbcExceptionHandler;
import raw.runtime.truffle.runtime.exceptions.rdbms.JdbcReaderRawTruffleException;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.primitives.DecimalObject;
import raw.runtime.truffle.runtime.primitives.TimeObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;
import raw.sources.api.SourceContext;
import raw.sources.jdbc.api.JdbcLocationProvider;
import raw.utils.RawException;

public class JdbcQuery {

  private final Connection connection;
  private final ResultSet rs;
  private final JdbcExceptionHandler exceptionHandler;
  private final String url;

  @TruffleBoundary
  public JdbcQuery(
      LocationDescription locationDescription,
      String query,
      SourceContext context,
      JdbcExceptionHandler exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
    this.url = locationDescription.url();
    try {
      connection = JdbcLocationProvider.build(locationDescription, context).getJdbcConnection();
      PreparedStatement stmt;
      int fetchSize = context.settings().getInt("raw.runtime.rdbms.fetch-size");
      try {
        stmt = connection.prepareStatement(query);
        stmt.setFetchSize(fetchSize);
        rs = stmt.executeQuery();
      } catch (SQLException e) {
        throw exceptionHandler.rewrite(e, this);
      }
    } catch (RawException e) {
      // exceptions due to location errors (e.g. connection failures) are turned into runtime
      // exceptions.
      throw new JdbcReaderRawTruffleException(e.getMessage(), this, e, null);
    }
  }

  @TruffleBoundary
  public void close() {
    if (rs != null) {
      try {
        rs.close();
        connection.close();
      } catch (SQLException ignored) {
      }
    }
  }

  @TruffleBoundary
  public boolean next() {
    try {
      return rs.next();
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this);
    }
  }

  @TruffleBoundary
  byte getByte(String colName, Node node) {
    try {
      return rs.getByte(colName);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  byte getByte(int idx, Node node) {
    try {
      return rs.getByte(idx + 1);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, idx, rs, node);
    }
  }

  @TruffleBoundary
  short getShort(String colName, Node node) {
    try {
      return rs.getShort(colName);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  short getShort(int idx, Node node) {
    try {
      return rs.getShort(idx + 1);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, idx, rs, node);
    }
  }

  @TruffleBoundary
  int getInt(String colName, Node node) {
    try {
      return rs.getInt(colName);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  int getInt(int idx, Node node) {
    try {
      return rs.getInt(idx + 1);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, idx, rs, node);
    }
  }

  @TruffleBoundary
  long getLong(String colName, Node node) {
    try {
      return rs.getLong(colName);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  long getLong(int idx, Node node) {
    try {
      return rs.getLong(idx + 1);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, idx, rs, node);
    }
  }

  @TruffleBoundary
  float getFloat(String colName, Node node) {
    try {
      return rs.getFloat(colName);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  float getFloat(int idx, Node node) {
    try {
      return rs.getFloat(idx + 1);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, idx, rs, node);
    }
  }

  @TruffleBoundary
  double getDouble(String colName, Node node) {
    try {
      return rs.getDouble(colName);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  double getDouble(int idx, Node node) {
    try {
      return rs.getDouble(idx + 1);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, idx, rs, node);
    }
  }

  @TruffleBoundary
  DecimalObject getDecimal(String colName, Node node) {
    try {
      return new DecimalObject(rs.getBigDecimal(colName));
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  DecimalObject getDecimal(int idx, Node node) {
    try {
      return new DecimalObject(rs.getBigDecimal(idx + 1));
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, idx, rs, node);
    }
  }

  @TruffleBoundary
  String getString(String colName, Node node) {
    try {
      return rs.getString(colName);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  String getString(int idx, Node node) {
    try {
      return rs.getString(idx + 1);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, idx, rs, node);
    }
  }

  @TruffleBoundary
  boolean getBool(String colName, Node node) {
    try {
      return rs.getBoolean(colName);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  boolean getBool(int idx, Node node) {
    try {
      return rs.getBoolean(idx + 1);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, idx, rs, node);
    }
  }

  @TruffleBoundary
  DateObject getDate(String colName, Node node) {
    try {
      java.sql.Date sqlDate = rs.getDate(colName);
      return new DateObject(sqlDate.toLocalDate());
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  DateObject getDate(int idx, Node node) {
    try {
      java.sql.Date sqlDate = rs.getDate(idx + 1);
      return new DateObject(sqlDate.toLocalDate());
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, idx, rs, node);
    }
  }

  @TruffleBoundary
  TimeObject getTime(String colName, Node node) {
    try {
      java.sql.Time sqlTime = rs.getTime(colName);
      return new TimeObject(sqlTime.toLocalTime());
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  TimeObject getTime(int idx, Node node) {
    try {
      java.sql.Time sqlTime = rs.getTime(idx + 1);
      return new TimeObject(sqlTime.toLocalTime());
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, idx, rs, node);
    }
  }

  @TruffleBoundary
  TimestampObject getTimestamp(String colName, Node node) {
    try {
      java.sql.Timestamp sqlTimestamp = rs.getTimestamp(colName);
      return new TimestampObject(sqlTimestamp.toLocalDateTime());
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  TimestampObject getTimestamp(int idx, Node node) {
    try {
      java.sql.Timestamp sqlTimestamp = rs.getTimestamp(idx + 1);
      return new TimestampObject(sqlTimestamp.toLocalDateTime());
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, idx, rs, node);
    }
  }

  @TruffleBoundary
  byte[] getBytes(String colName, Node node) {
    try {
      return rs.getBytes(colName);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  byte[] getBytes(int idx, Node node) {
    try {
      return rs.getBytes(idx + 1);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, idx, rs, node);
    }
  }

  @TruffleBoundary
  boolean isNull(String colName, Node node) {
    try {
      rs.getObject(colName);
      return rs.wasNull();
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  boolean isNull(int idx, Node node) {
    try {
      rs.getObject(idx + 1);
      return rs.wasNull();
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, idx, rs, node);
    }
  }

  public String location() {
    return url;
  }
}
