/*
 * Copyright 2024 RAW Labs S.A.
 *
 *   Use of this software is governed by the Business Source License
 *   included in the file licenses/BSL.txt.
 *
 *   As of the Change Date specified in that file, in accordance with
 *   the Business Source License, use of this software will be governed
 *   by the Apache License, Version 2.0, included in the file
 *   licenses/APL.txt.
 */

package raw.client.jinja.sql

import raw.client.api.{CompilerService, CompilerServiceBuilder}
import raw.utils.RawSettings

class JinjaSqlCompilerServiceBuilder extends CompilerServiceBuilder {
  override def language: Set[String] = Set("jinja-sql")

  override def build(maybeClassLoader: Option[ClassLoader])(implicit settings: RawSettings): CompilerService =
    new JinjaSqlCompilerService(maybeClassLoader)

}
