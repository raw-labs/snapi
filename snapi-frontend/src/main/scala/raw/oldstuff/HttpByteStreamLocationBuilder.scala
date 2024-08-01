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
//package raw.sources.bytestream.http
//
//import com.typesafe.scalalogging.StrictLogging
//import org.apache.hc.core5.http.HttpHeaders
//import raw.sources.api.{
//  ArrayOptionType,
//  BinaryOptionType,
//  IntOptionType,
//  LocationDescription,
//  LocationException,
//  MapOptionType,
//  OptionDefinition,
//  SourceContext,
//  StringOptionType,
//  Tuple2OptionType
//}
//import raw.sources.bytestream.api.ByteStreamLocationBuilder
//
//import java.net.{HttpURLConnection, MalformedURLException, URISyntaxException}
//import java.util.Base64
//import scala.collection.mutable
//
//object HttpByteStreamLocationBuilder {
//  private val REGEX = """(http|https)://(.*)""".r
//
//  private val CONFIG_METHOD = "method"
//  private val CONFIG_HEADERS = "headers"
//  private val CONFIG_ARGS = "args"
//  private val CONFIG_BODY = "body"
//  private val CONFIG_EXPECTED_STATUS = "expected-status"
//
//  // Settings for basic auth
//  private val CONFIG_USERNAME = "username"
//  private val CONFIG_PASSWORD = "password"
//
//}
//
//class HttpByteStreamLocationBuilder extends ByteStreamLocationBuilder with StrictLogging {
//
//  import HttpByteStreamLocationBuilder._
//
//  override def schemes: Seq[String] = Seq("http", "https")
//
//  override def validOptions: Seq[OptionDefinition] = Seq(
//    OptionDefinition(CONFIG_METHOD, StringOptionType, mandatory = false),
//    OptionDefinition(CONFIG_HEADERS, MapOptionType(StringOptionType, StringOptionType), mandatory = false),
//    OptionDefinition(CONFIG_ARGS, ArrayOptionType(Tuple2OptionType(IntOptionType, IntOptionType)), mandatory = false),
//    OptionDefinition(CONFIG_BODY, BinaryOptionType, mandatory = false),
//    OptionDefinition(CONFIG_EXPECTED_STATUS, ArrayOptionType(IntOptionType), mandatory = false),
//    OptionDefinition(CONFIG_USERNAME, StringOptionType, mandatory = false),
//    OptionDefinition(CONFIG_PASSWORD, StringOptionType, mandatory = false)
//  )
//
//  override def build(desc: LocationDescription)(implicit sourceContext: SourceContext): HttpByteStreamLocation = {
//    val url = desc.url
//    val groups = getRegexMatchingGroups(url, REGEX)
//    if (groups.length != 2) {
//      throw new LocationException("invalid HTTP URL")
//    }
//
//    val method = desc.getStringOpt(CONFIG_METHOD).getOrElse("get")
//    val args = desc.getArrayTuple2StringStringOpt(CONFIG_ARGS).getOrElse(Array.empty)
//    val maybeBody = desc.getBinaryOpt(CONFIG_BODY)
//
//    // Build set of headers
//    val headers = mutable.Map[String, String]()
//
//    // Add user headers
//    desc.getMapStringStringOpt(CONFIG_HEADERS).foreach(_.foreach { case (k, v) => headers.put(k, v) })
//
//    // Add Authorization headers
//    (desc.getStringOpt(CONFIG_USERNAME), desc.getStringOpt(CONFIG_PASSWORD)) match {
//      case (Some(username), Some(password)) => headers.put(
//          HttpHeaders.AUTHORIZATION,
//          s"Basic ${Base64.getEncoder.encodeToString(s"$username:$password".getBytes)}"
//        )
//      case _ =>
//    }
//
//    val expectedStatus = desc
//      .getArrayIntOpt(CONFIG_EXPECTED_STATUS)
//      .getOrElse(
//        Array(
//          HttpURLConnection.HTTP_OK,
//          HttpURLConnection.HTTP_ACCEPTED,
//          HttpURLConnection.HTTP_CREATED,
//          HttpURLConnection.HTTP_PARTIAL
//        )
//      )
//
//    val cli =
//      try {
//        new HttpByteStreamClient(method, args, headers.to, maybeBody, expectedStatus)(sourceContext.settings)
//      } catch {
//        case ex: MalformedURLException => throw new LocationException(s"invalid HTTP URL: ${ex.getMessage}", ex)
//        case ex: URISyntaxException => throw new LocationException(s"invalid HTTP URL: ${ex.getMessage}", ex)
//      }
//
//    new HttpByteStreamLocation(cli, desc.url)
//  }
//}
