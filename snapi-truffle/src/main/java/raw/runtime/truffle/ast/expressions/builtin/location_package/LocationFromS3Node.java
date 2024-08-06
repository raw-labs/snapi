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

package raw.runtime.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.client.api.S3Credential;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.primitives.*;
import raw.sources.filesystem.s3.S3Path;
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
    RawContext context = RawContext.get(this);
    S3Path location;

    if (RawContext.get(this).existsS3Credential(bucket)) {
      S3Credential cred = context.getS3Credential(bucket);
      location =
          new S3Path(
              bucket,
              cred.region(),
              cred.accessKey(),
              cred.secretKey(),
              path,
              context.getSettings());
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

    String url = "s3://" + bucket + path;
    return new LocationObject(location, url);
  }
}
