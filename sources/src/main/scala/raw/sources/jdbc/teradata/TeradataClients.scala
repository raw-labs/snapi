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

package raw.sources.jdbc.teradata

import raw.creds.api.TeradataCredential
import raw.sources.api.{LocationException, SourceContext}

object TeradataClients {

  def get(dbName: String)(implicit sourceContext: SourceContext): TeradataClient = {
    sourceContext.credentialsService.getRDBMSServer(sourceContext.user, dbName) match {
      case Some(cred: TeradataCredential) => new TeradataClient(cred)(sourceContext.settings)
      case _ => throw new LocationException(s"no credential found for teradata: $dbName")
    }
  }

}
