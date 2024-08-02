package raw.runtime.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.api.LocationDescription$;
import raw.compiler.rql2.api.SnowflakeServerLocationDescription;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.primitives.*;
import raw.sources.api.Location;
import raw.sources.jdbc.api.JdbcServerLocation;
import raw.sources.jdbc.snowflake.SnowflakeServerLocation;

@NodeInfo(shortName = "Location.FromSnowflakeCredential")
public class LocationFromSnowflakeCredentialNode extends ExpressionNode {

    @Child private ExpressionNode credentialName;

    public LocationFromSnowflakeCredentialNode(ExpressionNode credentialName) {
        this.credentialName = credentialName;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        RawContext context = RawContext.get(this);

        String credentialName = (String) this.credentialName.executeGeneric(frame);
        Location l = (Location) context.getProgramEnvironment().locations().get(credentialName).get();
        SnowflakeServerLocationDescription d = (SnowflakeServerLocationDescription) LocationDescription$.MODULE$.toLocationDescription(l);

        JdbcServerLocation location = new SnowflakeServerLocation(d.dbName(), d.username(), d.password(), d.accountIdentifier(), d.parameters(), context.getSettings());

        return new LocationObject(location);
    }

}