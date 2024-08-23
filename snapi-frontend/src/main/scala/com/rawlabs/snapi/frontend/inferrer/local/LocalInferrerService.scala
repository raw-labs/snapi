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

import com.rawlabs.utils.core.{RawException, RawSettings}
import com.typesafe.scalalogging.StrictLogging
import org.bitbucket.inkytonik.kiama.output.PrettyPrinter
import com.rawlabs.snapi.frontend.rql2.extensions.LocationDescription
import com.rawlabs.snapi.frontend.inferrer.api._
import com.rawlabs.snapi.frontend.inferrer.local.auto.{AutoInferrer, InferrerBufferedSeekableIS}
import com.rawlabs.snapi.frontend.inferrer.local.csv.{CsvInferrer, CsvMergeTypes}
import com.rawlabs.snapi.frontend.inferrer.local.hjson.HjsonInferrer
import com.rawlabs.snapi.frontend.inferrer.local.jdbc.JdbcInferrer
import com.rawlabs.snapi.frontend.inferrer.local.json.JsonInferrer
import com.rawlabs.snapi.frontend.inferrer.local.text.TextInferrer
import com.rawlabs.snapi.frontend.inferrer.local.xml.{XmlInferrer, XmlMergeTypes}
import com.rawlabs.utils.sources.api._
import com.rawlabs.utils.sources.bytestream.api.ByteStreamLocation
import com.rawlabs.utils.sources.filesystem.api.FileSystemLocation
import com.rawlabs.utils.sources.jdbc.api.JdbcTableLocation

import scala.util.control.NonFatal

