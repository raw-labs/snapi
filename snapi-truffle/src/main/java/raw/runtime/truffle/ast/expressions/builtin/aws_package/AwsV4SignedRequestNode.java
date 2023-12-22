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

package raw.runtime.truffle.ast.expressions.builtin.aws_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
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
import raw.client.api.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.list.ListNodes;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import scala.Tuple2;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.Map;
import scala.collection.immutable.VectorBuilder;

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
      throw new RawTruffleInternalErrorException(e);
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
      throw new RawTruffleInternalErrorException(e);
    }
  }

  @TruffleBoundary
  @Specialization(limit = "2")
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
      @Cached ListNodes.SortNode sortNode,
      @Cached ListNodes.SizeNode sizeNode,
      @Cached ListNodes.GetNode getNode,
      @CachedLibrary(limit = "2") InteropLibrary records) {
    try {
      Instant t = Instant.now();
      String amzdate = formatterWithTimeZone().format(t);
      String datestamp = getDateFormatter().format(t);

      // Task 1: create canonical request with all request settings: method, canonicalUri,
      // canonicalQueryString etc.
      VectorBuilder<Tuple2<String, String>> urlParamsVec = new VectorBuilder<>();
      StringBuilder canonicalQueryBuilder = new StringBuilder();

      Object urlParamsSorted = sortNode.execute(urlParams);

      for (int i = 0; i < sizeNode.execute(urlParamsSorted); i++) {
        canonicalQueryBuilder
            .append(
                URLEncoder.encode(
                    (String) records.readMember(getNode.execute(urlParamsSorted, i), "_1"),
                    StandardCharsets.UTF_8))
            .append("=")
            .append(
                URLEncoder.encode(
                    (String) records.readMember(getNode.execute(urlParamsSorted, i), "_2"),
                    StandardCharsets.UTF_8))
            .append("&");
        urlParamsVec.$plus$eq(
            new Tuple2<>(
                (String) records.readMember(getNode.execute(urlParamsSorted, i), "_1"),
                (String) records.readMember(getNode.execute(urlParamsSorted, i), "_2")));
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
      VectorBuilder<Tuple2<String, String>> headersParamsVec = new VectorBuilder<>();

      int headersSize = (int) sizeNode.execute(headers);
      // Adding space for host and "x-amz-date", "host" and "x-amz-security-token" if it is
      // defined
      int allHeadersSize = headersSize + 2;
      if (!sessionToken.isEmpty()) allHeadersSize++;

      Object[] allHeaders = new Object[allHeadersSize];

      for (int i = 0; i < allHeaders.length; i++) {
        allHeaders[i] = getNode.execute(headers, i);
      }

      allHeaders[headersSize] = RawLanguage.get(this).createRecord();
      records.writeMember(allHeaders[headersSize], "_1", "host");
      records.writeMember(allHeaders[headersSize], "_2", host);

      allHeaders[headersSize + 1] = RawLanguage.get(this).createRecord();
      records.writeMember(allHeaders[headersSize + 1], "_1", "x-amz-date");
      records.writeMember(allHeaders[headersSize + 1], "_2", amzdate);

      if (!sessionToken.isEmpty()) {
        allHeaders[headersSize + 2] = RawLanguage.get(this).createRecord();
        records.writeMember(allHeaders[headersSize + 2], "_1", "x-amz-security-token");
        records.writeMember(allHeaders[headersSize + 2], "_2", sessionToken);
      }

      Object sortedHeaders = sortNode.execute(new ObjectList(allHeaders));

      for (int i = 0; i < sizeNode.execute(sortedHeaders); i++) {
        canonicalHeadersBuilder
            .append(
                ((String) records.readMember(getNode.execute(sortedHeaders, i), "_1"))
                    .toLowerCase())
            .append(":")
            .append((String) records.readMember(getNode.execute(sortedHeaders, i), "_2"))
            .append("\n");
        signedHeadersBuilder
            .append(
                ((String) records.readMember(getNode.execute(sortedHeaders, i), "_1"))
                    .toLowerCase())
            .append(";");
      }

      for (int i = 0; i < sizeNode.execute(headers); i++) {
        headersParamsVec.$plus$eq(
            new Tuple2<>(
                ((String) records.readMember(getNode.execute(headers, i), "_1")).toLowerCase(),
                (String) records.readMember(getNode.execute(headers, i), "_2")));
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

      VectorBuilder<Tuple2<String, String>> newHeaders = new VectorBuilder<>();
      newHeaders.$plus$eq(new Tuple2<>("x-amz-date", amzdate));
      newHeaders.$plus$eq(new Tuple2<>("Authorization", authorizationHeader));
      if (!sessionToken.isEmpty()) {
        newHeaders.$plus$eq(new Tuple2<>("x-amz-security-token", sessionToken));
      }

      VectorBuilder<Tuple2<String, String>> requestHeaders =
          newHeaders.$plus$plus$eq(headersParamsVec.result());

      // host is added automatically
      Map<LocationSettingKey, LocationSettingValue> map = new HashMap<>();
      map =
          map.$plus(
              Tuple2.apply(
                  new LocationSettingKey("http-method"), new LocationStringSetting(method)));
      map =
          map.$plus(
              Tuple2.apply(
                  new LocationSettingKey("http-args"),
                  new LocationKVSetting(urlParamsVec.result())));
      map =
          map.$plus(
              Tuple2.apply(
                  new LocationSettingKey("http-headers"),
                  new LocationKVSetting(requestHeaders.result())));

      if (!bodyString.isEmpty()) {
        map =
            map.$plus(
                Tuple2.apply(
                    new LocationSettingKey("http-body-string"),
                    new LocationStringSetting(bodyString)));
      }

      String url = "https://" + host + "/" + path.replaceAll("^/+", "");

      return new LocationObject(url, map);
    } catch (UnsupportedMessageException
        | UnknownIdentifierException
        | UnsupportedTypeException e) {
      throw new RawTruffleInternalErrorException(e);
    }
  }
}
