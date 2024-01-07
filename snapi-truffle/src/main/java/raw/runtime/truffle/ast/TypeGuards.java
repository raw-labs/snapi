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

package raw.runtime.truffle.ast;

import com.oracle.truffle.api.dsl.Idempotent;
import raw.compiler.rql2.source.*;

public class TypeGuards {

  @Idempotent
  public static boolean isTryable(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties.props().contains(Rql2IsTryableTypeProperty.apply());
    }
    return false;
  }

  @Idempotent
  public static boolean isNullable(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties.props().contains(new Rql2IsNullableTypeProperty());
    }
    return false;
  }

  @Idempotent
  public static boolean isBooleanKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2BoolType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isByteKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2ByteType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isShortKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2ShortType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isIntKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2IntType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isLongKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2LongType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isFloatKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2FloatType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isDoubleKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2DoubleType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isDecimalKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2DecimalType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isStringKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2StringType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isIntervalKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2IntervalType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isDateKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2DateType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isTimeKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2TimeType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isTimestampKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2TimestampType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isRecordKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2RecordType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isListKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2ListType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isIterableKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2IterableType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }

  @Idempotent
  public static boolean isBinaryKind(Rql2Type rql2Type) {
    if (rql2Type instanceof Rql2TypeWithProperties rql2TypeWithProperties) {
      return rql2TypeWithProperties instanceof Rql2BinaryType
          && rql2TypeWithProperties.props().isEmpty();
    }
    return false;
  }
}
