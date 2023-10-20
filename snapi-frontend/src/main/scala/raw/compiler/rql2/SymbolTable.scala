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

package raw.compiler.rql2

import org.bitbucket.inkytonik.kiama.util.Entity
import raw.compiler.base
import raw.compiler.common.source._
import raw.compiler.rql2.api.PackageExtension
import raw.compiler.rql2.source._

/** Entity for a program parameter. */
final class ProgramParamEntity(val p: SourceProgramParam) extends Entity

/**
 *  Entity for a bound identifier.
 *  TODO (msb): Move out of common onto L4 + core.
 */
final class BindEntity(val b: Bind) extends Entity

trait SymbolTable extends base.SymbolTable

object SymbolTable extends SymbolTable

final class LetBindEntity(val b: LetBind) extends Entity

final class LetFunEntity(val f: LetFun) extends Entity

final class LetFunRecEntity(val f: LetFunRec) extends Entity

final class FunParamEntity(val f: FunParam) extends Entity

final class PackageEntity(val p: PackageExtension) extends Entity

final class MethodEntity(val d: Rql2Method) extends Entity
