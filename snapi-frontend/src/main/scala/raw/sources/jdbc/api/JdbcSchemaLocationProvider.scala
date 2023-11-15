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

package raw.sources.jdbc.api

import raw.sources.api.{LocationProvider, SourceContext}
import raw.client.api.LocationDescription

object JdbcSchemaLocationProvider extends LocationProvider {

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): JdbcSchemaLocation = {
    getScheme(location.url) match {
      case Some(scheme) =>
        val impls = sourceContext.jdbcSchemaLocationBuilderServices.filter(_.schemes.contains(scheme))
        if (impls.isEmpty) throw new JdbcLocationException(s"no schema location implementation found for $scheme")
        else if (impls.size > 1)
          throw new JdbcLocationException(s"more than one schema location implementation found for $scheme")
        else impls.head.build(location)
      case None => throw new JdbcLocationException(s"invalid url: '${location.url}'")
    }
  }

}
