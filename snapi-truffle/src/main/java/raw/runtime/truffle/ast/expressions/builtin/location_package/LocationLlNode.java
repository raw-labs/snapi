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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.list.StringList;
import raw.runtime.truffle.runtime.primitives.ErrorObject;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.runtime.primitives.NullObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.sources.api.SourceContext;
import raw.sources.filesystem.api.*;
import raw.utils.RawException;
import scala.Tuple2;
import scala.collection.IndexedSeq;

@NodeInfo(shortName = "String.Read")
@NodeChild("location")
public abstract class LocationLlNode extends ExpressionNode {
  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected Object doLl(
      LocationObject locationObject, @CachedLibrary(limit = "5") InteropLibrary records) {
    try {
      SourceContext context = RawContext.get(this).getSourceContext();
      FileSystemLocation fs =
          FileSystemLocationProvider.build(locationObject.getLocationDescription(), context);
      IndexedSeq<Tuple2<FileSystemLocation, FileSystemMetadata>> values =
          fs.lsWithMetadata().toIndexedSeq();
      int size = values.size();
      RecordObject[] result = new RecordObject[size];

      for (int i = 0; i < size; i++) {
        RecordObject topRecord = RawLanguage.get(this).createRecord();
        RecordObject metadata = RawLanguage.get(this).createRecord();
        records.writeMember(topRecord, "url", values.apply(i)._1.rawUri());
        if (values.apply(i)._2 instanceof DirectoryMetadata) {
          DirectoryMetadata directoryMetadata = (DirectoryMetadata) values.apply(i)._2;
          if (directoryMetadata.modifiedInstant().isDefined()) {
            records.writeMember(
                metadata,
                "modified",
                new TimestampObject(
                    LocalDateTime.ofInstant(
                        directoryMetadata.modifiedInstant().get(), ZoneOffset.UTC)));
          } else {
            records.writeMember(metadata, "modified", NullObject.INSTANCE);
          }
          records.writeMember(metadata, "size", NullObject.INSTANCE);
          records.writeMember(metadata, "blocks", new ObjectList(new Object[0]));
        } else {
          FileMetadata fileMetadata = (FileMetadata) values.apply(i)._2;
          if (fileMetadata.modifiedInstant().isDefined()) {
            records.writeMember(
                metadata,
                "modified",
                new TimestampObject(
                    LocalDateTime.ofInstant(fileMetadata.modifiedInstant().get(), ZoneOffset.UTC)));
          } else {
            records.writeMember(metadata, "modified", NullObject.INSTANCE);
          }
          if (fileMetadata.size().isDefined()) {
            records.writeMember(metadata, "size", fileMetadata.size().get());
          } else {
            records.writeMember(metadata, "size", NullObject.INSTANCE);
          }
          int blocksSize = fileMetadata.blocks().length;
          RecordObject[] blocks = new RecordObject[blocksSize];
          for (int j = 0; j < blocksSize; j++) {
            RecordObject block = RawLanguage.get(this).createRecord();
            records.writeMember(block, "hosts", new StringList(fileMetadata.blocks()[j].hosts()));
            records.writeMember(block, "offset", fileMetadata.blocks()[j].offset());
            records.writeMember(block, "length", fileMetadata.blocks()[j].length());
            blocks[j] = block;
          }

          ObjectList blockList = new ObjectList(blocks);
          records.writeMember(metadata, "blocks", blockList);
        }
        records.writeMember(topRecord, "metadata", metadata);
        result[i] = topRecord;
      }

      return new ObjectList(result);
    } catch (RawException e) {
      return new ErrorObject(e.getMessage());
    } catch (UnsupportedMessageException
        | UnknownIdentifierException
        | UnsupportedTypeException e) {
      throw new RawTruffleInternalErrorException(e);
    }
  }
}
