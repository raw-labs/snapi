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

package com.rawlabs.snapi.truffle.ast.io.jdbc;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.snapi.truffle.runtime.exceptions.rdbms.JdbcExceptionHandler;
import com.rawlabs.snapi.truffle.runtime.exceptions.rdbms.JdbcReaderTruffleException;
import com.rawlabs.snapi.truffle.runtime.primitives.*;
import com.rawlabs.utils.core.RawException;
import com.rawlabs.utils.core.RawSettings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcQuery {

  private final String publicDescription;
  private final Connection connection;
  private final ResultSet rs;
  private final JdbcExceptionHandler exceptionHandler;

  @TruffleBoundary
  public JdbcQuery(
      LocationObject locationObject,
      String query,
      RawSettings rawSettings,
      JdbcExceptionHandler exceptionHandler) {
    this.publicDescription = locationObject.getPublicDescription();
    this.exceptionHandler = exceptionHandler;
    try {
      PreparedStatement stmt;
      int fetchSize = rawSettings.getInt("raw.runtime.rdbms.fetch-size");
      // FIXME (msb): What is crashes during construction? Must release connection.
      connection = locationObject.getJdbcServerLocation().getJdbcConnection();
      try {
        stmt = connection.prepareStatement(query);
        stmt.setFetchSize(fetchSize);
        rs = stmt.executeQuery();
      } catch (SQLException e) {
        throw exceptionHandler.rewrite(e, this);
      }
    } catch (RawException e) {
      // Exceptions due to location errors (e.g. connection failures) are turned into runtime
      // exceptions.
      throw new JdbcReaderTruffleException(e.getMessage(), this, e, null);
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
  short getShort(String colName, Node node) {
    try {
      return rs.getShort(colName);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
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
  long getLong(String colName, Node node) {
    try {
      return rs.getLong(colName);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
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
  double getDouble(String colName, Node node) {
    try {
      return rs.getDouble(colName);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
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
  String getString(String colName, Node node) {
    try {
      return rs.getString(colName);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
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
  DateObject getDate(String colName, Node node) {
    try {
      java.sql.Date sqlDate = rs.getDate(colName);
      return new DateObject(sqlDate.toLocalDate());
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
    }
  }

  @TruffleBoundary
  TimeObject getTime(String colName, Node node) {
    try {
      // Extract the SQL time (a JDBC object) from the result set.
      java.sql.Time sqlTime = rs.getTime(colName);
      // Turn it into LocalTime. It does something proper with potential timezone conversion, but
      // doesn't have the milliseconds (toLocalTime's doc says it sets the LocalTime nanoseconds
      // field to zero).
      java.time.LocalTime withoutMilliseconds = sqlTime.toLocalTime();
      // Get the value as milliseconds (possibly shifted by a certain timezone) but we have the
      // milliseconds.
      long asMillis = sqlTime.getTime();
      // Extract the actual milliseconds.
      long millis = asMillis % 1000;
      // Fix the LocalTime milliseconds.
      LocalTime localTime = withoutMilliseconds.with(ChronoField.MILLI_OF_SECOND, millis);
      return new TimeObject(localTime);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
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
  byte[] getBytes(String colName, Node node) {
    try {
      return rs.getBytes(colName);
    } catch (SQLException e) {
      throw exceptionHandler.columnParseError(e, colName, node);
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

  public String getPublicDescription() {
    return publicDescription;
  }
}
