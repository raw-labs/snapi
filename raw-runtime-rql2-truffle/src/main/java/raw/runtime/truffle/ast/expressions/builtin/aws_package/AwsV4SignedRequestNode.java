package raw.runtime.truffle.ast.expressions.builtin.aws_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.primitives.LocationObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@NodeInfo(shortName = "Aws.V4SignedRequest")
@NodeChild("key")
@NodeChild("secretKey")
@NodeChild("service")
@NodeChild("region")
@NodeChild("path")
@NodeChild("method")
@NodeChild("host")
@NodeChild("bodyString")
@NodeChild("args")
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
        for(byte b: bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @Specialization
    protected LocationObject doRequest(
            String key,
            String secretKey,
            String service,
            String region,
            String path,
            String method,
            String host,
            String bodyString,
            Object args,
            Object headers
    ) {

        return new LocationObject("TODO");
    }
}
