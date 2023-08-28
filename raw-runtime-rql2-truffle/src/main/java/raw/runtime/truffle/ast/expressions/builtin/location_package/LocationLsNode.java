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

package raw.runtime.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.api.RawException;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.list.StringList;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;
import raw.sources.Location;
import raw.sources.filesystem.FileSystemException;
import raw.sources.filesystem.FileSystemLocation;
import raw.sources.filesystem.FileSystemLocationProvider;
import scala.collection.IndexedSeq;

@NodeInfo(shortName = "String.Read")
@NodeChild("location")
public abstract class LocationLsNode extends ExpressionNode {
    @Specialization
    protected Object doLs(LocationObject locationObject) {
        try {
            RuntimeContext context = RawContext.get(this).getRuntimeContext();
            FileSystemLocation fs =
                    FileSystemLocationProvider.build(
                            locationObject.getLocationDescription(), context.sourceContext());
            IndexedSeq<String> values = fs.ls().map(Location::rawUri).toIndexedSeq();
            int size = values.size();
            String[] result = new String[size];

            for (int i = 0; i < size; i++) {
                result[i] = values.apply(i);
            }

            return ObjectTryable.BuildSuccess(new StringList(result));
        } catch (RawException e) {
            return ObjectTryable.BuildFailure(e.getMessage());
        }
    }
}
