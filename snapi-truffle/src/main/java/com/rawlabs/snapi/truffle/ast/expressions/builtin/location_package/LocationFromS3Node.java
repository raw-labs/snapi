/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.protocol.compiler.LocationConfig;
import com.rawlabs.protocol.compiler.S3Config;
import com.rawlabs.snapi.truffle.SnapiContext;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.primitives.*;
import com.rawlabs.utils.sources.filesystem.s3.S3Path;
import scala.None$;
import scala.Option;
import scala.Some;

@NodeInfo(shortName = "Location.FromS3")
public class LocationFromS3Node extends ExpressionNode {

  @Child private ExpressionNode bucket;
  @Child private ExpressionNode path;
  @Child private ExpressionNode accessKey;
  @Child private ExpressionNode secretKey;
  @Child private ExpressionNode region;

  public LocationFromS3Node(
      ExpressionNode bucket,
      ExpressionNode path,
      ExpressionNode accessKey,
      ExpressionNode secretKey,
      ExpressionNode region) {
    this.bucket = bucket;
    this.path = path;
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    String bucket = (String) this.bucket.executeGeneric(frame);
    String path = (String) this.path.executeGeneric(frame);

    // The docs say:
    // "If the S3 bucket is not registered in the credentials storage, then the region, accessKey
    // and secretKey must be provided as arguments."
    // However, if the access key/secret key are passed, they should be used.
    SnapiContext context = SnapiContext.get(this);
    S3Path location;
    if (this.accessKey == null
        && this.secretKey == null
        && context.existsLocationConfig(bucket)
        && context.getLocationConfig(bucket).hasS3()) {
      LocationConfig l = context.getLocationConfig(bucket);
      S3Config s3Config = l.getS3();
      Option<String> maybeAccessKey =
          (s3Config.hasAccessSecretKey())
              ? new Some(s3Config.getAccessSecretKey().getAccessKey())
              : None$.empty();
      Option<String> maybeSecretKey =
          (s3Config.hasAccessSecretKey())
              ? new Some(s3Config.getAccessSecretKey().getSecretKey())
              : None$.empty();
      Option<String> maybeRegion =
          (s3Config.hasRegion()) ? new Some(s3Config.getRegion()) : None$.empty();
      location =
          new S3Path(
              bucket, maybeRegion, maybeAccessKey, maybeSecretKey, path, context.getSettings());
    } else {
      // We actually do NOT throw an exception if the accessKey is not passed.
      // Instead, we go without it, which triggers anonymous access to the S3 bucket.
      Option<String> maybeAccessKey =
          (this.accessKey != null) ? new Some(this.accessKey.executeGeneric(frame)) : None$.empty();
      Option<String> maybeSecretKey =
          (this.secretKey != null) ? new Some(this.secretKey.executeGeneric(frame)) : None$.empty();
      Option<String> maybeRegion =
          (this.region != null) ? new Some(this.region.executeGeneric(frame)) : None$.empty();
      location =
          new S3Path(
              bucket, maybeRegion, maybeAccessKey, maybeSecretKey, path, context.getSettings());
    }

    StringBuilder url = new StringBuilder();
    url.append("s3://");
    url.append(bucket);
    if (!path.startsWith("/")) url.append("/");
    url.append(path);

    return new LocationObject(location, url.toString());
  }
}
