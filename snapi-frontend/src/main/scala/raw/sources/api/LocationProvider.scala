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

package raw.sources.api

import raw.client.api.LocationDescription

trait LocationProvider {

  def build(location: LocationDescription)(implicit sourceContext: SourceContext): Location

  protected def getScheme(url: String): Option[String] = {
    val i = url.indexOf(':')
    if (i == -1) None
    else Some(url.take(i))
  }

}
