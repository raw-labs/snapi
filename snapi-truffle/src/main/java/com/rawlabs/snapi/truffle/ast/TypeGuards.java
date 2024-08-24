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

package com.rawlabs.snapi.truffle.ast;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Idempotent;
import com.rawlabs.snapi.frontend.rql2.source.*;

public class TypeGuards {

  public static final SnapiIsTryableTypeProperty tryable = SnapiIsTryableTypeProperty.apply();
  public static final SnapiIsNullableTypeProperty nullable = SnapiIsNullableTypeProperty.apply();

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isTryable(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties.props().contains(tryable);
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isNullable(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties.props().contains(nullable);
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isBooleanKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiBoolType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isByteKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiByteType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isShortKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiShortType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isIntKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiIntType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isLongKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiLongType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isFloatKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiFloatType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isDoubleKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiDoubleType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isDecimalKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiDecimalType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isStringKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiStringType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isIntervalKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiIntervalType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isDateKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiDateType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isTimeKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiTimeType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isTimestampKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiTimestampType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isRecordKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiRecordType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isListKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiListType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isIterableKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiIterableType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isBinaryKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof SnapiBinaryType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }
}
