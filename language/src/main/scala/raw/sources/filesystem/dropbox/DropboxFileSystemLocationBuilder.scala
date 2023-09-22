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

package raw.sources.filesystem.dropbox

import com.typesafe.scalalogging.StrictLogging
import raw.creds.api.{BearerToken, DropboxToken}
import raw.sources.filesystem.{FileSystemException, FileSystemLocation, FileSystemLocationBuilder}
import raw.sources.{LocationDescription, SourceContext}

object DropboxFileSystemLocationBuilder {
  val dropboxRegex = "dropbox:(?://([^/]+)?)?(.*)".r
}
class DropboxFileSystemLocationBuilder extends FileSystemLocationBuilder with StrictLogging {

  // Syntax: dropbox:/<path>
  // user may be empty
  override def schemes: Seq[String] = Seq("dropbox")

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): FileSystemLocation = {
    import DropboxFileSystemLocationBuilder._

    location.url match {
      case dropboxRegex(name, path) =>
        if (name == null) {
          sourceContext.credentialsService.getDropboxToken(sourceContext.user) match {
            case Some(cred: DropboxToken) =>
              val cli = new DropboxFileSystem(BearerToken(cred.accessToken, Map.empty))(sourceContext.settings)
              new DropboxPath(
                cli,
                path,
                location
              )
            case _ => throw new FileSystemException("no credential found for Dropbox")
          }
        } else {
          logger.debug(s"location: $location, name: $name, path: $path")
          sourceContext.credentialsService.getNewHttpAuth(sourceContext.user, name) match {
            case Some(cred: BearerToken) =>
              val cli = new DropboxFileSystem(cred, name)(sourceContext.settings)
              new DropboxPath(
                cli,
                path,
                location
              )
            case Some(crds @ _) =>
              throw new FileSystemException(s"invalid credential type for Dropbox: ${crds.getClass}")
            case _ => throw new FileSystemException("no credential found for Dropbox")
          }
        }

      case _ => throw new FileSystemException(s"not a Dropbox location: ${location.url}")
    }

  }
}
