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

package com.rawlabs.snapi.compiler.tests.regressions

import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class RD5914Test extends Rql2TruffleCompilerTestContext {

  test("""let item1 = {name: "coffee machine", price: 200, price: 199}, // price is duplicated, price is an int
    |    item2 = {name: "coffee machine", price: 200.00, price: 199.99} // price is duplicated, price is a double
    |in [item1, item2] // item1 integer fields have to be cast to double
    |""".stripMargin)(_ should evaluateTo("""[{name: "coffee machine", price: 200.0, price: 199.0},
    |{name: "coffee machine", price: 200.00, price: 199.99}
    |]""".stripMargin))

}
