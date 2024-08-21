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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.rql2.api.LocationDescription$;
import com.rawlabs.utils.core.RawException;
import com.rawlabs.utils.sources.filesystem.api.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.list.ObjectList;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.list.StringList;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.ErrorObject;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.LocationObject;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.NullObject;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.TimestampObject;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.record.RecordNodes;
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
