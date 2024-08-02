package raw.runtime.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.api.LocationDescription$;
import raw.compiler.rql2.api.OracleServerLocationDescription;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.primitives.*;
        import raw.sources.api.Location;
import raw.sources.jdbc.api.JdbcServerLocation;
import raw.sources.jdbc.oracle.OracleServerLocation;

@NodeInfo(shortName = "Location.FromOracleCredential")
public class LocationFromOracleCredentialNode extends ExpressionNode {

    @Child private ExpressionNode credentialName;

    public LocationFromOracleCredentialNode(ExpressionNode credentialName) {
        this.credentialName = credentialName;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        RawContext context = RawContext.get(this);

        String credentialName = (String) this.credentialName.executeGeneric(frame);
        Location l = (Location) context.getProgramEnvironment().locations().get(credentialName).get();
        OracleServerLocationDescription d = (OracleServerLocationDescription) LocationDescription$.MODULE$.toLocationDescription(l);

        JdbcServerLocation location = new OracleServerLocation(d.host(), d.port(), d.dbName(), d.username(), d.password(), context.getSettings());

        return new LocationObject(location);
    }

}
