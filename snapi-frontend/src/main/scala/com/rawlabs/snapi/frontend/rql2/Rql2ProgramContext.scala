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

package com.rawlabs.snapi.frontend.rql2

import com.rawlabs.compiler.ProgramEnvironment
import com.rawlabs.snapi.frontend.base.CompilerContext

class Rql2ProgramContext(
    override val programEnvironment: ProgramEnvironment,
    override val compilerContext: CompilerContext
) extends ProgramContext
