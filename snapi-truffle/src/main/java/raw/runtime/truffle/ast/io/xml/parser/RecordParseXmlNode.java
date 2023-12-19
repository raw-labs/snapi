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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.*;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.runtime.exceptions.xml.XmlParserRawTruffleException;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.primitives.NullObject;
import raw.runtime.truffle.runtime.record.RecordNodes;
import raw.runtime.truffle.runtime.record.RecordNodesFactory;
import raw.runtime.truffle.runtime.record.RecordObject;

@NodeInfo(shortName = "RecordParseXml")
public class RecordParseXmlNode extends ExpressionNode {

  @Child
  private RecordNodes.WriteIndexNode writeIndexNode = RecordNodesFactory.WriteIndexNodeGen.create();

  @Children private DirectCallNode[] childDirectCalls;

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
    return doExecute(parser);
  }

  @CompilerDirectives.TruffleBoundary
  private Object doExecute(RawTruffleXmlParser parser) {
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

    // processing lists and collections
    for (String fieldName : collectionValues.keySet()) {
      // build an object list (for all cases)
      ArrayList<Object> items = collectionValues.get(fieldName);
      ObjectList list = new ObjectList(items.toArray());
      int index = collectionsIndex.get(fieldName);
      Type fieldType = fieldTypes[index];
      if (fieldType instanceof Rql2IterableType) {
        // if the collection is an iterable, convert the list to an iterable.
        writeIndexNode.execute(record, index, fieldName, list.toIterable());
      } else {
        writeIndexNode.execute(record, index, fieldName, list);
      }
    }

    StringBuilder missingFields = new StringBuilder();
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
            Object nullValue = NullObject.INSTANCE;
            writeIndexNode.execute(record, i, fieldName, nullValue);
          } else {
            missingFields.append(", ");
            missingFields.append(fieldName);
          }
        }
      }
    }

    // if there are missing fields, throw an exception with all the missing fields
    if (missingFields.length() != 0) {
      String missingFieldsStr = missingFields.substring(2);
      throw new XmlParserRawTruffleException("fields not found: " + missingFieldsStr, parser, this);
    }
    // Skipping the END_OBJECT token here after checking if everything is ok.
    // Because if there is an exception TryableParseXmlNode will skip the current object
    parser.nextToken();
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
      writeIndexNode.execute(record, index, fieldName, value);
      bitSet.set(index);
    }
  }
}
