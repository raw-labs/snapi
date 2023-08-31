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

package raw.runtime.truffle.ast.io.xml.parser;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.runtime.exceptions.xml.XmlParserRawTruffleException;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.option.EmptyOption;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

import java.util.*;

@NodeInfo(shortName = "RecordParseXml")
public class RecordParseXmlNode extends ExpressionNode {

  @Children private DirectCallNode[] childDirectCalls;

  @Child private InteropLibrary records = InteropLibrary.getFactory().createDispatched(2);

  // Field name and its index in the childDirectCalls array
  private final int fieldsSize;
  private final String[] fields;
  private final Rql2TypeWithProperties[] fieldTypes;
  private final Map<String, ArrayList<Object>> collectionValues = new HashMap<>();
  private final Map<String, Integer> fieldsIndex = new HashMap<>();
  private final Map<String, Integer> attributesIndex = new HashMap<>();
  private final Map<String, Integer> collectionsIndex = new HashMap<>();
  private final BitSet refBitSet;
  private final BitSet bitSet;

  public RecordParseXmlNode(
      ProgramExpressionNode[] childProgramExpressionNode,
      String[] fieldNames,
      Rql2TypeWithProperties[] fieldTypes) {
    this.fieldTypes = fieldTypes;
    this.fields = fieldNames;
    this.fieldsSize = childProgramExpressionNode.length;
    this.childDirectCalls = new DirectCallNode[this.fieldsSize];
    refBitSet = new BitSet(this.fieldsSize);
    for (int index = 0; index < this.fieldsSize; index++) {
      String fieldName = fieldNames[index];
      fieldsIndex.put(fieldName, index);
      // register the parser for the field
      this.childDirectCalls[index] =
          DirectCallNode.create(childProgramExpressionNode[index].getCallTarget());
      // take note of fields that should be parsed as attributes
      if (fieldName.startsWith("@")) {
        attributesIndex.put(fieldName, index);
      }
      // take note of fields that should be parsed as collections
      Type fieldType = fieldTypes[index];
      if (fieldType instanceof Rql2IterableType || fieldType instanceof Rql2ListType) {
        collectionsIndex.put(fieldName, index);
        refBitSet.set(index);
      }
    }
    bitSet = new BitSet();
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    RawTruffleXmlParser parser = (RawTruffleXmlParser) args[0];

    for (String fieldName : collectionsIndex.keySet()) {
      // set collections/lists to empty ones
      collectionValues.put(fieldName, new ArrayList<>());
    }
    // restart from the original bitset where collection/list fields are already set
    bitSet.clear();
    bitSet.or(refBitSet);

    // the record to be returned
    RecordObject record = RawLanguage.get(this).createRecord();

    Vector<String> attributes = parser.attributes();
    int nAttributes = attributes.size();
    // Loop through all existing attributes
    for (int attributeIndex = 0; attributeIndex < nAttributes; attributeIndex++) {
      String attributeName = attributes.get(attributeIndex);
      String fieldName = '@' + attributeName;
      Integer index = attributesIndex.get(fieldName);
      if (index != null) {
        // A record field exists with a matching name. Use the related parser.
        Object value = childDirectCalls[index].call(parser, attributeIndex);
        storeFieldValue(fieldName, index, value, record);
      }
    }
    String recordTag = parser.getCurrentName();
    parser.nextToken(); // Now consume the START_OBJECT to "enter" the record.

    // * text content in case of #text field,
    // * START_ELEMENT in case of field,
    // * the record END_ELEMENT in the end, possibly first if the record has no fields (all
    // nulls or
    // empty lists)
    while (!parser.onEndTag()) {
      // we're inside the object, so the current token is a field name, or text content if the
      // record has #text.
      if (parser.onStartTag()) {
        parseTagContent(parser, parser.getCurrentName(), record);
      } else {
        // on #text
        parseTagContent(parser, "#text", record);
      }
    }
    parser.expectEndTag(recordTag);
    parser.nextToken(); // skip the END_OBJECT token

    // processing lists and collections
    for (String fieldName : collectionValues.keySet()) {
      // build an object list (for all cases)
      ArrayList<Object> items = collectionValues.get(fieldName);
      ObjectList list = new ObjectList(items.toArray());
      int index = collectionsIndex.get(fieldName);
      Type fieldType = fieldTypes[index];
      if (fieldType instanceof Rql2IterableType) {
        // if the collection is an iterable, convert the list to an iterable.
        record.writeIdx(index, fieldName, list.toIterable());
      } else {
        record.writeIdx(index, fieldName, list);
      }
    }
    // process nullable fields (null when not found)
    if (bitSet.cardinality() != this.fieldsSize) {
      // not all fields were found in the JSON. Fill the missing nullable ones with nulls or
      // fail.
      for (int i = 0; i < fieldsSize; i++) {
        String fieldName = fields[i];
        if (!bitSet.get(i)) {
          if (fieldTypes[i].props().contains(Rql2IsNullableTypeProperty.apply())) {
            // It's OK, the field is nullable. If it's tryable, make a success null,
            // else a plain
            // null.
            Object nullValue =
                fieldTypes[i].props().contains(Rql2IsTryableTypeProperty.apply())
                    ? ObjectTryable.BuildSuccess(new EmptyOption())
                    : new EmptyOption();
            record.writeIdx(i, fieldName, nullValue);
          } else {
            throw new XmlParserRawTruffleException("field not found: " + fieldName, parser, this);
          }
        }
      }
    }
    return record;
  }

  private void parseTagContent(RawTruffleXmlParser parser, String fieldName, RecordObject record) {
    Integer index = fieldsIndex.get(fieldName);
    if (index != null) {
      applyParser(parser, index, fieldName, record);
    } else {
      // skip the whole tag subtree
      parser.skipTag();
    }
  }

  private void applyParser(
      RawTruffleXmlParser parser, int index, String fieldName, RecordObject record) {
    Object value = childDirectCalls[index].call(parser);
    storeFieldValue(fieldName, index, value, record);
  }

  private void storeFieldValue(String fieldName, int index, Object value, RecordObject record) {
    ArrayList<Object> collectionField = collectionValues.get(fieldName);
    if (collectionField != null) {
      // if the field is a collection or a list, add the item to the list instead writing it
      // in the
      // record.
      collectionField.add(value);
    } else {
      record.writeIdx(index, fieldName, value);
      bitSet.set(index);
    }
  }
}
