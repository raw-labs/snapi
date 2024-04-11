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

import raw.client.utils.{AuthenticatedUser, RawSettings}
import raw.creds.api.CredentialsService
import raw.sources.bytestream.api.ByteStreamLocationBuilder
import raw.sources.filesystem.api.FileSystemLocationBuilder
import raw.sources.jdbc.api.{JdbcLocationBuilder, JdbcSchemaLocationBuilder, JdbcTableLocationBuilder}

import scala.collection.JavaConverters._
import java.util.ServiceLoader

class SourceContext(
    val user: AuthenticatedUser,
    val credentialsService: CredentialsService,
    val settings: RawSettings,
    val maybeClassLoader: Option[ClassLoader]
) {

  val byteStreamLocationBuilderServices: Array[ByteStreamLocationBuilder] = {
    maybeClassLoader match {
      case Some(cl) => ServiceLoader.load(classOf[ByteStreamLocationBuilder], cl).asScala.toArray
      case None => ServiceLoader.load(classOf[ByteStreamLocationBuilder]).asScala.toArray
    }
  }

  val fileSystemLocationBuilderServices: Array[FileSystemLocationBuilder] = maybeClassLoader match {
    case Some(cl) => ServiceLoader.load(classOf[FileSystemLocationBuilder], cl).asScala.toArray
    case None => ServiceLoader.load(classOf[FileSystemLocationBuilder]).asScala.toArray
  }

  val jdbcLocationBuilderServices: Array[JdbcLocationBuilder] = maybeClassLoader match {
    case Some(cl) => ServiceLoader.load(classOf[JdbcLocationBuilder], cl).asScala.toArray
    case None => ServiceLoader.load(classOf[JdbcLocationBuilder]).asScala.toArray
  }

  val jdbcSchemaLocationBuilderServices: Array[JdbcSchemaLocationBuilder] = maybeClassLoader match {
    case Some(cl) => ServiceLoader.load(classOf[JdbcSchemaLocationBuilder], cl).asScala.toArray
    case None => ServiceLoader.load(classOf[JdbcSchemaLocationBuilder]).asScala.toArray
  }

  val jdbcTableLocationBuilderServices: Array[JdbcTableLocationBuilder] = maybeClassLoader match {
    case Some(cl) => ServiceLoader.load(classOf[JdbcTableLocationBuilder], cl).asScala.toArray
    case None => ServiceLoader.load(classOf[JdbcTableLocationBuilder]).asScala.toArray
  }

}
