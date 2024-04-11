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

package raw.sources.bytestream.api

import raw.client.api.LocationDescription
import raw.client.utils.RawException
import raw.sources.api._

object ByteStreamLocationProvider extends LocationProvider {

  def isSupported(location: LocationDescription)(implicit sourceContext: SourceContext): Boolean = {
    isSupported(location.url)
  }

  def isSupported(url: String)(implicit sourceContext: SourceContext): Boolean = {
    getScheme(url) match {
      case Some(scheme) => sourceContext.byteStreamLocationBuilderServices.exists(_.schemes.contains(scheme))
      case None => false
    }
  }

  @throws[RawException]
  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): ByteStreamLocation = {
    getScheme(location.url) match {
      case Some(scheme) =>
        val impls = sourceContext.byteStreamLocationBuilderServices.filter(_.schemes.contains(scheme))
        if (impls.isEmpty) throw new ByteStreamException(s"no byte stream location implementation found for $scheme")
        else if (impls.length > 1)
          throw new ByteStreamException(s"more than one byte stream location implementation found for $scheme")
        else impls.head.build(location)
      case None => throw new ByteStreamException(s"invalid url: '${location.url}'")
    }
  }

}
