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

package com.rawlabs.compiler.snapi.base

import com.typesafe.scalalogging.StrictLogging
import org.bitbucket.inkytonik.kiama.util.Entity

// TODO (msb): Is keeping track of entities useful in practice, or just drop and rely on the simpler Kiama version?
final class MultipleEntity(val entities: Vector[Entity]) extends Entity

trait SymbolTable extends StrictLogging

final case class UnknownEntity() extends Entity
