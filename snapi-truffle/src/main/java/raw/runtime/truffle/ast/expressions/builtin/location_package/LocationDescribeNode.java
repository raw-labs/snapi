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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import raw.compiler.rql2.Rql2TypeUtils$;
import raw.compiler.rql2.source.*;
import raw.inferrer.api.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.primitives.ErrorObject;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.runtime.primitives.NullObject;
import raw.runtime.truffle.runtime.record.RecordNodes;
import raw.utils.RawException;
import scala.Some;

// A.Z Similar implementation to Scala
@NodeInfo(shortName = "String.Read")
@NodeChild("location")
@NodeChild("sampleSize")
public abstract class LocationDescribeNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected Object doDescribe(
      LocationObject locationObject,
      int sampleSize,
      @Cached(inline = true) RecordNodes.AddPropNode addPropNode) {
    InferrerService inferrer = RawContext.get(this).getInferrer();
    try {
      // In scala implementation interpreter there is a sample size argument
      InputFormatDescriptor descriptor =
          inferrer.infer(
              AutoInferrerProperties.apply(
                  locationObject.getLocationDescription(),
                  sampleSize == Integer.MAX_VALUE ? Some.empty() : Some.apply(sampleSize)));

      String format = "";
      String comment = "";
      SourceType tipe = SourceAnyType.apply();
      Map<String, String> properties = new HashMap<>();
      boolean sampled = false;

      if (descriptor instanceof ExcelInputFormatDescriptor) {
        ExcelInputFormatDescriptor excelDescriptor = (ExcelInputFormatDescriptor) descriptor;
        format = "excel";
        comment = "";
        tipe = excelDescriptor.tipe();
        properties.put("sheet", excelDescriptor.sheet());
        properties.put("x0", String.valueOf(excelDescriptor.x0()));
        properties.put("y0", String.valueOf(excelDescriptor.y0()));
        properties.put("x1", String.valueOf(excelDescriptor.x1()));
        properties.put("y1", String.valueOf(excelDescriptor.y1()));
      } else if (descriptor instanceof SqlTableInputFormatDescriptor) {
        SqlTableInputFormatDescriptor sqlTableDescriptor =
            (SqlTableInputFormatDescriptor) descriptor;
        format = "relational table";
        tipe = sqlTableDescriptor.tipe();
      } else if (descriptor instanceof SqlQueryInputFormatDescriptor) {
        SqlQueryInputFormatDescriptor sqlQueryDescriptor =
            (SqlQueryInputFormatDescriptor) descriptor;
        format = "relational query";
        tipe = sqlQueryDescriptor.tipe();
      } else if (descriptor instanceof TextInputStreamFormatDescriptor) {
        TextInputStreamFormatDescriptor textInputQueryDescriptor =
            (TextInputStreamFormatDescriptor) descriptor;
        comment =
            String.format(
                    "encoding %s (confidence: %s",
                    textInputQueryDescriptor.encoding(), textInputQueryDescriptor.confidence())
                + "%)";
        if (textInputQueryDescriptor.format() instanceof CsvInputFormatDescriptor) {
          CsvInputFormatDescriptor csvDescriptor =
              (CsvInputFormatDescriptor) textInputQueryDescriptor.format();
          properties.put("has_header", String.valueOf(csvDescriptor.hasHeader()));
          properties.put("delimiter", String.valueOf(csvDescriptor.delimiter()));
          ArrayList<String> nls = new ArrayList<>();
          csvDescriptor.nulls().foreach(nls::add);
          properties.put(
              "nulls",
              nls.stream().map(x -> '"' + x + '"').collect(Collectors.joining(",", "[", "]")));

          ArrayList<String> nans = new ArrayList<>();
          csvDescriptor.nans().foreach(nans::add);
          properties.put(
              "nans",
              nans.stream().map(x -> '"' + x + '"').collect(Collectors.joining(",", "[", "]")));
          properties.put("multiLine_fields", String.valueOf(csvDescriptor.multiLineFields()));
          properties.put("skip", String.valueOf(csvDescriptor.skip()));
          if (csvDescriptor.escapeChar().isDefined()) {
            properties.put("escape", csvDescriptor.escapeChar().get().toString());
          }
          if (csvDescriptor.quoteChar().isDefined()) {
            properties.put("quote", csvDescriptor.quoteChar().get().toString());
          }
          format = "csv";
          tipe = csvDescriptor.tipe();
          sampled = csvDescriptor.sampled();
        } else if (textInputQueryDescriptor.format() instanceof JsonInputFormatDescriptor) {
          JsonInputFormatDescriptor jsonDescriptor =
              (JsonInputFormatDescriptor) textInputQueryDescriptor.format();
          format = "json";
          tipe = jsonDescriptor.tipe();
          sampled = jsonDescriptor.sampled();
        } else if (textInputQueryDescriptor.format() instanceof HjsonInputFormatDescriptor) {
          HjsonInputFormatDescriptor hjsonDescriptor =
              (HjsonInputFormatDescriptor) textInputQueryDescriptor.format();
          format = "hjson";
          tipe = hjsonDescriptor.tipe();
          sampled = hjsonDescriptor.sampled();
        } else if (textInputQueryDescriptor.format() instanceof XmlInputFormatDescriptor) {
          XmlInputFormatDescriptor xmlDescriptor =
              (XmlInputFormatDescriptor) textInputQueryDescriptor.format();
          format = "xml";
          tipe = xmlDescriptor.tipe();
          sampled = xmlDescriptor.sampled();
        } else if (textInputQueryDescriptor.format() instanceof LinesInputFormatDescriptor) {
          LinesInputFormatDescriptor linesDescriptor =
              (LinesInputFormatDescriptor) textInputQueryDescriptor.format();
          format = "lines";
          tipe = linesDescriptor.tipe();
          sampled = linesDescriptor.sampled();
          if (linesDescriptor.regex().isDefined()) {
            properties.put("regex", linesDescriptor.regex().get());
          }
        }
      }

      Rql2Type rql2Type =
          (Rql2Type) Rql2TypeUtils$.MODULE$.inferTypeToRql2Type(tipe, sampled, sampled);
      Rql2Type flatten = rql2Type;
      boolean isCollection = false;

      if (rql2Type instanceof Rql2IterableType) {
        Rql2IterableType rql2IterableType = (Rql2IterableType) rql2Type;
        flatten = (Rql2Type) rql2IterableType.innerType();
        isCollection = true;
      } else if (rql2Type instanceof Rql2ListType) {
        Rql2ListType rql2IterableType = (Rql2ListType) rql2Type;
        flatten = (Rql2Type) rql2IterableType.innerType();
        isCollection = true;
      }

      String formattedType = SourcePrettyPrinter$.MODULE$.format(rql2Type);

      Object record = RawLanguage.get(this).createPureRecord();

      record = addPropNode.execute(this, record, "format", format);
      record = addPropNode.execute(this, record, "comment", comment);
      record = addPropNode.execute(this, record, "type", formattedType);

      Object[] propRecords = new Object[properties.size()];
      // properties
      List<String> keyList = new ArrayList<>(properties.keySet());
      for (int i = 0; i < keyList.size(); i++) {
        Object rec = RawLanguage.get(this).createPureRecord();
        rec = addPropNode.execute(this, rec, "name", keyList.get(i));
        if (properties.containsKey(keyList.get(i))) {
          rec = addPropNode.execute(this, rec, "value", properties.get(keyList.get(i)));
        } else {
          rec = addPropNode.execute(this, rec, "value", NullObject.INSTANCE);
        }
        propRecords[i] = rec;
      }
      ObjectList propList = new ObjectList(propRecords);

      record = addPropNode.execute(this, record, "properties", propList);
      record = addPropNode.execute(this, record, "is_collection", isCollection);

      // columns
      if (flatten instanceof Rql2RecordType) {
        Rql2RecordType rql2RecordType = (Rql2RecordType) flatten;
        Object[] columnRecords = new Object[rql2RecordType.atts().length()];
        for (int i = 0; i < rql2RecordType.atts().length(); i++) {
          String typeStr;
          boolean isNullable;
          Rql2TypeWithProperties fieldType =
              (Rql2TypeWithProperties) rql2RecordType.atts().apply(i).tipe();
          typeStr = SourcePrettyPrinter$.MODULE$.format(fieldType);
          isNullable = fieldType.props().contains(Rql2IsNullableTypeProperty.apply());
          Object column = RawLanguage.get(this).createPureRecord();
          column =
              addPropNode.execute(this, column, "col_name", rql2RecordType.atts().apply(i).idn());
          column = addPropNode.execute(this, column, "col_type", typeStr);
          column = addPropNode.execute(this, column, "nullable", isNullable);
          columnRecords[i] = column;
        }
        ObjectList columnList = new ObjectList(columnRecords);
        record = addPropNode.execute(this, record, "columns", columnList);
      } else {
        String typeStr;
        boolean isNullable = false;
        if (flatten instanceof Rql2TypeWithProperties) {
          typeStr = SourcePrettyPrinter$.MODULE$.format(flatten);
          isNullable =
              ((Rql2TypeWithProperties) flatten)
                  .props()
                  .contains(Rql2IsNullableTypeProperty.apply());
        } else {
          typeStr = SourcePrettyPrinter$.MODULE$.format(flatten);
        }
        Object column = RawLanguage.get(this).createPureRecord();
        column = addPropNode.execute(this, column, "col_name", NullObject.INSTANCE);
        column = addPropNode.execute(this, column, "col_type", typeStr);
        column = addPropNode.execute(this, column, "nullable", isNullable);
        ObjectList columnList = new ObjectList(new Object[] {column});
        record = addPropNode.execute(this, record, "columns", columnList);
      }
      record = addPropNode.execute(this, record, "sampled", sampled);
      return record;
    } catch (RawException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      inferrer.stop();
    }
  }
}
