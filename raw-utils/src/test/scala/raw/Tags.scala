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

package raw

import org.scalatest.Tag

/**
 * Represents a test that performs actions against a database instance.
 */
object DbTest extends Tag("raw.testing.tags.DbAccessTest")

/**
 * Represents a test that performs actions against an external network.
 */
object NetworkTest extends Tag("raw.testing.tags.NetworkAccessTest")

/**
 * Represents a test that performs actions against a raw executor instance.
 */
object ExecutorTest extends Tag("raw.testing.tags.ExecutorAccessTest")

/**
 * Represents a test that performs actions against some entity inside a protected network.
 */

object VPNTest extends Tag("raw.testing.tags.VPNAccessTest")
