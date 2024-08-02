package raw.runtime.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.primitives.*;
import raw.sources.bytestream.http.HttpByteStreamLocation;

@NodeInfo(shortName = "Location.FromHttp")
public class LocationFromHttpNode extends ExpressionNode {

    private String method;
    @Child private ExpressionNode url;
    @Child private ExpressionNode args;
    @Child private ExpressionNode headers;
    @Child private ExpressionNode body;
    @Child private ExpressionNode expectedStatus;
    @Child private ExpressionNode username;
    @Child private ExpressionNode password;

    public LocationFromHttpNode(String method, ExpressionNode url, ExpressionNode args, ExpressionNode headers, ExpressionNode body, ExpressionNode expectedStatus, ExpressionNode username, ExpressionNode password) {
        this.method = method;
        this.url = url;
        this.args = args;
        this.headers = headers;
        this.body = body;
        this.expectedStatus = expectedStatus;
        this.username = username;
        this.password = password;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {

        handle basic auth here?

        String url = (String) this.url.executeGeneric(frame);
        String args = (String) this.args.executeGeneric(frame);
        String headers = (String) this.headers.executeGeneric(frame);
        String body = (String) this.body.executeGeneric(frame);
        int expectedStatus = (int) this.expectedStatus.executeGeneric(frame);
        String username = (String) this.username.executeGeneric(frame);
        String password = (String) this.password.executeGeneric(frame);

        HttpByteStreamLocation location = new HttpByteStreamLocation(method, url, args, headers, body, expectedStatus, username, password, RawContext.get(this).getSettings());

        return new LocationObject(location);
    }

}