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

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import raw.compiler.rql2.api.LocationDescription$;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.list.StringList;
import raw.runtime.truffle.runtime.primitives.ErrorObject;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.runtime.primitives.NullObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;
import raw.runtime.truffle.runtime.record.RecordNodes;
import com.rawlabs.utils.sources.filesystem.api.*;
import com.rawlabs.utils.core.RawException;
import scala.Tuple2;
import scala.collection.IndexedSeq;

@NodeInfo(shortName = "String.Read")
@NodeChild("location")
public abstract class LocationLlNode extends ExpressionNode {
  @Specialization
  @TruffleBoundary
  protected Object doLl(
      LocationObject locationObject, @Cached(inline = true) RecordNodes.AddPropNode addPropNode) {
    try {
      FileSystemLocation fs = locationObject.getFileSystemLocation();
      IndexedSeq<Tuple2<FileSystemLocation, FileSystemMetadata>> values =
          fs.lsWithMetadata().toIndexedSeq();
      int size = values.size();
      Object[] result = new Object[size];

      for (int i = 0; i < size; i++) {
        Object topRecord = RawLanguage.get(this).createPureRecord();
        Object metadata = RawLanguage.get(this).createPureRecord();
        addPropNode.execute(
            this,
            topRecord,
            "url",
            LocationDescription$.MODULE$.locationToPublicUrl(values.apply(i)._1),
            false);
        if (values.apply(i)._2 instanceof DirectoryMetadata) {
          DirectoryMetadata directoryMetadata = (DirectoryMetadata) values.apply(i)._2;
          if (directoryMetadata.modifiedInstant().isDefined()) {

            addPropNode.execute(
                this,
                metadata,
                "modified",
                new TimestampObject(
                    LocalDateTime.ofInstant(
                        directoryMetadata.modifiedInstant().get(), ZoneOffset.UTC)),
                false);
          } else {
            addPropNode.execute(this, metadata, "modified", NullObject.INSTANCE, false);
          }
          addPropNode.execute(this, metadata, "size", NullObject.INSTANCE, false);
          addPropNode.execute(this, metadata, "blocks", new ObjectList(new Object[0]), false);
        } else {
          FileMetadata fileMetadata = (FileMetadata) values.apply(i)._2;
          if (fileMetadata.modifiedInstant().isDefined()) {
            addPropNode.execute(
                this,
                metadata,
                "modified",
                new TimestampObject(
                    LocalDateTime.ofInstant(fileMetadata.modifiedInstant().get(), ZoneOffset.UTC)),
                false);
          } else {
            addPropNode.execute(this, metadata, "modified", NullObject.INSTANCE, false);
          }
          if (fileMetadata.size().isDefined()) {
            addPropNode.execute(this, metadata, "size", fileMetadata.size().get(), false);
          } else {
            addPropNode.execute(this, metadata, "size", NullObject.INSTANCE, false);
          }
          int blocksSize = fileMetadata.blocks().length;
          Object[] blocks = new Object[blocksSize];
          for (int j = 0; j < blocksSize; j++) {
            Object block = RawLanguage.get(this).createPureRecord();

            addPropNode.execute(
                this, block, "hosts", new StringList(fileMetadata.blocks()[j].hosts()), false);
            addPropNode.execute(this, block, "offset", fileMetadata.blocks()[j].offset(), false);
            addPropNode.execute(this, block, "length", fileMetadata.blocks()[j].length(), false);
            blocks[j] = block;
          }

          ObjectList blockList = new ObjectList(blocks);
          addPropNode.execute(this, metadata, "blocks", blockList, false);
        }
        addPropNode.execute(this, topRecord, "metadata", metadata, false);
        result[i] = topRecord;
      }

      return new ObjectList(result);
    } catch (RawException e) {
      return new ErrorObject(e.getMessage());
    }
  }
}
