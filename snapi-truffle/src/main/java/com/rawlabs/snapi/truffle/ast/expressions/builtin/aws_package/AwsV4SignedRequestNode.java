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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.aws_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.Rql2Context;
import com.rawlabs.snapi.truffle.Rql2Language;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleInternalErrorException;
import com.rawlabs.snapi.truffle.runtime.list.ListNodes;
import com.rawlabs.snapi.truffle.runtime.list.ObjectList;
import com.rawlabs.snapi.truffle.runtime.primitives.LocationObject;
import com.rawlabs.snapi.truffle.runtime.record.RecordNodes;
import com.rawlabs.utils.core.RawSettings;
import com.rawlabs.utils.sources.bytestream.http.HttpByteStreamLocation;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import scala.None$;
import scala.Option;
import scala.Some;
import scala.Tuple2;
import scala.collection.mutable.ArrayBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

@NodeInfo(shortName = "Aws.V4SignedRequest")
@NodeChild("key")
@NodeChild("secretKey")
@NodeChild("service")
@NodeChild("region")
@NodeChild("sessionToken")
@NodeChild("path")
@NodeChild("method")
@NodeChild("host")
@NodeChild("bodyString")
@NodeChild("urlParams")
@NodeChild("headers")
public abstract class AwsV4SignedRequestNode extends ExpressionNode {

  @TruffleBoundary
  private byte[] hmacSHA256(String data, byte[] key) {
    try {
      String algorithm = "HmacSHA256";
      Mac mac = Mac.getInstance(algorithm);
      mac.init(new SecretKeySpec(key, algorithm));
      return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new TruffleInternalErrorException(e);
    }
  }

  private byte[] getSignatureKey(
      String key, String dateStamp, String regionName, String serviceName) {
    byte[] kSecret = ("AWS4" + key).getBytes(StandardCharsets.UTF_8);
    byte[] kDate = hmacSHA256(dateStamp, kSecret);
    byte[] kRegion = hmacSHA256(regionName, kDate);
    byte[] kService = hmacSHA256(serviceName, kRegion);
    return hmacSHA256("aws4_request", kService);
  }

  @TruffleBoundary
  private String toHexString(byte[] bytes) {
    StringBuilder hexString = new StringBuilder();
    String hex;
    for (byte aByte : bytes) {
      hex = Integer.toHexString(0xff & aByte);
      if (hex.length() == 1) hexString.append("0");
      hexString.append(hex);
    }
    return hexString.toString();
  }

  // Amazon needs timestamps for signing requests with specific formats.
  @TruffleBoundary
  private DateTimeFormatter formatterWithTimeZone() {
    return DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX").withZone(ZoneId.from(ZoneOffset.UTC));
  }

