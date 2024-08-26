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

package com.rawlabs.utils.sources.filesystem.mock

import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.utils.sources.bytestream.api.SeekableInputStream
import com.rawlabs.utils.sources.filesystem.api._

import java.io.InputStream
import java.lang.StackWalker.StackFrame
import java.nio.file.Path

class MockPath(val delayMillis: Long, val delegate: FileSystemLocation) extends FileSystemLocation with StrictLogging {

  private def doDelay(): Unit = {
    val sw = StackWalker.getInstance()
    val caller: StackFrame = sw.walk(s => {
      s.skip(1).findFirst().get()
    })

    logger.info(s"Pausing for $delayMillis millis. Called by: $caller")
    Thread.sleep(delayMillis)
    logger.info(s"Continuing")
  }

  override def testAccess(): Unit = {
    doDelay()
    delegate.testAccess()
  }

  override protected def doGetInputStream(): InputStream = {
    doDelay()
    delegate.getInputStream
  }

  override protected def doGetSeekableInputStream(): SeekableInputStream = {
    doDelay()
    delegate.getSeekableInputStream
  }

  override def getLocalPath(): Path = {
    doDelay()
    delegate.getLocalPath()
  }

  override def metadata(): FileSystemMetadata = {
    doDelay()
    delegate.metadata()
  }

  override protected def doLs(): Iterator[FileSystemLocation] = {
    doDelay()
    delegate.ls()
  }

  override protected def doLsWithMetadata(): Iterator[(FileSystemLocation, FileSystemMetadata)] = {
    doDelay()
    delegate.lsWithMetadata()
  }

}
