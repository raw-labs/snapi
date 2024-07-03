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

package raw.sources.bytestream.github

import raw.sources.bytestream.http.{HttpByteStreamException, HttpByteStreamLocationBuilder}
import raw.sources.bytestream.api.{ByteStreamLocation, ByteStreamLocationBuilder}
import raw.sources.api.{LocationException, SourceContext}
import raw.client.api.{LocationDescription, OptionType, OptionValue}

import scala.util.matching.Regex

object GithubByteStreamLocationBuilder {

  /**
   * Github location format:
   *   github://<username>/<repository>/<filename>[\[<branch>\]]
   *
   * Note that the branch is optional.
   *
   * Example:
   *   github://torcato/test-repo/1/public/code.rql
   * ... is the same as ...
   *   github://torcato/test-repo/1/public/code.rql[main]
   */
  private val REGEX = """github://([^/]+)/([^/]+)/(?:([^\[]+)\[(.*)\]|(.*))""".r
}

class GithubByteStreamLocationBuilder extends ByteStreamLocationBuilder {

  import GithubByteStreamLocationBuilder._

  private val httpBuilder = new HttpByteStreamLocationBuilder()

  override def schemes: Seq[String] = Seq("github")

  override def regex: Regex = REGEX

  override def validOptions: Map[String, OptionType] = Map.empty

  override def build(groups: List[String], options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): ByteStreamLocation = {
    val List(username, repo, maybeNullFile, maybeNullBranch, maybeNullFileWithoutBranch) = groups
    val (file: String, branch: String) =
      if (maybeNullBranch != null) {
        // Branch is defined, so use that.
        (maybeNullFile, maybeNullBranch)
      } else {
        // Branch not defined, so let's find the default one.
        def testBranch(branch: String) = {
          val url = s"https://github.com/$username/$repo/tree/$branch"
          try {
            val httpLocation = httpBuilder.build(LocationDescription(url, options))
            httpLocation.testAccess()
            true
          } catch {
            case _: HttpByteStreamException => false
          }
        }
        // Tried listing branches like this
        //   curl -H "Accept: application/vnd.github.v3+json"   https://api.github.com/repos/torcato/test-repo/branches
        // But got the following after some tests
        //   {"message":"API rate limit exceeded for 84.226.22.197. (But here's the good news: Authenticated requests get a higher rate limit. Check out the documentation for more details.)","documentation_url":"https://docs.github.com/rest/overview/resources-in-the-rest-api#rate-limiting"}
        val branch = Seq("main", "master")
          .find(testBranch)
          .getOrElse(
            throw new LocationException(
              s"could not find default branch after trying 'main' and 'master'; is the GitHub repository public?"
            )
          )
        (maybeNullFileWithoutBranch, branch)
      }
    val url = s"https://raw.githubusercontent.com/$username/$repo/$branch/$file"
    val httpLocation = httpBuilder.build(LocationDescription(url, options))
    new GithubByteStreamLocation(httpLocation, username, repo, file, branch)
  }

}
