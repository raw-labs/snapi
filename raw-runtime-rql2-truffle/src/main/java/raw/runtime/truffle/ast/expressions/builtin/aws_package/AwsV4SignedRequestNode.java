package raw.runtime.truffle.ast.expressions.builtin.aws_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.list.ListLibrary;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.sources.LocationKVSetting;
import raw.sources.LocationSettingKey;
import raw.sources.LocationSettingValue;
import raw.sources.LocationStringSetting;
import scala.Tuple2;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.Map;
import scala.collection.immutable.VectorBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@NodeInfo(shortName = "Aws.V4SignedRequest")
@NodeChild("key")
@NodeChild("secretKey")
@NodeChild("service")
@NodeChild("region")
@NodeChild("path")
@NodeChild("method")
@NodeChild("host")
@NodeChild("bodyString")
@NodeChild("urlParams")
@NodeChild("headers")
public abstract class AwsV4SignedRequestNode extends ExpressionNode {

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

    private byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) {
        byte[] kSecret = ("AWS4" + key).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = hmacSHA256(dateStamp, kSecret);
        byte[] kRegion = hmacSHA256(regionName, kDate);
        byte[] kService = hmacSHA256(serviceName, kRegion);
        return hmacSHA256("aws4_request", kService);
    }

    private String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // Amazon needs timestamps for signing requests with specific formats.
    private DateTimeFormatter formatterWithTimeZone() {
        return DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX").withZone(ZoneId.from(ZoneOffset.UTC));
    }

    private DateTimeFormatter getDateFormatter() {
        return DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.from(ZoneOffset.UTC));
    }

    private MessageDigest getSha256Digest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RawTruffleInternalErrorException(e);
        }
    }

    @CompilerDirectives.TruffleBoundary
    @Specialization(limit = "2")
    protected LocationObject doRequest(
            String key,
            String secretKey,
            String service,
            String region,
            String path,
            String method,
            String host,
            String bodyString,
            Object urlParams,
            Object headers,
            @CachedLibrary("urlParams") ListLibrary urlParamsLists,
            @CachedLibrary("headers") ListLibrary headersLists,
            @CachedLibrary(limit = "2") InteropLibrary records
    ) {
        try {
            Instant t = Instant.now();
            String amzdate = formatterWithTimeZone().format(t);
            String datestamp = getDateFormatter().format(t);

            // Task 1: create canonical request with all request settings: method, canonicalUri, canonicalQueryString etc.

            VectorBuilder<Tuple2<String, String>> urlParamsVec = new VectorBuilder<>();

            StringBuilder canonicalQueryBuilder = new StringBuilder();
            for (int i = 0; i < urlParamsLists.size(urlParams); i++) {
                canonicalQueryBuilder
                        .append(URLEncoder.encode((String) records.readMember(urlParamsLists.get(urlParams, i), "_1"), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode((String) records.readMember(urlParamsLists.get(urlParams, i), "_2"), StandardCharsets.UTF_8))
                        .append("&");
                urlParamsVec.$plus$eq(
                        new Tuple2<>((String) records.readMember(urlParamsLists.get(urlParams, i), "_1"),
                                (String) records.readMember(urlParamsLists.get(urlParams, i), "_2"))
                );
            }
            // remove last '&'
            if (canonicalQueryBuilder.length() > 0) {
                canonicalQueryBuilder.deleteCharAt(canonicalQueryBuilder.length() - 1);
            }

            String canonicalQueryString = canonicalQueryBuilder.toString();

            // Create the canonical headers and signed headers.
            // Header names must be trimmed and lowercase, and sorted in code point order from
            // low to high. Note that there is a trailing \n.
            // Note: The request can include any headers; canonical_headers and signed_headers lists
            // those that you want to be included in the hash of the request. "Host" and "x-amz-date" are always required.
            StringBuilder canonicalHeadersBuilder = new StringBuilder();
            StringBuilder signedHeadersBuilder = new StringBuilder();
            VectorBuilder<Tuple2<String, String>> headersParamsVec = new VectorBuilder<>();
            for (int i = 0; i < headersLists.size(headers); i++) {
                canonicalHeadersBuilder
                        .append((String) records.readMember(urlParamsLists.get(headers, i), "_1"))
                        .append(":")
                        .append((String) records.readMember(urlParamsLists.get(headers, i), "_2"))
                        .append("\n");
                signedHeadersBuilder.append((String) records.readMember(urlParamsLists.get(headers, i), "_1")).append(";");

                headersParamsVec.$plus$eq(
                        new Tuple2<>((String) records.readMember(urlParamsLists.get(headers, i), "_1"),
                                (String) records.readMember(urlParamsLists.get(headers, i), "_2"))
                );
            }
            canonicalHeadersBuilder.append("host").append(":").append(host).append("\n");
            canonicalHeadersBuilder.append("x-amz-date").append(":").append(amzdate).append("\n");
            signedHeadersBuilder.append("host").append(";").append("x-amz-date");

            String canonicalHeaders = canonicalHeadersBuilder.toString();

            // List of signed headers: lists the headers in the canonical_headers list, delimited with ";".
            String signedHeaders = signedHeadersBuilder.toString();

            String payloadHash = toHexString(getSha256Digest().digest(bodyString.getBytes(StandardCharsets.UTF_8)));

            String canonicalRequest = method + "\n" +
                    path + "\n" +
                    canonicalQueryString + "\n" +
                    canonicalHeaders + "\n" +
                    signedHeaders + "\n" +
                    payloadHash;

            // Task 2: create string to sign
            // Match the algorithm to the hashing algorithm you use, either SHA-1 or SHA-256 (recommended).

            String algorithm = "AWS4-HMAC-SHA256";
            String credentialScope = datestamp + "/" + region + "/" + service + "/" + "aws4_request";

            String stringToSign = algorithm + "\n" +
                    amzdate + "\n" +
                    credentialScope + "\n" +
                    toHexString(getSha256Digest().digest(canonicalRequest.getBytes(StandardCharsets.UTF_8)));

            // Task 3: calculate the signature using amazon java example function.
            byte[] signingKey = getSignatureKey(secretKey, datestamp, region, service);
            String signature = toHexString(hmacSHA256(stringToSign, signingKey));

            // Task 4: Finally create request using signing information.
            String authorizationHeader = algorithm + " " +
                    "Credential=" + key + "/" + credentialScope + ", " +
                    "SignedHeaders=" + signedHeaders + ", " +
                    "Signature=" + signature;

            headersParamsVec.$plus$eq(new Tuple2<>("x-amz-date", amzdate));
            headersParamsVec.$plus$eq(new Tuple2<>("Authorization", authorizationHeader));

            // host is added automatically
            Map<LocationSettingKey, LocationSettingValue> map = new HashMap<>();
            map.$plus(Tuple2.apply(new LocationSettingKey("http-method"), new LocationStringSetting(method)));
            map.$plus(Tuple2.apply(new LocationSettingKey("http-args"), new LocationKVSetting(urlParamsVec.result())));
            map.$plus(Tuple2.apply(new LocationSettingKey("http-headers"), new LocationKVSetting(headersParamsVec.result())));

            if (!bodyString.isEmpty()) {
                map.$plus(Tuple2.apply(new LocationSettingKey("http-body-string"), new LocationStringSetting(bodyString)));
            }

            String url = "https://" + host + "/" + path.replaceAll("^/+", "");

            return new LocationObject(url, map);
        } catch (UnsupportedMessageException | UnknownIdentifierException e) {
            throw new RawTruffleInternalErrorException(e);
        }

    }
}
