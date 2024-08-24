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

package com.rawlabs.snapi.truffle.emitter.builtin.type_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.TypeProtectCastEntry;
import com.rawlabs.snapi.frontend.rql2.source.*;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.type_package.TypeProtectCastOptionNode;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.type_package.TypeProtectCastTryableNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleInternalErrorException;
import scala.collection.JavaConverters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TruffleTypeProtectCastEntry extends TypeProtectCastEntry
    implements TruffleEntryExtension {
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, SnapiLanguage rawLanguage) {
    ExpType expSourceType = (ExpType) args.get(0).type();
    ExpType expTargetType = (ExpType) args.get(1).type();
    Type sourceType = expSourceType.t();
    Type targetType = expTargetType.t();
    ExpressionNode e = args.get(2).exprNode();
    Set<SnapiTypeProperty> extraProps = extraProps(targetType, sourceType);
    if (extraProps.equals(Set.of(new SnapiIsTryableTypeProperty()))) {
      return new TypeProtectCastTryableNode(e);
    } else if (extraProps.equals(Set.of(new SnapiIsNullableTypeProperty()))) {
      return new TypeProtectCastOptionNode(e);
    } else {
      return new TypeProtectCastTryableNode(new TypeProtectCastOptionNode(e));
    }
  }

  private Set<SnapiTypeProperty> extraProps(Type target, Type source) {
    return switch (source) {
      case SnapiListType sourceType -> {
        SnapiListType targetType = (SnapiListType) target;
        Set<SnapiTypeProperty> sourceProps = JavaConverters.setAsJavaSet(sourceType.props());
        Set<SnapiTypeProperty> targetProps = JavaConverters.setAsJavaSet(targetType.props());
        Set<SnapiTypeProperty> innerProps = extraProps(targetType.innerType(), sourceType.innerType());
        sourceProps.removeAll(targetProps);
        Set<SnapiTypeProperty> finalProps = new HashSet<>(innerProps);
        finalProps.addAll(sourceProps);
        yield finalProps;
      }
      case SnapiIterableType sourceType -> {
        SnapiIterableType targetType = (SnapiIterableType) target;
        // inner types aren't checked because iterables aren't consumed in the moment they're passed to
        // the function. No exception will be raised under ProtectCast regarding an iterable's items.
        Set<SnapiTypeProperty> sourceProps = JavaConverters.setAsJavaSet(sourceType.props());
        Set<SnapiTypeProperty> targetProps = JavaConverters.setAsJavaSet(targetType.props());
        Set<SnapiTypeProperty> finalProps = new HashSet<>(sourceProps);
        finalProps.removeAll(targetProps);
        yield finalProps;
      }
      case SnapiRecordType sourceType -> {
        SnapiRecordType targetType = (SnapiRecordType) target;
        Set<SnapiTypeProperty> sourceProps = JavaConverters.setAsJavaSet(sourceType.props());
        Set<SnapiTypeProperty> targetProps = JavaConverters.setAsJavaSet(targetType.props());
        Set<SnapiTypeProperty> finalProps = new HashSet<>(sourceProps);
        finalProps.removeAll(targetProps);
        List<Type> sourceTypes = JavaConverters.seqAsJavaList(sourceType.atts()).stream().map(SnapiAttrType::tipe).toList();
        List<Type> targetTypes = JavaConverters.seqAsJavaList(targetType.atts()).stream().map(SnapiAttrType::tipe).toList();
        assert sourceTypes.size() == targetTypes.size();
        for (int i = 0; i < sourceTypes.size(); i++) {
          Type sourceAttrType = sourceTypes.get(i);
          Type targetAttrType = targetTypes.get(i);
          Set<SnapiTypeProperty> innerProps = extraProps(targetAttrType, sourceAttrType);
          finalProps.addAll(innerProps);
        }
        yield finalProps;
      }
      case SnapiTypeWithProperties sourceType -> {
        SnapiTypeWithProperties targetType = (SnapiTypeWithProperties) target;
        Set<SnapiTypeProperty> sourceProps = JavaConverters.setAsJavaSet(sourceType.props());
        Set<SnapiTypeProperty> targetProps = JavaConverters.setAsJavaSet(targetType.props());
        Set<SnapiTypeProperty> finalProps = new HashSet<>(sourceProps);
        finalProps.removeAll(targetProps);
        yield finalProps;
      }
      default -> throw new TruffleInternalErrorException();
    };
  }

}
