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

import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}
import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.hc.core5.http.{ContentType, HttpHeaders, HttpStatus}
import org.apache.hc.core5.net.URIBuilder
import raw.api.RawException
import raw.config.RawSettings

import java.net.http.HttpClient.Version
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.{InetAddress, URI}
import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class JiraReporterException(message: String, val statusCode: Int, val cause: Throwable = null)
    extends RawException(message, cause)

class NotFoundJiraException(message: String) extends JiraReporterException(message, HttpStatus.SC_NOT_FOUND)

object JiraCrashReporter extends StrictLogging {

  private val JIRA_CONNECTION_TIMEOUT = "raw.jira-crash-reporter.connection-timeout"
  private val JIRA_USERNAME = "raw.jira-crash-reporter.username"
  private val JIRA_TOKEN = "raw.jira-crash-reporter.token"
  private val JIRA_PROJECT = "raw.jira-crash-reporter.project"
  private val JIRA_COMPONENTS = "raw.jira-crash-reporter.components"

  private val BASE_URI = new URI("https://raw-labs.atlassian.net/rest/api/3/")

  private val objectMapper = new ObjectMapper with ClassTagExtensions {
    registerModule(DefaultScalaModule)
    configure(SerializationFeature.INDENT_OUTPUT, true)
  }

  /**
   * Version used when the version specified in createIssue() does not exist in Jira and we are not able to create it
   * (due to lack of permissions, only administrators can create a new Jira version:
   * https://jira.atlassian.com/browse/JSWCLOUD-17608%C2%A0)
   * This version below must have been pre-created in Jira.
   * (Note that versions are persistent objects in Jira, identified by an id, which must be created explicitly).
   */
  private val DEFAULT_VERSION = "0.0.0"

  private var _httpClient: HttpClient = _
  private val httpClientInitLock = new Object

  private def httpClient()(implicit settings: RawSettings): HttpClient = {
    httpClientInitLock.synchronized {
      if (_httpClient == null) {
        val connectionTime = settings.getDuration(JIRA_CONNECTION_TIMEOUT)
        if (_httpClient == null) {
          _httpClient = HttpClient.newBuilder
            .version(Version.HTTP_1_1)
            .connectTimeout(connectionTime)
            .build
        }
      }
    }
    _httpClient
  }

  /**
   * Create an issue on JIRA. The unique signature will ensure that we won't repeatedly be creating the same issue.
   */
  def createIssue(
      summary: String,
      description: String,
      maybeVersionName: Option[String],
      maybeThrowable: Option[Throwable],
      uniqueSignature: String
  )(implicit settings: RawSettings): Option[String] = {
    val maybeUsername = settings.getStringOpt(JIRA_USERNAME)
    val maybeToken = settings.getStringOpt(JIRA_TOKEN, false)
    val maybeProject = settings.getStringOpt(JIRA_PROJECT)
    val components = settings.getStringList(JIRA_COMPONENTS)
    (maybeUsername, maybeToken, maybeProject) match {
      case (Some(user), Some(token), Some(project)) =>
        val effectiveComponents =
          if (components.isEmpty) {
            throw new AssertionError(s"No JIRA project component specified in the configuration")
          } else {
            components
          }
        val issueCreator = new JiraCrashReporter(user, token, project, effectiveComponents)
        // The issue will be tagged with a version. Versions are separate entities in Jira, so they must be created first.

        val maybeVersionId: Option[Long] = maybeVersionName.flatMap { versionName =>
          issueCreator.getVersionId(versionName).orElse {
            try {
              Option(issueCreator.createVersion(versionName))
            } catch {
              case ex: NotFoundJiraException =>
                logger.info(
                  s"Could not create new version: $versionName: ${ex.getMessage} Defaulting to $DEFAULT_VERSION"
                )
                issueCreator.getVersionId(DEFAULT_VERSION).orElse {
                  logger.warn(
                    s"Could not find pre-defined default version: $DEFAULT_VERSION. This should have been created in Jira."
                  )
                  None
                }
            }
          }
        }
        Some(issueCreator.createIssue(summary, description, maybeVersionName, maybeVersionId, maybeThrowable))
      case _ =>
        logger.debug("Not creating issue, JIRA credentials not configured.")
        None
    }
  }
}

