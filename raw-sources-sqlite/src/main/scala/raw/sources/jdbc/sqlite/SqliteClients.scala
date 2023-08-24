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

package raw.sources.jdbc.sqlite

import raw.sources.bytestream.ByteStreamLocationProvider
import raw.sources.{LocationDescription, SourceContext}

object SqliteClients {

  def get(url: String)(implicit sourceContext: SourceContext): SqliteClient = {
    // TODO (msb): This construction is suspicious...
    val locationDescription = LocationDescription(url)
    val location = ByteStreamLocationProvider.build(locationDescription)
    val localPath = location.getLocalPath()
    new SqliteClient(localPath)(sourceContext.settings)
  }

}
