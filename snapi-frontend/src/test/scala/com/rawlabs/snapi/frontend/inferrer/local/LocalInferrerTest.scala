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

package com.rawlabs.snapi.frontend.inferrer.local

import com.rawlabs.utils.core.{RawTestSuite, RawUtils, SettingsTestContext}
import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.utils.core._
import com.rawlabs.snapi.frontend.inferrer.api._
import com.rawlabs.utils.sources.api._
import com.rawlabs.utils.sources.filesystem.local.LocalPath

import java.io._
import java.util.concurrent.{SynchronousQueue, ThreadPoolExecutor, TimeUnit}
import scala.util.Random

class LocalInferrerTest extends RawTestSuite with SettingsTestContext with StrictLogging {

  test("test encodings") { _ =>
    val encodings = Seq(UTF_8(), UTF_16BE(), UTF_16LE(), ISO_8859_1())
    for (encoding <- encodings) {
      val f = java.io.File.createTempFile(encoding.rawEncoding + ".", ".txt")
      val out = new OutputStreamWriter(new FileOutputStream(f), encoding.rawEncoding)
      // TODO (ctm): find a better test string
      val s = """Vous êtes au volant d'une voiture et vous roulez à vitesse constante.
        |À votre droite, le vide. À votre gauche, un camion de pompiers qui roule à la même vitesse et dans la même direction que vous.
        |Devant vous, un cochon, qui est plus gros que votre voiture !
        |Derrière vous, un hélicoptère qui vous suit, en rase-motte.
        |Le cochon et l'hélicoptère vont à la même vitesse que vous?
        |Face à tous ces éléments, comment faites-vous pour vous arrêter ?
        |C'est simple, vous descendez du manège !
        """.stripMargin
      out.write(s)
      out.close()
      val l1 = new LocalPath(f.toPath)
      val inferrer = new LocalInferrerService
      try {
        val TextInputStreamInferrerOutput(detectedEncoding, _, LinesFormatDescriptor(_, _, _)) =
          inferrer.infer(AutoInferrerInput(l1, None))
        assert(detectedEncoding == encoding)
      } finally {
        RawUtils.withSuppressNonFatalException(inferrer.stop())
        RawUtils.withSuppressNonFatalException(f.delete())
      }
    }
  }

  test("Can call inferrer in parallel") { _ =>
    val files = Array(
      new LocalPath(RawUtils.getResource("data/publications/authors.json")),
      new LocalPath(RawUtils.getResource("data/publications/publications.json")),
      new LocalPath(RawUtils.getResource("data/publications/publications.hjson"))
    )

    val ex = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue[Runnable]())

    ex.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy)

    val inferrer = new LocalInferrerService
    try {
      for (i <- 0 to 100) {
        ex.submit(new Runnable {
          override def run(): Unit = {
            val file = files(Random.nextInt(3))
            inferrer.infer(AutoInferrerInput(file, None))
          }
        })
      }
    } finally {
      RawUtils.withSuppressNonFatalException(inferrer.stop())
      RawUtils.withSuppressNonFatalException(ex.shutdownNow())
    }
  }

}
