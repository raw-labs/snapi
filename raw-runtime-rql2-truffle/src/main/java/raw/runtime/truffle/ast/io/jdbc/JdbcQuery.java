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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import raw.api.RawException;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.runtime.exceptions.rdbms.JdbcExceptionHandler;
import raw.runtime.truffle.runtime.exceptions.rdbms.JdbcReaderRawTruffleException;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.primitives.TimeObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;
import raw.sources.LocationDescription;
import raw.sources.jdbc.JdbcLocationProvider;

public class JdbcQuery {

  private final Connection connection;
  private final ResultSet rs;
  private final JdbcExceptionHandler exceptionHandler;
  private final String url;

  public JdbcQuery(
      LocationDescription locationDescription,
      String query,
      RuntimeContext context,
      JdbcExceptionHandler exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
    this.url = locationDescription.url();
    try {
      connection =
          JdbcLocationProvider.build(locationDescription, context.sourceContext())
              .getJdbcConnection();
      PreparedStatement stmt;
      try {
        stmt = connection.prepareStatement(query);
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

  public void close() {
    if (rs != null) {
      try {
        rs.close();
        connection.close();
      } catch (SQLException ignored) {
      }
    }
  }

  public boolean next() {
    try {
      return rs.next();
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this);
    }
  }

  @CompilerDirectives.TruffleBoundary
  byte getByte(String idx, Node node) {
    try {
      return rs.getByte(idx);
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  short getShort(String idx, Node node) {
    try {
      return rs.getShort(idx);
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  int getInt(String idx, Node node) {
    try {
      return rs.getInt(idx);
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  long getLong(String idx, Node node) {
    try {
      return rs.getLong(idx);
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  float getFloat(String idx, Node node) {
    try {
      return rs.getFloat(idx);
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  double getDouble(String idx, Node node) {
    try {
      return rs.getDouble(idx);
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  BigDecimal getDecimal(String idx, Node node) {
    try {
      return rs.getBigDecimal(idx);
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  String getString(String idx, Node node) {
    try {
      return rs.getString(idx);
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  boolean getBool(String idx, Node node) {
    try {
      return rs.getBoolean(idx);
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  DateObject getDate(String idx, Node node) {
    try {
      java.sql.Date sqlDate = rs.getDate(idx);
      return new DateObject(sqlDate.toLocalDate());
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  TimeObject getTime(String idx, Node node) {
    try {
      java.sql.Time sqlTime = rs.getTime(idx);
      return new TimeObject(sqlTime.toLocalTime());
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  TimestampObject getTimestamp(String idx, Node node) {
    try {
      java.sql.Timestamp sqlTimestamp = rs.getTimestamp(idx);
      return new TimestampObject(sqlTimestamp.toLocalDateTime());
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  byte[] getBytes(String idx, Node node) {
    try {
      return rs.getBytes(idx);
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  boolean isNull(String idx, Node node) {
    try {
      rs.getObject(idx);
      return rs.wasNull();
    } catch (SQLException e) {
      throw exceptionHandler.rewrite(e, this, node);
    }
  }

  public String location() {
    return url;
  }
}