class LocalInferrerService(implicit settings: RawSettings)
    extends InferrerService
    with InferrerErrorHandler
    with PrettyPrinter
    with StrictLogging {

  private val textInferrer = new TextInferrer
  private val csvInferrer = new CsvInferrer
  private val jsonInferrer = new JsonInferrer
  private val hjsonInferrer = new HjsonInferrer
  private val xmlInferrer = new XmlInferrer

  private val jdbcInferrer = new JdbcInferrer

  protected val autoInferrer = new AutoInferrer(
    textInferrer,
    csvInferrer,
    jsonInferrer,
    hjsonInferrer,
    xmlInferrer
  )

  private val defaultSampleFiles = settings.getInt("raw.snapi.frontend.inferrer.local.sample-files")
  // This buffered-IS is only valid for text formats
  private val useBufferedSeekableIs = settings.getBoolean("raw.snapi.frontend.inferrer.local.use-buffered-seekable-is")

  private def textInputStream(loc: ByteStreamLocation) = {
    if (useBufferedSeekableIs) {
      new InferrerBufferedSeekableIS(loc.getSeekableInputStream)
    } else {
      loc.getSeekableInputStream
    }
  }

  override def infer(properties: InferrerInput): InferrerOutput = {
    try {
      properties match {
        case tbl: SqlTableInferrerInput =>
          val tipe = jdbcInferrer.getTableType(tbl.location)
          SqlTableInferrerOutput(tipe)
        case query: SqlQueryInferrerInput =>
          val tipe = jdbcInferrer.getQueryType(query.location, query.sql)
          SqlQueryInferrerOutput(tipe)
        case csv: CsvInferrerInput =>
          val is = textInputStream(csv.location)
          try {
            csvInferrer.infer(
              is,
              csv.maybeEncoding,
              csv.maybeHasHeader,
              csv.maybeDelimiters,
              csv.maybeNulls,
              csv.maybeSampleSize,
              csv.maybeNans,
              csv.maybeSkip,
              csv.maybeEscapeChar,
              csv.maybeQuoteChars
            )
          } finally {
            is.close()
          }
        case csv: ManyCsvInferrerInput =>
          val files = csv.location.ls()
          readMany(
            files,
            csv.maybeSampleFiles,
            { file =>
              val is = textInputStream(file)
              try {
                csvInferrer.infer(
                  is,
                  csv.maybeEncoding,
                  csv.maybeHasHeader,
                  csv.maybeDelimiters,
                  csv.maybeNulls,
                  csv.maybeSampleSize,
                  csv.maybeNans,
                  csv.maybeSkip,
                  csv.maybeEscapeChar,
                  csv.maybeQuoteChars
                )
              } finally {
                is.close()
              }
            }
          )
        case hjson: HjsonInferrerInput =>
          val is = textInputStream(hjson.location)
          try {
            hjsonInferrer.infer(is, hjson.maybeEncoding, hjson.maybeSampleSize)
          } finally {
            is.close()
          }
        case hjson: ManyHjsonInferrerInput =>
          val files = hjson.location.ls()
          readMany(
            files,
            hjson.maybeSampleFiles,
            { file =>
              val is = textInputStream(file)
              try {
                hjsonInferrer.infer(is, hjson.maybeEncoding, hjson.maybeSampleSize)
              } finally {
                is.close()
              }
            }
          )
        case json: JsonInferrerInput =>
          val is = textInputStream(json.location)
          try {
            jsonInferrer.infer(is, json.maybeEncoding, json.maybeSampleSize)
          } finally {
            is.close()
          }
        case json: ManyJsonInferrerInput =>
          val files = json.location.ls()
          readMany(
            files,
            json.maybeSampleFiles,
            { file =>
              val is = textInputStream(file)
              try {
                jsonInferrer.infer(is, json.maybeEncoding, json.maybeSampleSize)
              } finally {
                is.close()
              }
            }
          )
        case xml: XmlInferrerInput =>
          val is = textInputStream(xml.location)
          try {
            xmlInferrer.infer(is, xml.maybeEncoding, xml.maybeSampleSize)
          } finally {
            is.close()
          }
        case xml: ManyXmlInferrerInput =>
          val files = xml.location.ls()
          readMany(
            files,
            xml.maybeSampleFiles,
            { file =>
              val is = textInputStream(file)
              try {
                xmlInferrer.infer(is, xml.maybeEncoding, xml.maybeSampleSize)
              } finally {
                is.close()
              }
            }
          )
        case auto: AutoInferrerInput => auto.location match {
            case bs: ByteStreamLocation => autoInferrer.infer(bs, auto.maybeSampleSize)
            case tbl: JdbcTableLocation =>
              val tipe = jdbcInferrer.getTableType(tbl)
              SqlTableInferrerOutput(tipe)
            case _ => throw new LocalInferrerException("unsupported location for auto inference")
          }
        case auto: ManyAutoInferrerInput =>
          val files = auto.location.ls()
          readMany(files, auto.maybeSampleFiles, file => autoInferrer.infer(file, auto.maybeSampleSize))
      }
    } catch {
      case ex: LocalInferrerException => throw ex
      case ex: RawException =>
        // Errors such as e.g. accessing a data source are seen as inferrer errors.
        logger.debug(
          s"""Inferrer failed gracefully.
            |Inferrer properties: $properties""".stripMargin,
          ex
        )
        throw new LocalInferrerException(ex.getMessage, ex)
      case NonFatal(t) => throw new LocalInferrerException("inference failed unexpectedly", t)
    }
  }

  /**
   * TODO (msb): This method should *also* do a metadata call to ensure it is a directory?
   *  Or should listFiles throw if not a directory? listFiles right now is 'ls' so works for both.
   */
  private def readMany(
      locations: Iterator[ByteStreamLocation],
      maybeSampleFiles: Option[Int],
      doInference: ByteStreamLocation => InferrerOutput
  ): InferrerOutput = {
    if (!locations.hasNext) {
      throw new LocalInferrerException("location is empty")
    }

    // Number of files to sample; negative is all.
    val nFilesToSample = maybeSampleFiles.getOrElse(defaultSampleFiles)
    if (nFilesToSample == 0) throw new LocalInferrerException("invalid sample_files")

    def infer(location: Location) = {
      location match {
        case loc: ByteStreamLocation =>
          try {
            doInference(loc)
          } catch {
            case ex: RawException => loc match {
                case fs: FileSystemLocation =>
                  // Annotate actual failing file in message.
                  throw new LocalInferrerException(
                    s"failed inferring '${LocationDescription.locationToPublicUrl(fs)}' with error '${ex.getMessage}'",
                    ex
                  )
                case _ =>
                  // Otherwise, just leave message as is.
                  throw ex
              }
          }
        case _ => throw new LocationException("input stream location required")
      }
    }

    // Collects descriptors
    val descriptors =
      if (nFilesToSample > 0) locations.take(nFilesToSample).map(infer)
      else locations.map(infer)

    // Merges the descriptors
    mergeDescriptors(descriptors.toSeq)
  }

  private def mergeDescriptors(
      l: Seq[InferrerOutput]
  ): InferrerOutput = {
    l.tail.foldLeft(l.head)((acc, desc) => mergeDescriptors(acc, desc))
  }

  private def mergeDescriptors(x: InferrerOutput, y: InferrerOutput): InferrerOutput = (x, y) match {
    case (
          TextInputStreamInferrerOutput(ec1, conf1, format1),
          TextInputStreamInferrerOutput(ec2, conf2, format2)
        ) =>
      val (encoding, confidence) =
        if (conf1 > conf2) (ec1, conf1)
        else (ec2, conf2)

      if (ec1 != ec2) {
        logger.debug(
          s"Detected different encodings: $ec1 (confidence: $conf1); $ec2 (confidence: $conf2). Choosing $encoding."
        )
      }

      val merge = (format1, format2) match {
        case (
              JsonFormatDescriptor(t1, sp1, tf1, df1, tsf1),
              JsonFormatDescriptor(t2, sp2, tf2, df2, tsf2)
            ) =>
          if (tf1 != tf2 || df1 != df2 || tsf1 != tsf2)
            throw new LocalInferrerException("incompatible json files found")

          JsonFormatDescriptor(MergeTypes.maxOf(t1, t2), sp1 || sp2, tf1, df1, tsf1)
        case (
              XmlFormatDescriptor(t1, sampled1, tf1, df1, tsf1),
              XmlFormatDescriptor(t2, sampled2, tf2, df2, tsf2)
            ) =>
          if (tf1 != tf2 || df1 != df2 || tsf1 != tsf2)
            throw new LocalInferrerException("incompatible json files found")
          XmlFormatDescriptor(XmlMergeTypes.maxOf(t1, t2), sampled1 || sampled2, tf1, df1, tsf1)
        case (
              HjsonFormatDescriptor(t1, sp1, tf1, df1, tsf1),
              HjsonFormatDescriptor(t2, sp2, tf2, df2, tsf2)
            ) =>
          if (tf1 != tf2 || df1 != df2 || tsf1 != tsf2)
            throw new LocalInferrerException("incompatible hjson files found")

          HjsonFormatDescriptor(MergeTypes.maxOf(t1, t2), sp1 || sp2, tf1, df1, tsf1)
        case (LinesFormatDescriptor(t1, r1, sp1), LinesFormatDescriptor(t2, r2, sp2)) =>
          // if the regexes are not the same then it defaults to simple text without a regex
          if (r1 == r2) LinesFormatDescriptor(MergeTypes.maxOf(t1, t2), r1, sp1 || sp2)
          else LinesFormatDescriptor(SourceCollectionType(SourceStringType(false), false), None, sp1 || sp2)
        case (
              CsvFormatDescriptor(
                t1,
                hasHeader1,
                sep1,
                nulls1,
                multiline1,
                nans1,
                skip1,
                escape1,
                quote1,
                sampled1,
                tf1,
                df1,
                tsf1
              ),
              CsvFormatDescriptor(
                t2,
                hasHeader2,
                sep2,
                nulls2,
                multiline2,
                nans2,
                skip2,
                escape2,
                quote2,
                sampled2,
                tf2,
                df2,
                tsf2
              )
            ) =>
          if (
            sep1 != sep2 || nulls1 != nulls2 || nans1 != nans2 || hasHeader1 != hasHeader2 || skip1 != skip2 || escape1 != escape2 || quote1 != quote2
          ) {
            throw new LocalInferrerException("incompatible CSV files found")
          }
          val t = CsvMergeTypes.maxOf(t1, t2)
          t match {
            case SourceCollectionType(SourceRecordType(_, _), _) => CsvFormatDescriptor(
                t,
                hasHeader1,
                sep1,
                nulls1,
                multiline1 || multiline2,
                nans1,
                skip1,
                escape1,
                quote1,
                sampled1 || sampled2,
                if (tf1 == tf2) tf1 else None,
                if (df1 == df2) df1 else None,
                if (tsf1 == tsf2) tsf1 else None
              )
            case _ => throw new LocalInferrerException("incompatible CSV files found")
          }

        // Defaults to lines of text if nothing else
        case _ => LinesFormatDescriptor(SourceCollectionType(SourceStringType(false), false), None, false)
      }
      TextInputStreamInferrerOutput(encoding, confidence, merge)
    case _ => throw new LocalInferrerException(s"incompatible formats found")
  }

}
