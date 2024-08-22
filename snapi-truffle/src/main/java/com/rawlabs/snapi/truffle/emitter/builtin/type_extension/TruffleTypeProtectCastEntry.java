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
import com.rawlabs.snapi.frontend.rql2.builtin.TypeProtectCastEntry;
import com.rawlabs.snapi.frontend.rql2.source.*;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.type_package.TypeProtectCastOptionNode;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.type_package.TypeProtectCastTryableNode;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.RawTruffleInternalErrorException;
import scala.collection.JavaConverters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TruffleTypeProtectCastEntry extends TypeProtectCastEntry
    implements TruffleEntryExtension {
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpType expSourceType = (ExpType) args.get(0).type();
    ExpType expTargetType = (ExpType) args.get(1).type();
    Type sourceType = expSourceType.t();
    Type targetType = expTargetType.t();
    ExpressionNode e = args.get(2).exprNode();
    Set<Rql2TypeProperty> extraProps = extraProps(targetType, sourceType);
    if (extraProps.equals(Set.of(new Rql2IsTryableTypeProperty()))) {
      return new TypeProtectCastTryableNode(e);
    } else if (extraProps.equals(Set.of(new Rql2IsNullableTypeProperty()))) {
      return new TypeProtectCastOptionNode(e);
    } else {
      return new TypeProtectCastTryableNode(new TypeProtectCastOptionNode(e));
    }
  }

  private Set<Rql2TypeProperty> extraProps(Type target, Type source) {
    return switch (source) {
      case Rql2ListType sourceType -> {
        Rql2ListType targetType = (Rql2ListType) target;
        Set<Rql2TypeProperty> sourceProps = JavaConverters.setAsJavaSet(sourceType.props());
        Set<Rql2TypeProperty> targetProps = JavaConverters.setAsJavaSet(targetType.props());
        Set<Rql2TypeProperty> innerProps = extraProps(targetType.innerType(), sourceType.innerType());
        sourceProps.removeAll(targetProps);
        Set<Rql2TypeProperty> finalProps = new HashSet<>(innerProps);
        finalProps.addAll(sourceProps);
        yield finalProps;
      }
      case Rql2IterableType sourceType -> {
        Rql2IterableType targetType = (Rql2IterableType) target;
        // inner types aren't checked because iterables aren't consumed in the moment they're passed to
        // the function. No exception will be raised under ProtectCast regarding an iterable's items.
        Set<Rql2TypeProperty> sourceProps = JavaConverters.setAsJavaSet(sourceType.props());
        Set<Rql2TypeProperty> targetProps = JavaConverters.setAsJavaSet(targetType.props());
        Set<Rql2TypeProperty> finalProps = new HashSet<>(sourceProps);
        finalProps.removeAll(targetProps);
        yield finalProps;
      }
      case Rql2RecordType sourceType -> {
        Rql2RecordType targetType = (Rql2RecordType) target;
        Set<Rql2TypeProperty> sourceProps = JavaConverters.setAsJavaSet(sourceType.props());
        Set<Rql2TypeProperty> targetProps = JavaConverters.setAsJavaSet(targetType.props());
        Set<Rql2TypeProperty> finalProps = new HashSet<>(sourceProps);
        finalProps.removeAll(targetProps);
        List<Type> sourceTypes = JavaConverters.seqAsJavaList(sourceType.atts()).stream().map(Rql2AttrType::tipe).toList();
        List<Type> targetTypes = JavaConverters.seqAsJavaList(targetType.atts()).stream().map(Rql2AttrType::tipe).toList();
        assert sourceTypes.size() == targetTypes.size();
        for (int i = 0; i < sourceTypes.size(); i++) {
          Type sourceAttrType = sourceTypes.get(i);
          Type targetAttrType = targetTypes.get(i);
          Set<Rql2TypeProperty> innerProps = extraProps(targetAttrType, sourceAttrType);
          finalProps.addAll(innerProps);
        }
        yield finalProps;
      }
      case Rql2TypeWithProperties sourceType -> {
        Rql2TypeWithProperties targetType = (Rql2TypeWithProperties) target;
        Set<Rql2TypeProperty> sourceProps = JavaConverters.setAsJavaSet(sourceType.props());
        Set<Rql2TypeProperty> targetProps = JavaConverters.setAsJavaSet(targetType.props());
        Set<Rql2TypeProperty> finalProps = new HashSet<>(sourceProps);
        finalProps.removeAll(targetProps);
        yield finalProps;
      }
      default -> throw new RawTruffleInternalErrorException();
    };
  }

}
