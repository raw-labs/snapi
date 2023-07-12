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

package raw.sources.filesystem

import raw.sources.{LocationDescription, SourceContext}
import raw.sources.bytestream.ByteStreamLocationBuilder

abstract class FileSystemLocationBuilder extends ByteStreamLocationBuilder {

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): FileSystemLocation

}
