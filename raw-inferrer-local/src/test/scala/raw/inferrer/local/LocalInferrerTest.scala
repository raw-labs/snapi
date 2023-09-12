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

package raw.inferrer.local

import com.typesafe.scalalogging.StrictLogging
import raw._
import raw.inferrer._
import raw.sources._
import raw.sources.filesystem.local.LocalPath
import raw.utils._

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
      implicit val sourceContext = new SourceContext(null, null, settings)
      val inferrer = new LocalInferrerService
      try {
        val TextInputStreamFormatDescriptor(detectedEncoding, _, LinesInputFormatDescriptor(_, _, _)) =
          inferrer.infer(AutoInferrerProperties(LocationDescription(l1.rawUri), None))
        assert(detectedEncoding == encoding)
      } finally {
        withSuppressNonFatalException(inferrer.stop())
        withSuppressNonFatalException(f.delete())
      }
    }
  }

  test("Can call inferrer in parallel") { _ =>
    val files = Array(
      new LocalPath(getResource("data/publications/authors.json")),
      new LocalPath(getResource("data/publications/publications.json")),
      new LocalPath(getResource("data/publications/publications.hjson"))
    )

    val ex = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue[Runnable]())

    ex.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy)

    implicit val sourceContext = new SourceContext(null, null, settings)
    val inferrer = new LocalInferrerService
    try {
      for (i <- 0 to 100) {
        ex.submit(new Runnable {
          override def run(): Unit = {
            val file = files(Random.nextInt(3))
            inferrer.infer(AutoInferrerProperties(LocationDescription(file.rawUri), None))
          }
        })
      }
    } finally {
      withSuppressNonFatalException(inferrer.stop())
      withSuppressNonFatalException(ex.shutdownNow())
    }
  }

  test("test pretty printer") { _ =>
    val tipe = SourceRecordType(
      Vector(
        SourceAttrType("nhits", SourceIntType(false)),
        SourceAttrType(
          "parameters",
          SourceRecordType(
            Vector(
              SourceAttrType("dataset", SourceCollectionType(SourceStringType(false), false)),
              SourceAttrType("rows", SourceIntType(false)),
              SourceAttrType("start", SourceIntType(false)),
              SourceAttrType("format", SourceStringType(false)),
              SourceAttrType("timezone", SourceStringType(false))
            ),
            false
          )
        ),
        SourceAttrType(
          "records",
          SourceCollectionType(
            SourceRecordType(
              Vector(
                SourceAttrType("datasetid", SourceStringType(false)),
                SourceAttrType("recordid", SourceStringType(false)),
                SourceAttrType(
                  "fields",
                  SourceRecordType(
                    Vector(
                      SourceAttrType("fiche_identite", SourceStringType(false)),
                      SourceAttrType("statut", SourceStringType(false)),
                      SourceAttrType("code_ape", SourceStringType(false)),
                      SourceAttrType("siren", SourceStringType(false)),
                      SourceAttrType("date_immatriculation", SourceStringType(false)),
                      SourceAttrType("geolocalisation", SourceCollectionType(SourceDoubleType(false), true)),
                      SourceAttrType("ville", SourceStringType(false)),
                      SourceAttrType("adresse", SourceStringType(false)),
                      SourceAttrType("code_postal", SourceStringType(false)),
                      SourceAttrType("departement", SourceStringType(false)),
                      SourceAttrType("denomination", SourceStringType(false)),
                      SourceAttrType("devise", SourceStringType(false)),
                      SourceAttrType("nic", SourceStringType(false)),
                      SourceAttrType("forme_juridique", SourceStringType(false)),
                      SourceAttrType("secteur_d_activite", SourceStringType(false)),
                      SourceAttrType("sigle", SourceStringType(true)),
                      SourceAttrType("region", SourceStringType(false)),
                      SourceAttrType("date_de_publication", SourceStringType(false)),
                      SourceAttrType("num_dept", SourceStringType(false)),
                      SourceAttrType("etat", SourceStringType(false)),
                      SourceAttrType("greffe", SourceStringType(false)),
                      SourceAttrType("code_greffe", SourceStringType(false)),
                      SourceAttrType("etat_pub", SourceStringType(false)),
                      SourceAttrType("date_immatriculation_origine", SourceStringType(true))
                    ),
                    false
                  )
                ),
                SourceAttrType(
                  "geometry",
                  SourceRecordType(
                    Vector(
                      SourceAttrType("type", SourceStringType(false)),
                      SourceAttrType("coordinates", SourceCollectionType(SourceDoubleType(false), false))
                    ),
                    true
                  )
                ),
                SourceAttrType("record_timestamp", SourceStringType(false))
              ),
              false
            ),
            false
          )
        )
      ),
      false
    )
    implicit val sourceContext = new SourceContext(null, null, settings)
    val inferrer = new LocalInferrerService
    try {
      val result = inferrer.prettyPrint(tipe)
      assert(
        result ===
          """record(
            |  `nhits`: int,
            |  `parameters`: record(
            |    `dataset`: collection(string),
            |    `rows`: int,
            |    `start`: int,
            |    `format`: string,
            |    `timezone`: string),
            |  `records`: collection(record(
            |      `datasetid`: string,
            |      `recordid`: string,
            |      `fields`: record(
            |        `fiche_identite`: string,
            |        `statut`: string,
            |        `code_ape`: string,
            |        `siren`: string,
            |        `date_immatriculation`: string,
            |        `geolocalisation`: collection(double) nullable,
            |        `ville`: string,
            |        `adresse`: string,
            |        `code_postal`: string,
            |        `departement`: string,
            |        `denomination`: string,
            |        `devise`: string,
            |        `nic`: string,
            |        `forme_juridique`: string,
            |        `secteur_d_activite`: string,
            |        `sigle`: string nullable,
            |        `region`: string,
            |        `date_de_publication`: string,
            |        `num_dept`: string,
            |        `etat`: string,
            |        `greffe`: string,
            |        `code_greffe`: string,
            |        `etat_pub`: string,
            |        `date_immatriculation_origine`: string nullable),
            |      `geometry`: record(
            |        `type`: string,
            |        `coordinates`: collection(double)) nullable,
            |      `record_timestamp`: string)))""".stripMargin
      )
    } finally {
      withSuppressNonFatalException(inferrer.stop())
    }
  }
}
