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

package com.rawlabs.utils.sources.bytestream.github

import java.io.InputStream
import java.nio.file.Path
import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.utils.sources.bytestream.http.{HttpByteStreamException, HttpByteStreamLocation}
import com.rawlabs.utils.sources.bytestream.api.{ByteStreamException, ByteStreamLocation, SeekableInputStream}
import com.rawlabs.utils.core.RawSettings

// Supports only public repositories.
class GitHubLocation(val username: String, val repo: String, val file: String, val maybeBranch: Option[String])(
    implicit settings: RawSettings
) extends ByteStreamLocation
    with StrictLogging {

  // If branch is not defined, try to find the default one.
  // Tried listing branches like this:
  //   curl -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/torcato/test-repo/branches
  // But got the following after some tests:
  //   {"message":"API rate limit exceeded for 84.226.22.197. (But here's the good news: Authenticated requests get a higher rate limit. Check out the documentation for more details.)","documentation_url":"https://docs.github.com/rest/overview/resources-in-the-rest-api#rate-limiting"}
  private val branch = {
    maybeBranch.getOrElse {

      def testBranch(branch: String) = {
        val githubUrl = s"https://github.com/$username/$repo/tree/$branch"
        try {
          val httpLocation = new HttpByteStreamLocation(githubUrl)
          httpLocation.testAccess()
          true
        } catch {
          case _: HttpByteStreamException => false
        }
      }

      Seq("main", "master") // Default branch names.
        .find(testBranch)
        .getOrElse(
          throw new ByteStreamException(
            s"could not find default branch after trying 'main' and 'master'; is the GitHub repository public?"
          )
        )
    }
  }

  private val githubUrl = s"https://github.com/$username/$repo/tree/$branch/$file"

  private val httpClient = new HttpByteStreamLocation(githubUrl)

  override protected def doGetInputStream(): InputStream = {
    httpClient.getInputStream
  }

  override protected def doGetSeekableInputStream(): SeekableInputStream = {
    httpClient.getSeekableInputStream
  }

  override def getLocalPath(): Path = {
    httpClient.getLocalPath()
  }

  override def testAccess(): Unit = {
    httpClient.testAccess()
  }
}