private class JiraCrashReporter(username: String, token: String, project: String, components: Seq[String])(
    implicit settings: RawSettings
) extends StrictLogging {
  import JiraCrashReporter._
  private val bearerHeader = Base64.getEncoder.encodeToString(s"$username:$token".getBytes(StandardCharsets.UTF_8))

  private lazy val requestBuilder = HttpRequest
    .newBuilder()
    .header(HttpHeaders.AUTHORIZATION, s"Basic $bearerHeader")
    .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString)
    .header(HttpHeaders.ALLOW, ContentType.APPLICATION_JSON.toString)

  private lazy val projectId = getProjectId(project)

  checkComponentsExist(components)

  private def logRequest(request: HttpRequest, body: String = ""): Unit = {
    logger.debug(s"Calling JIRA REST API: ${request.method()} ${request.uri()} $body")
  }

  private def throwUnexpectedResponse[T](response: HttpResponse[T]): Exception = {
    new JiraReporterException(s"Unexpected response: ${response.body()}", response.statusCode())
  }

  def getProjectId(key: String): Long = {
    val uri = new URIBuilder(BASE_URI).appendPath(s"project/$key").build()
    val request = requestBuilder
      .uri(uri)
      .GET()
      .build()

    val response = httpClient.send(request, BodyHandlers.ofString())
    logRequest(request)
    response.statusCode() match {
      case HttpStatus.SC_OK =>
        val data = objectMapper.readTree(response.body())
        data.get("id").asLong()
      case _ => throw throwUnexpectedResponse(response)
    }
  }

  def getVersionId(versionName: String): Option[Long] = {
    val uri = new URIBuilder(BASE_URI)
      .appendPath(s"project/$projectId/version")
      .addParameter("query", versionName)
      .build()

    val request = requestBuilder
      .uri(uri)
      .GET()
      .build()

    logRequest(request)

    val response = httpClient.send(request, BodyHandlers.ofString())
    response.statusCode() match {
      case HttpStatus.SC_OK =>
        val jsonTree = objectMapper.readTree(response.body())
        val totalValues = jsonTree.get("total").asInt()
        if (totalValues == 0) {
          None
        } else {
          val iter = jsonTree.get("values").iterator()
          while (iter.hasNext) {
            val versionNode = iter.next()
            if (versionNode.get("name").asText() == versionName) {
              return Some(versionNode.get("id").asLong())
            }
          }
          None
        }

      case _ => throw throwUnexpectedResponse(response)
    }
  }

  private def checkComponentsExist(names: Seq[String]): Unit = {
    val existingComponents = getProjectComponents()
    for (n <- names) {
      if (!existingComponents.contains(n)) {
        throw new IllegalStateException(s"Component $n does not exist in Jira Project $project")
      }
    }
  }

  private def getProjectComponents(): Set[String] = {
    val uri = new URIBuilder(BASE_URI).appendPathSegments(s"project/$projectId/components").build()
    val request = requestBuilder
      .uri(uri)
      .GET()
      .build()
    logRequest(request)
    val response = httpClient.send(request, BodyHandlers.ofString())
    response.statusCode() match {
      case HttpStatus.SC_OK =>
        val data = objectMapper.readValue[Array[Map[String, Any]]](response.body())
        data.map(m => m("name").toString).toSet
      case _ => throw throwUnexpectedResponse(response)
    }
  }

  def createVersion(version: String): Long = {
    val uri = new URIBuilder(BASE_URI).appendPath("version").build()

    val data = Map[String, Any](
      "name" -> version,
      "projectId" -> projectId,
      "released" -> true,
      "archived" -> false
    )

    val requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data)

    val request = requestBuilder
      .uri(uri)
      .POST(BodyPublishers.ofString(requestBody))
      .build()
    logRequest(request, requestBody)

    val response = httpClient.send(request, BodyHandlers.ofString())
    response.statusCode() match {
      case HttpStatus.SC_CREATED => objectMapper.readTree(response.body()).get("id").asLong()
      case HttpStatus.SC_NOT_FOUND =>
        logger.warn(s"Could not create new Jira version ($version). Error: ${response.body()}")
        throw new NotFoundJiraException("createVersion")
      case _ => throw throwUnexpectedResponse(response)
    }
  }

  def createIssue(
      summary: String,
      description: String,
      maybeVersionName: Option[String],
      maybeVersionID: Option[Long],
      maybeThrowable: Option[Throwable]
  ): String = {
    val uri = new URIBuilder(BASE_URI).appendPath("issue").build()

    val descriptionText = new StringBuilder(description)

    // Optionally add the value of the variable RAW_DEPLOYMENT to the description of the issue
    val rawDeployment = System.getenv("RAW_DEPLOYMENT")
    if (rawDeployment != null) {
      descriptionText ++= s"\nDeployment: $rawDeployment"
    }
    descriptionText ++= s"\nHost: ${InetAddress.getLocalHost().getHostName()}"

    // Create a content section with two blocks: a description of the bug and a code block with the stack trace.
    val content = new ArrayBuffer[Map[String, Any]]()
    maybeVersionName.foreach { versionName =>
      content += Map(
        "type" -> "paragraph",
        "content" -> Array(
          Map(
            "text" -> s"Affects version: $versionName",
            "type" -> "text"
          )
        )
      )
    }

    content += Map(
      "type" -> "codeBlock",
      "content" -> Array(
        Map(
          "text" -> descriptionText.toString(),
          "type" -> "text"
        )
      )
    )
    val title = new StringBuilder(summary)

    maybeThrowable.foreach { t =>
      title ++= s": ${t.getClass.getName}"
      content += Map(
        "type" -> "codeBlock",
        "attrs" -> Map(),
        "content" -> Array(
          Map(
            "type" -> "text",
            "text" -> ExceptionUtils.getStackTrace(t)
          )
        )
      )
    }

    val fieldsMap = new mutable.HashMap[String, Any]()
    fieldsMap.put("labels", Array("auto-bug-report"))
    maybeVersionID.foreach(versionID => fieldsMap.put("versions", Array(Map("id" -> versionID.toString))))
    fieldsMap.put("summary", title.toString())
    fieldsMap.put("issuetype", Map("id" -> "10004"))
    fieldsMap.put("project", Map("key" -> project))
    fieldsMap.put("components", components.map(name => Map("name" -> name)).toArray)
    fieldsMap.put(
      "description",
      Map("version" -> 1, "type" -> "doc", "content" -> content)
    )
    val data = Map("fields" -> fieldsMap)

    val requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data)
    val request = requestBuilder
      .uri(uri)
      .POST(BodyPublishers.ofString(requestBody))
      .build()
    logRequest(request, requestBody)

    val response = httpClient.send(request, BodyHandlers.ofString())
    response.statusCode() match {
      case HttpStatus.SC_CREATED =>
        val json = objectMapper.readTree(response.body())
        json.get("key").asText()
      case _ => throw throwUnexpectedResponse(response)
    }
  }
}
