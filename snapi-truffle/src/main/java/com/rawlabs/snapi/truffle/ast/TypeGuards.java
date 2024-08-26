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
import com.rawlabs.snapi.frontend.snapi.source.*;

public class TypeGuards {

  public static final SnapiIsTryableTypeProperty tryable = SnapiIsTryableTypeProperty.apply();
  public static final SnapiIsNullableTypeProperty nullable = SnapiIsNullableTypeProperty.apply();

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isTryable(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties.props().contains(tryable);
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isNullable(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties.props().contains(nullable);
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isBooleanKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiBoolType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isByteKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiByteType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isShortKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiShortType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isIntKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiIntType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isLongKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiLongType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isFloatKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiFloatType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isDoubleKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiDoubleType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isDecimalKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiDecimalType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isStringKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiStringType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isIntervalKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiIntervalType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isDateKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiDateType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isTimeKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiTimeType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isTimestampKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiTimestampType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isRecordKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiRecordType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isListKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiListType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isIterableKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiIterableType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  @CompilerDirectives.TruffleBoundary
  public static boolean isBinaryKind(SnapiType snapiType) {
    if (snapiType instanceof SnapiTypeWithProperties snapiTypeWithProperties) {
      return snapiTypeWithProperties instanceof SnapiBinaryType
          && snapiTypeWithProperties.props().isEmpty();
    }
    return false;
  }
}
