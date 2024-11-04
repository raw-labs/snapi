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
import com.rawlabs.protocol.compiler.AwsConfig;
import com.rawlabs.protocol.compiler.LocationConfig;
import com.rawlabs.snapi.truffle.SnapiContext;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.primitives.*;
import com.rawlabs.utils.sources.filesystem.s3.S3Path;
import scala.None$;
import scala.Option;
import scala.Some;

@NodeInfo(shortName = "Location.FromS3")
public class LocationFromS3Node extends ExpressionNode {

  @Child private ExpressionNode bucket;
  @Child private ExpressionNode path;
  @Child private ExpressionNode awsCredential;
  @Child private ExpressionNode accessKey;
  @Child private ExpressionNode secretKey;
  @Child private ExpressionNode region;

  public LocationFromS3Node(
      ExpressionNode bucket,
      ExpressionNode path,
      ExpressionNode awsCredential,
      ExpressionNode accessKey,
      ExpressionNode secretKey,
      ExpressionNode region) {
    this.bucket = bucket;
    this.path = path;
    this.awsCredential = awsCredential;
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    String bucket = (String) this.bucket.executeGeneric(frame);
    String path = (String) this.path.executeGeneric(frame);

    SnapiContext context = SnapiContext.get(this);

    Option<String> maybeAccessKey = None$.empty();
    Option<String> maybeSecretKey = None$.empty();
    Option<String> maybeRegion = None$.empty();
    if (this.awsCredential != null) {
      String credName = (String) this.awsCredential.executeGeneric(frame);
      // getLocationConfig throws if the credential isn't found.
      LocationConfig locationConfig = context.getLocationConfig(credName);
      if (!locationConfig.hasAws()) {
        throw new TruffleRuntimeException("credential is not an AWS credential");
      }
      AwsConfig awsConfig = locationConfig.getAws();
      maybeAccessKey = new Some(awsConfig.getAccessKey());
      maybeSecretKey = new Some(awsConfig.getSecretKey());
    }

    if (this.accessKey != null) {
      maybeAccessKey = new Some(this.accessKey.executeGeneric(frame));
    }
    if (this.secretKey != null) {
      maybeSecretKey = new Some(this.secretKey.executeGeneric(frame));
    }
    if (this.region != null) {
      maybeRegion = new Some(this.region.executeGeneric(frame));
    }

    S3Path location =
        new S3Path(
            bucket, maybeRegion, maybeAccessKey, maybeSecretKey, path, context.getSettings());

    StringBuilder url = new StringBuilder();
    url.append("s3://");
    url.append(bucket);
    if (!path.startsWith("/")) url.append("/");
    url.append(path);

    return new LocationObject(location, url.toString());
  }
}