  @TruffleBoundary
  private DateTimeFormatter getDateFormatter() {
    return DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.from(ZoneOffset.UTC));
  }

  @TruffleBoundary
  private MessageDigest getSha256Digest() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new TruffleInternalErrorException(e);
    }
  }

  @TruffleBoundary
  @Specialization
  protected LocationObject doRequest(
      String key,
      String secretKey,
      String service,
      String region,
      String sessionToken,
      String path,
      String method,
      String host,
      String bodyString,
      Object urlParams,
      Object headers,
      @Cached(inline = true) ListNodes.SortNode sortNode,
      @Cached(inline = true) ListNodes.SizeNode sizeNode,
      @Cached(inline = true) ListNodes.GetNode getNode,
      @Cached(inline = true) RecordNodes.AddPropNode addPropNode,
      @Cached(inline = true) RecordNodes.GetValueNode getValueNode) {

    Instant t = Instant.now();
    String amzdate = formatterWithTimeZone().format(t);
    String datestamp = getDateFormatter().format(t);

    ClassTag<Tuple2<String, String>> tupleClassTag =
        (ClassTag<Tuple2<String, String>>) (ClassTag<?>) ClassTag$.MODULE$.apply(Tuple2.class);

    // Task 1: create canonical request with all request settings: method, canonicalUri,
    // canonicalQueryString etc.
    ArrayBuilder<Tuple2<String, String>> argsBuilder = new ArrayBuilder.ofRef(tupleClassTag);
    StringBuilder canonicalQueryBuilder = new StringBuilder();

    Object urlParamsSorted = sortNode.execute(this, urlParams);

    for (int i = 0; i < sizeNode.execute(this, urlParamsSorted); i++) {
      canonicalQueryBuilder
          .append(
              URLEncoder.encode(
                  (String)
                      getValueNode.execute(this, getNode.execute(this, urlParamsSorted, i), "_1"),
                  StandardCharsets.UTF_8))
          .append("=")
          .append(
              URLEncoder.encode(
                  (String)
                      getValueNode.execute(this, getNode.execute(this, urlParamsSorted, i), "_2"),
                  StandardCharsets.UTF_8))
          .append("&");
      argsBuilder.$plus$eq(
          new Tuple2<>(
              (String) getValueNode.execute(this, getNode.execute(this, urlParamsSorted, i), "_1"),
              (String)
                  getValueNode.execute(this, getNode.execute(this, urlParamsSorted, i), "_2")));
    }
    // remove last '&'
    if (!canonicalQueryBuilder.isEmpty()) {
      canonicalQueryBuilder.deleteCharAt(canonicalQueryBuilder.length() - 1);
    }

    // Create the canonical headers and signed headers.
    // Header names must be trimmed and lowercase, and sorted in code point order from
    // low to high. Note that there is a trailing \n.
    // Note: The request can include any headers; canonical_headers and signed_headers lists
    // those that you want to be included in the hash of the request. "Host" and
    // "x-amz-date" are
    // always required.
    StringBuilder canonicalHeadersBuilder = new StringBuilder();
    StringBuilder signedHeadersBuilder = new StringBuilder();

    ArrayBuilder<Tuple2<String, String>> headersBuilder = new ArrayBuilder.ofRef(tupleClassTag);

    int headersSize = (int) sizeNode.execute(this, headers);
    // Adding space for host and "x-amz-date", "host" and "x-amz-security-token" if it is
    // defined
    int allHeadersSize = headersSize + 2;
    if (!sessionToken.isEmpty()) allHeadersSize++;

    Object[] allHeaders = new Object[allHeadersSize];

    for (int i = 0; i < headersSize; i++) {
      allHeaders[i] = getNode.execute(this, headers, i);
    }

    allHeaders[headersSize] = Rql2Language.get(this).createPureRecord();
    addPropNode.execute(this, allHeaders[headersSize], "_1", "host", false);
    addPropNode.execute(this, allHeaders[headersSize], "_2", host, false);

    allHeaders[headersSize + 1] = Rql2Language.get(this).createPureRecord();
    addPropNode.execute(this, allHeaders[headersSize + 1], "_1", "x-amz-date", false);
    addPropNode.execute(this, allHeaders[headersSize + 1], "_2", amzdate, false);

    if (!sessionToken.isEmpty()) {
      allHeaders[headersSize + 2] = Rql2Language.get(this).createPureRecord();
      addPropNode.execute(this, allHeaders[headersSize + 2], "_1", "x-amz-security-token", false);
      addPropNode.execute(this, allHeaders[headersSize + 2], "_2", sessionToken, false);
    }

    Object sortedHeaders = sortNode.execute(this, new ObjectList(allHeaders));

    for (int i = 0; i < sizeNode.execute(this, sortedHeaders); i++) {
      canonicalHeadersBuilder
          .append(
              getValueNode
                  .execute(this, getNode.execute(this, sortedHeaders, i), "_1")
                  .toString()
                  .toLowerCase())
          .append(":")
          .append(getValueNode.execute(this, getNode.execute(this, sortedHeaders, i), "_2"))
          .append("\n");
      signedHeadersBuilder
          .append(
              getValueNode
                  .execute(this, getNode.execute(this, sortedHeaders, i), "_1")
                  .toString()
                  .toLowerCase())
          .append(";");
    }

    for (int i = 0; i < sizeNode.execute(this, headers); i++) {
      headersBuilder.$plus$eq(
          new Tuple2<>(
              getValueNode
                  .execute(this, getNode.execute(this, headers, i), "_1")
                  .toString()
                  .toLowerCase(),
              (String) getValueNode.execute(this, getNode.execute(this, headers, i), "_2")));
    }

    if (!signedHeadersBuilder.isEmpty()) {
      signedHeadersBuilder.deleteCharAt(signedHeadersBuilder.length() - 1);
    }

    // List of signed headers: lists the headers in the canonical_headers list, delimited
    // with
    // ";".
    String signedHeaders = signedHeadersBuilder.toString();

    String payloadHash =
        toHexString(getSha256Digest().digest(bodyString.getBytes(StandardCharsets.UTF_8)));

    String canonicalRequest =
        method
            + "\n"
            + path
            + "\n"
            + canonicalQueryBuilder
            + "\n"
            + canonicalHeadersBuilder
            + "\n"
            + signedHeadersBuilder
            + "\n"
            + payloadHash;

    // Task 2: create string to sign
    // Match the algorithm to the hashing algorithm you use, either SHA-1 or SHA-256
    // (recommended).

    String algorithm = "AWS4-HMAC-SHA256";
    String credentialScope = datestamp + "/" + region + "/" + service + "/" + "aws4_request";

    String stringToSign =
        algorithm
            + "\n"
            + amzdate
            + "\n"
            + credentialScope
            + "\n"
            + toHexString(
                getSha256Digest().digest(canonicalRequest.getBytes(StandardCharsets.UTF_8)));

    // Task 3: calculate the signature using amazon java example function.
    byte[] signingKey = getSignatureKey(secretKey, datestamp, region, service);
    String signature = toHexString(hmacSHA256(stringToSign, signingKey));

    // Task 4: Finally create request using signing information.
    String authorizationHeader =
        algorithm
            + " "
            + "Credential="
            + key
            + "/"
            + credentialScope
            + ", "
            + "SignedHeaders="
            + signedHeaders
            + ", "
            + "Signature="
            + signature;

    headersBuilder.$plus$eq(new Tuple2<>("x-amz-date", amzdate));
    headersBuilder.$plus$eq(new Tuple2<>("Authorization", authorizationHeader));
    if (!sessionToken.isEmpty()) {
      headersBuilder.$plus$eq(new Tuple2<>("x-amz-security-token", sessionToken));
    }

    Option<byte[]> maybeBody;
    if (!bodyString.isEmpty()) {
      maybeBody = new Some(bodyString.getBytes());
    } else {
      maybeBody = None$.empty();
    }

    String url = "https://" + host + "/" + path.replaceAll("^/+", "");

    int[] expectedStatusArray = {
      HttpURLConnection.HTTP_OK,
      HttpURLConnection.HTTP_ACCEPTED,
      HttpURLConnection.HTTP_CREATED,
      HttpURLConnection.HTTP_PARTIAL
    };

    RawSettings rawSettings = Rql2Context.get(this).getSettings();

    HttpByteStreamLocation location =
        new HttpByteStreamLocation(
            url,
            method,
            (Tuple2<String, String>[]) argsBuilder.result(),
            (Tuple2<String, String>[]) headersBuilder.result(),
            maybeBody,
            expectedStatusArray,
            rawSettings);

    return new LocationObject(location, "aws:" + service + ":" + region + ":" + path);
  }
}
