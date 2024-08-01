/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

///*
// * Copyright 2023 RAW Labs S.A.
// *
// * Use of this software is governed by the Business Source License
// * included in the file licenses/BSL.txt.
// *
// * As of the Change Date specified in that file, in accordance with
// * the Business Source License, use of this software will be governed
// * by the Apache License, Version 2.0, included in the file
// * licenses/APL.txt.
// */
//
//package raw.sources.filesystem.dropbox
//
//import raw.sources.api.{LocationDescription, OptionDefinition, SourceContext, StringOptionType}
//import raw.sources.filesystem.api.{FileSystemException, FileSystemLocation, FileSystemLocationBuilder}
//
//import scala.util.matching.Regex
//
//object DropboxFileSystemLocationBuilder {
//  private val REGEX: Regex = "dropbox:(?://)?(.*)".r
//  private val OPTION_ACCESS_TOKEN = "access_token"
//  private val OPTION_USER = "user"
//  private val OPTION_PASSWORD = "password"
//}
//
//class DropboxFileSystemLocationBuilder extends FileSystemLocationBuilder {
//
//  import DropboxFileSystemLocationBuilder._
//
//  override def schemes: Seq[String] = Seq("dropbox")
//
//  override def validOptions: Seq[OptionDefinition] = Seq(
//    OptionDefinition(OPTION_ACCESS_TOKEN, StringOptionType, mandatory = false),
//    OptionDefinition(OPTION_USER, StringOptionType, mandatory = false),
//    OptionDefinition(OPTION_PASSWORD, StringOptionType, mandatory = false)
//  )
//
//  override def build(desc: LocationDescription)(
//      implicit sourceContext: SourceContext
//  ): FileSystemLocation = {
//    val url = desc.url
//    val groups = getRegexMatchingGroups(url, REGEX)
//    val path = groups(0)
//    val dropboxClient = {
//      desc.getStringOpt(OPTION_ACCESS_TOKEN) match {
//        case Some(accessToken) => new DropboxFileSystem(accessToken)(sourceContext.settings)
//        case None => desc.getStringOpt(OPTION_USER) match {
//            case Some(user) => desc.getStringOpt(OPTION_PASSWORD) match {
//                case Some(password) => new DropboxFileSystem(user, password)(sourceContext.settings)
//                case None => throw new FileSystemException("missing password for Dropbox")
//              }
//            case None => throw new FileSystemException("missing user for Dropbox")
//          }
//      }
//    }
//    new DropboxPath(dropboxClient, path, desc.options)
//  }
//}
//
///*
//object LocationBuilder {
//WAIT.. MAKE IT ALSO SUPPORTS ... CRED NAMES..
//AND OLD-STYLE URLS
//NOT JUST NEW-STYLE URLS
//
//}
// */