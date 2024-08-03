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

package raw.runtime.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.net.URI;
import java.net.URISyntaxException;
import raw.client.api.JdbcLocation;
import raw.client.api.PostgresJdbcLocation;
import raw.client.api.S3Credential;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.sources.api.Location;
import raw.sources.filesystem.local.LocalPath;
import raw.sources.filesystem.s3.S3Path;
import raw.sources.jdbc.pgsql.PostgresqlSchemaLocation;
import raw.sources.jdbc.pgsql.PostgresqlServerLocation;
import raw.sources.jdbc.pgsql.PostgresqlTableLocation;
import raw.utils.RawSettings;
import scala.None$;
import scala.Some;

@NodeInfo(shortName = "Location.FromString")
public class LocationFromStringNode extends ExpressionNode {

  @Child private ExpressionNode url;

  public LocationFromStringNode(ExpressionNode url) {
    this.url = url;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    String url = (String) this.url.executeGeneric(frame);

    // Check protocol in url
    int colonIndex = url.indexOf(':');
    if (colonIndex == -1) {
      throw new RawTruffleRuntimeException("missing protocol: " + url);
    }

    RawContext context = RawContext.get(this);
    RawSettings rawSettings = context.getSettings();

    Location location = null;

    String protocol = url.substring(0, colonIndex);
    switch (protocol) {
      case "pgsql":
        location = getPgsqlLocation(url, context);
        break;
      case "mysql":
        throw new RawTruffleRuntimeException("mysql location not supported");
        //        break;
      case "oracle":
        throw new RawTruffleRuntimeException("oracle location not supported");
        //        break;
      case "sqlserver":
        throw new RawTruffleRuntimeException("sqlserver location not supported");
        //        break;
      case "sqlite":
        throw new RawTruffleRuntimeException("sqlite location not supported");
        //        break;
      case "teradata":
        throw new RawTruffleRuntimeException("teradata location not supported");
        //        break;
      case "snowflake":
        throw new RawTruffleRuntimeException("snowflake location not supported");
        //        break;
      case "s3":
        location = getS3Location(url, context);
        break;
      case "http":
        throw new RawTruffleRuntimeException("http location not supported");
        //        break;
      case "https":
        throw new RawTruffleRuntimeException("https location not supported");
        //        break;
      case "github":
        throw new RawTruffleRuntimeException("github location not supported");
        //        break;
      case "file":
        location = getLocalLocation(url);
        break;
      case "local":
        location = getLocalLocation(url);
        break;
      case "mock":
        throw new RawTruffleRuntimeException("mock location not supported");
        //        break;
      default:
        throw new RawTruffleRuntimeException("unsupported protocol: " + protocol);
    }

    return new LocationObject(location);
  }

  @CompilerDirectives.TruffleBoundary
  private Location getLocalLocation(String url) {
    try {
      URI uri = new URI(url);
      String path = uri.getPath();
      return new LocalPath(path);
    } catch (URISyntaxException e) {
      throw new RawTruffleRuntimeException("invalid local URL: " + url);
    }
  }

  @CompilerDirectives.TruffleBoundary
  private Location getS3Location(String url, RawContext context) {
    try {
      URI uri = new URI(url);
      String uriUserInfo = uri.getUserInfo();
      String bucketName = uri.getHost();
      String path = uri.getPath();
      String objectKey = path.startsWith("/") ? path.substring(1) : path;

      String accessKey = null;
      String secretKey = null;
      if (uriUserInfo != null) {
        String[] userInfoParts = uriUserInfo.split(":");
        accessKey = userInfoParts[0];
        if (accessKey.isEmpty()) {
          throw new RawTruffleRuntimeException("missing S3 access key");
        }
        if (userInfoParts.length > 1) {
          secretKey = userInfoParts[1];
          if (secretKey.isEmpty()) {
            throw new RawTruffleRuntimeException("missing S3 secret key");
          }
        } else {
          throw new RawTruffleRuntimeException("missing S3 secret key");
        }
      }

      if (accessKey == null) {
        // If the access key/secret key are not defined, then the "host" is actually the bucket name
        // in the program environment credentials set.
        S3Credential s3Credential = RawContext.get(this).getS3Credential(bucketName);
        return new S3Path(
            bucketName,
            s3Credential.region(),
            s3Credential.accessKey(),
            s3Credential.secretKey(),
            objectKey,
            context.getSettings());
      } else {
        // TODO (msb): There is no way to specify the region when using a direct URL...
        return new S3Path(
            bucketName,
            None$.empty(),
            new Some(accessKey),
            new Some(secretKey),
            objectKey,
            context.getSettings());
      }
    } catch (URISyntaxException e) {
      throw new RawTruffleRuntimeException("invalid S3 URL: " + url);
    }
  }

  @CompilerDirectives.TruffleBoundary
  private Location getPgsqlLocation(String url, RawContext context) {
    try {
      URI uri = new URI(url);
      String uriUserInfo = uri.getUserInfo();
      String uriHost = uri.getHost();
      int uriPort = uri.getPort();
      String uriPath = uri.getPath();

      String host = null;
      Integer port = null;
      String username = null;
      String password = null;
      String dbname = null;
      String schema = null;
      String table = null;

      if (uriUserInfo != null) {
        String[] userInfoParts = uriUserInfo.split(":");
        username = userInfoParts[0];
        if (username.isEmpty()) {
          throw new RawTruffleRuntimeException("missing PostgreSQL username");
        }
        if (userInfoParts.length > 1) {
          password = userInfoParts[1];
          if (password.isEmpty()) {
            throw new RawTruffleRuntimeException("missing PostgreSQL password");
          }
        } else {
          throw new RawTruffleRuntimeException("missing PostgreSQL password");
        }
      }

      if (username == null) {
        // If the username/password are not defined, then the "host" is actually the database name
        // in the program environment credentials set.
        JdbcLocation jdbcLocation =
            RawContext.get(this).getProgramEnvironment().jdbcServers().get(uriHost).get();
        if (jdbcLocation instanceof PostgresJdbcLocation) {
          PostgresJdbcLocation pgsqlLocation = (PostgresJdbcLocation) jdbcLocation;
          host = pgsqlLocation.host();
          port = pgsqlLocation.port();
          username = pgsqlLocation.username();
          password = pgsqlLocation.password();
        } else {
          throw new RawTruffleRuntimeException("not a PostgreSQL credential: " + uriHost);
        }
      } else {
        // If the username/password are defined, then the "host" is the actual host.

        host = uriHost;
        port = uriPort;

        if (uriPath != null) {
          String[] pathParts = uriPath.split("/");
          dbname = pathParts.length > 1 ? pathParts[1] : null;
          schema = pathParts.length > 2 ? pathParts[2] : null;
          table = pathParts.length > 3 ? pathParts[3] : null;
        }
      }

      if (dbname != null && schema != null && table != null) {
        return new PostgresqlTableLocation(
            host, port, dbname, username, password, schema, table, context.getSettings());
      } else if (dbname != null && schema != null) {
        return new PostgresqlSchemaLocation(
            host, port, dbname, username, password, schema, context.getSettings());
      } else if (dbname != null) {
        return new PostgresqlServerLocation(host, port, dbname, username, password, context.getSettings());
      } else {
        throw new RawTruffleRuntimeException("invalid PostgreSQL URL: " + url);
      }

    } catch (URISyntaxException e) {
      throw new RawTruffleRuntimeException("invalid PostgreSQL URL: " + url);
    }
  }
}
