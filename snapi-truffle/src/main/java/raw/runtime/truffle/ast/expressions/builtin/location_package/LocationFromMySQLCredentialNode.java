package raw.runtime.truffle.ast.expressions.builtin.location_package;

package raw.runtime.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.api.LocationDescription$;
import raw.compiler.rql2.api.MySqlServerLocationDescription;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.primitives.*;
import raw.sources.api.Location;
import raw.sources.jdbc.api.JdbcServerLocation;
import raw.sources.jdbc.mysql.MySqlServerLocation;

@NodeInfo(shortName = "Location.FromMySQLCredential")
public class LocationFromMySQLCredentialNode extends ExpressionNode {

    @Child private ExpressionNode credentialName;

    public LocationFromMySQLCredentialNode(ExpressionNode credentialName) {
        this.credentialName = credentialName;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        RawContext context = RawContext.get(this);

        String credentialName = (String) this.credentialName.executeGeneric(frame);
        Location l = (Location) context.getProgramEnvironment().locations().get(credentialName).get();
        MySqlServerLocationDescription d = (MySqlServerLocationDescription) LocationDescription$.MODULE$.toLocationDescription(l);

        JdbcServerLocation location = new MySqlServerLocation(d.host(), d.port(), d.dbName(), d.username(), d.password(), context.getSettings());

        return new LocationObject(location);
    }

}
