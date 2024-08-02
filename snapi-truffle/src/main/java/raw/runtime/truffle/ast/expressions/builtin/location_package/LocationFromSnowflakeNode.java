package raw.runtime.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.primitives.*;
import raw.sources.jdbc.api.JdbcServerLocation;
import raw.sources.jdbc.snowflake.SnowflakeServerLocation;

@NodeInfo(shortName = "Location.FromSnowflake")
public class LocationFromSnowflakeNode extends ExpressionNode {

    @Child private ExpressionNode db;
    @Child private ExpressionNode username;
    @Child private ExpressionNode password;
    @Child private ExpressionNode accountID;
    @Child private ExpressionNode options;

    public LocationFromSnowflakeNode(ExpressionNode db, ExpressionNode username, ExpressionNode password, ExpressionNode accountID, ExpressionNode options) {
        this.db = db;
        this.username = username;
        this.password = password;
        this.accountID = accountID;
        this.options = options;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        String db = (String) this.db.executeGeneric(frame);
        String username = (String) this.username.executeGeneric(frame);
        String password = (String) this.password.executeGeneric(frame);
        String accountID = (String) this.accountID.executeGeneric(frame);
        String options = (String) this.options.executeGeneric(frame);

        JdbcServerLocation location = new SnowflakeServerLocation(db, username, password, accountID, options, RawContext.get(this).getSettings());

        return new LocationObject(location);
    }

}
