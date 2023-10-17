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

package raw.utils

import com.fasterxml.jackson.annotation.JsonSubTypes.{Type => JsonType}
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[InteractiveUser], name = "interactive"),
    new JsonType(value = classOf[ApiUser], name = "api"),
    new JsonType(value = classOf[ImpersonatedUser], name = "impersonated")
  )
)
sealed trait AuthenticatedUser {
  def uid: Uid
  def name: String
  def permissions: Seq[String]
}

final case class InteractiveUser(uid: Uid, name: String, email: String, permissions: Seq[String] = Seq.empty)
    extends AuthenticatedUser

final case class ApiUser(
    uid: Uid,
    name: String,
    audience: Seq[String],
    issuer: String,
    permissions: Seq[String],
    scopes: Seq[String]
) extends AuthenticatedUser

final case class ImpersonatedUser(
    uid: Uid,
    name: String,
    permissions: Seq[String],
    impersonatedUser: AuthenticatedUser,
    impersonater: AuthenticatedUser
) extends AuthenticatedUser

object AuthenticatedUser {

  private val mapper: ObjectMapper with ClassTagExtensions = {
    val om = new ObjectMapper() with ClassTagExtensions
    om.registerModule(DefaultScalaModule)
    om
  }

  private val reader = mapper.readerFor[AuthenticatedUser]
  private val writer = mapper.writerFor[AuthenticatedUser]

  def toString(user: AuthenticatedUser): String = {
    writer.writeValueAsString(user)
  }

  def fromString(str: String): AuthenticatedUser = {
    reader.readValue(str)
  }

}
