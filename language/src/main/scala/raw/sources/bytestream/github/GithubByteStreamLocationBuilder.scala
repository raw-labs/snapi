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

import com.typesafe.scalalogging.StrictLogging
import raw.sources.bytestream.http.{HttpByteStreamLocationBuilder, HttpClientException}
import raw.sources.bytestream.api.{ByteStreamLocation, ByteStreamLocationBuilder}
import raw.sources.api.{LocationDescription, LocationException, SourceContext}

class GithubByteStreamLocationBuilder extends ByteStreamLocationBuilder with StrictLogging {

  // Github regex github://<username>/<repository>/<filename>[\[<branch>\]]
  // branch is optional for example:
  // github://torcato/test-repo/1/public/code.rql the same as github://torcato/test-repo/1/public/code.rql[main]
  private val githubRegex = """github://([^/]+)/([^/]+)/(?:([^\[]+)\[(.*)\]|(.*))""".r
  val httpBuilder = new HttpByteStreamLocationBuilder()

  override def schemes: Seq[String] = Seq("github")

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): ByteStreamLocation = {
    location.url match {
      case githubRegex(username, repo, maybeNullFile, maybeNullBranch, maybeNullFileWithoutBranch) =>
        val (file: String, branch: String) =
          if (maybeNullBranch != null) {
            (maybeNullFile, maybeNullBranch)
          } else {
            def testBranch(branch: String) = {
              val url = s"https://github.com/$username/$repo/tree/$branch"
              try {
                val httpLocation = httpBuilder.build(LocationDescription(url, location.settings))
                httpLocation.testAccess()
                true
              } catch {
                case _: HttpClientException => false
              }
            }
            // tried listing branches like this
            // curl -H "Accept: application/vnd.github.v3+json"   https://api.github.com/repos/torcato/test-repo/branches
            // but got the following after some tests
            // {"message":"API rate limit exceeded for 84.226.22.197. (But here's the good news: Authenticated requests get a higher rate limit. Check out the documentation for more details.)","documentation_url":"https://docs.github.com/rest/overview/resources-in-the-rest-api#rate-limiting"}
            val branch = Seq("main", "master")
              .find(testBranch)
              .getOrElse(
                throw new LocationException(
                  s"could not find default branch for ${location.url}, tried (main, master), is the github repository public?"
                )
              )

            (maybeNullFileWithoutBranch, branch)
          }
        val url = s"https://raw.githubusercontent.com/$username/$repo/$branch/$file"
        val httpLocation = httpBuilder.build(LocationDescription(url, location.settings))
        new GithubByteStreamLocation(httpLocation, location.url)
      case _ => throw new LocationException("not an Github location")
    }
  }
}
