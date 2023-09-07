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
import raw.api.RawException;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.list.StringList;
import raw.runtime.truffle.runtime.option.LongOption;
import raw.runtime.truffle.runtime.option.ObjectOption;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;
import raw.sources.filesystem.*;
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
      RuntimeContext context = RawContext.get(this).getRuntimeContext();
      FileSystemLocation fs =
          FileSystemLocationProvider.build(
              locationObject.getLocationDescription(), context.sourceContext());
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
                new ObjectOption(
                    new TimestampObject(
                        LocalDateTime.ofInstant(
                            directoryMetadata.modifiedInstant().get(), ZoneOffset.UTC))));
          } else {
            records.writeMember(metadata, "modified", new ObjectOption());
          }
          records.writeMember(metadata, "size", new LongOption());
          records.writeMember(metadata, "blocks", new ObjectList(new Object[0]));
        } else {
          FileMetadata fileMetadata = (FileMetadata) values.apply(i)._2;
          if (fileMetadata.modifiedInstant().isDefined()) {
            records.writeMember(
                metadata,
                "modified",
                new ObjectOption(
                    new TimestampObject(
                        LocalDateTime.ofInstant(
                            fileMetadata.modifiedInstant().get(), ZoneOffset.UTC))));
          } else {
            records.writeMember(metadata, "modified", new ObjectOption());
          }
          if (fileMetadata.size().isDefined()) {
            records.writeMember(metadata, "size", new LongOption((long) fileMetadata.size().get()));
          } else {
            records.writeMember(metadata, "size", new LongOption());
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

      return ObjectTryable.BuildSuccess(new ObjectList(result));
    } catch (RawException e) {
      return ObjectTryable.BuildFailure(e.getMessage());
    } catch (UnsupportedMessageException
        | UnknownIdentifierException
        | UnsupportedTypeException e) {
      throw new RawTruffleInternalErrorException(e);
    }
  }
}
