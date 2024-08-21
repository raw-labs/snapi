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

package com.rawlabs.utils.sources.bytestream.http

trait HttpLocationsTestContext {

  lazy val authorsHjsonHttp = s"http://test-data.raw-labs.com/public/authors.hjson"
  lazy val authorsSmallHjsonHttp = s"http://test-data.raw-labs.com/public/authorsSmall.hjson"
  lazy val publicationsHjsonHttp = s"http://test-data.raw-labs.com/public/publications.hjson"

  lazy val authorsJsonHttp = s"http://test-data.raw-labs.com/public/authors.json"

}
