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

package raw.compiler.snapi.truffle.builtin.xml_extension;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.api.Rql2Arg;
import raw.compiler.rql2.builtin.ReadXmlEntry;
import raw.compiler.rql2.source.Rql2IterableType;
import raw.compiler.rql2.source.Rql2ListType;
import raw.compiler.rql2.source.Rql2Type;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEmitter;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.iterable.list.ListFromNode;
import raw.runtime.truffle.ast.expressions.iterable.list.ListFromUnsafe;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import raw.runtime.truffle.ast.io.json.reader.TryableTopLevelWrapper;
import raw.runtime.truffle.ast.io.xml.parser.XmlReadCollectionNode;
import raw.runtime.truffle.ast.io.xml.parser.XmlReadValueNode;

public class TruffleReadXmlEntry extends ReadXmlEntry implements TruffleEntryExtension {

  private static ExpressionNode getArg(
      List<TruffleArg> namedArgs, String identifier, ExpressionNode defExp) {
    return namedArgs.stream()
        .filter(arg -> arg.identifier() != null && arg.identifier().equals(identifier))
        .map(TruffleArg::exprNode)
        .findFirst()
        .orElse(defExp);
  }

  private static final ExpressionNode defaultEncoding = new StringNode("utf-8");
  private static final ExpressionNode defaultTimestampFormat =
      new StringNode("yyyy-M-d['T'][ ]HH:mm[:ss[.SSS]]");
  private static final ExpressionNode defaultDateFormat = new StringNode("yyyy-M-d");
  private static final ExpressionNode defaultTimeFormat = new StringNode("HH:mm[:ss[.SSS]]");

  @Override
  public ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    List<TruffleArg> truffleArgs = rql2argsToTruffleArgs(args, emitter);
    FrameDescriptor.Builder builder = emitter.getFrameDescriptorBuilder();

    List<TruffleArg> unnamedArgs =
        truffleArgs.stream().filter(arg -> arg.identifier() == null).toList();
    List<TruffleArg> namedArgs =
        truffleArgs.stream().filter(arg -> arg.identifier() != null).toList();
    ExpressionNode encoding = getArg(namedArgs, "encoding", defaultEncoding);
    ExpressionNode timeFormatExp = getArg(namedArgs, "timeFormat", defaultTimeFormat);
    ExpressionNode dateFormatExp = getArg(namedArgs, "dateFormat", defaultDateFormat);
    ExpressionNode timestampFormatExp =
        getArg(namedArgs, "timestampFormat", defaultTimestampFormat);

    return switch (type) {
      case Rql2IterableType iterableType -> {
        ExpressionNode parseNode =
            new XmlReadCollectionNode(
                unnamedArgs.get(0).exprNode(),
                encoding,
                dateFormatExp,
                timeFormatExp,
                timestampFormatExp,
                XmlRecurse.recurseXmlParser(
                    (Rql2TypeWithProperties) iterableType.innerType(), emitter.getLanguage()));
        if (XmlRecurse.isTryable(iterableType)) {
          // Probably will need to be either reused in json and xml or create a copy
          yield new TryableTopLevelWrapper(parseNode);
        } else {
          yield parseNode;
        }
      }
      case Rql2ListType listType -> {
        ExpressionNode parseNode =
            new XmlReadCollectionNode(
                unnamedArgs.get(0).exprNode(),
                encoding,
                dateFormatExp,
                timeFormatExp,
                timestampFormatExp,
                XmlRecurse.recurseXmlParser(
                    (Rql2TypeWithProperties) listType.innerType(), emitter.getLanguage()));

        int generatorSlot =
            builder.addSlot(
                FrameSlotKind.Object, "generator", "a slot to store the generator of osr");
        int listSlot =
            builder.addSlot(
                FrameSlotKind.Object, "filterList", "a slot to store the ArrayList of osr");
        int currentIdxSlot =
            builder.addSlot(
                FrameSlotKind.Int, "currentIdxSlot", "a slot to store the current index of osr");
        int listSizeSlot =
            builder.addSlot(
                FrameSlotKind.Int, "listSize", "a slot to store the size of the list of osr");
        int resultSlot =
            builder.addSlot(
                FrameSlotKind.Object, "list", "a slot to store the result array of osr");

        if (XmlRecurse.isTryable(listType)) {
          // Probably will need to be either reused in json and xml or create a copy
          yield new ListFromNode(
              parseNode,
              (Rql2Type) listType.innerType(),
              generatorSlot,
              listSlot,
              currentIdxSlot,
              listSizeSlot,
              resultSlot);
        } else {
          yield new ListFromUnsafe(
              parseNode,
              (Rql2Type) listType.innerType(),
              generatorSlot,
              listSlot,
              currentIdxSlot,
              listSizeSlot,
              resultSlot);
        }
      }
      case Rql2TypeWithProperties t -> {
        ExpressionNode parseNode =
            new XmlReadValueNode(
                unnamedArgs.get(0).exprNode(),
                encoding,
                dateFormatExp,
                timeFormatExp,
                timestampFormatExp,
                XmlRecurse.recurseXmlParser(t, emitter.getLanguage()).getCallTarget());
        if (XmlRecurse.isTryable(t)) {
          yield new TryableTopLevelWrapper(parseNode);
        } else {
          yield parseNode;
        }
      }
      default -> throw new IllegalStateException("Unexpected value: " + type);
    };
  }
}
