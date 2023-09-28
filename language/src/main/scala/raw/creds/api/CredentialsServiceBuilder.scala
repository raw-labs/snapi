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

<<<<<<<< HEAD:extensions/src/main/java/module-info.java
module raw.language.extensions {
  requires raw.language;
========
package raw.creds.api

import raw.utils.RawSettings

trait CredentialsServiceBuilder {

  def name: String

  def build(implicit settings: RawSettings): CredentialsService

>>>>>>>> origin/graal-21-modularize-v2-rebased-to-0.24.27:language/src/main/scala/raw/creds/api/CredentialsServiceBuilder.scala
}
