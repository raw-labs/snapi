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
import org.bitbucket.inkytonik.kiama.output.PrettyPrinter
import raw.client.utils.RawException
import raw.inferrer.api._
import raw.inferrer.local.auto.{AutoInferrer, InferrerBufferedSeekableIS}
import raw.inferrer.local.csv.{CsvInferrer, CsvMergeTypes}
import raw.inferrer.local.excel.ExcelInferrer
import raw.inferrer.local.hjson.HjsonInferrer
import raw.inferrer.local.jdbc.JdbcInferrer
import raw.inferrer.local.json.JsonInferrer
import raw.inferrer.local.text.TextInferrer
import raw.inferrer.local.xml.{XmlInferrer, XmlMergeTypes}
import raw.sources.api._
import raw.sources.bytestream.api.{ByteStreamLocation, ByteStreamLocationProvider}
import raw.sources.filesystem.api.FileSystemLocationProvider
import raw.sources.jdbc.api.{JdbcLocationProvider, JdbcTableLocationProvider}

import scala.util.control.NonFatal

class LocalInferrerService(implicit sourceContext: SourceContext)
    extends InferrerService
    with InferrerErrorHandler
    with PrettyPrinter
    with StrictLogging {

  private val settings = sourceContext.settings

  private val textInferrer = new TextInferrer
  private val csvInferrer = new CsvInferrer
  private val jsonInferrer = new JsonInferrer
  private val hjsonInferrer = new HjsonInferrer
  private val xmlInferrer = new XmlInferrer

  private val excelInferrer = new ExcelInferrer

  private val jdbcInferrer = new JdbcInferrer

  protected val autoInferrer = new AutoInferrer(
    textInferrer,
    csvInferrer,
    jsonInferrer,
    hjsonInferrer,
    xmlInferrer,
    excelInferrer
  )

  private val defaultSampleFiles = settings.getInt("raw.inferrer.local.sample-files")
  // This buffered-IS is only valid for text formats
  private val useBufferedSeekableIs = sourceContext.settings.getBoolean("raw.inferrer.local.use-buffered-seekable-is")

  private def textInputStream(loc: ByteStreamLocation) = {
    if (useBufferedSeekableIs) {
      new InferrerBufferedSeekableIS(loc.getSeekableInputStream)
    } else {
      loc.getSeekableInputStream
    }
  }

  override def infer(properties: InferrerProperties): InputFormatDescriptor = {
    try {
      properties match {
        case tbl: SqlTableInferrerProperties =>
          val location = JdbcTableLocationProvider.build(tbl.location)
          val tipe = jdbcInferrer.getTableType(location)
          SqlTableInputFormatDescriptor(location.vendor, location.dbName, location.maybeSchema, location.table, tipe)
        case query: SqlQueryInferrerProperties =>
          val location = JdbcLocationProvider.build(query.location)
          val tipe = jdbcInferrer.getQueryType(location, query.sql)
          SqlQueryInputFormatDescriptor(location.vendor, location.dbName, tipe)
        case csv: CsvInferrerProperties =>
          val location = ByteStreamLocationProvider.build(csv.location)
          val is = textInputStream(location)
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
        case csv: ManyCsvInferrerProperties =>
          val location = FileSystemLocationProvider.build(csv.location)
          val files = location.ls()
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
        case excel: ExcelInferrerProperties =>
          val location = ByteStreamLocationProvider.build(excel.location)
          val is = location.getInputStream
          try {
            excelInferrer.infer(is, excel.maybeSheet, excel.maybeHasHeader, excel.maybeAt)
          } finally {
            is.close()
          }
        case excel: ManyExcelInferrerProperties =>
          val location = FileSystemLocationProvider.build(excel.location)
          val files = location.ls()
          readMany(
            files,
            excel.maybeSampleFiles,
            { file =>
              val is = file.getInputStream
              try {
                excelInferrer.infer(is, excel.maybeSheet, excel.maybeHasHeader, excel.maybeAt)
              } finally {
                is.close()
              }
            }
          )
        case hjson: HjsonInferrerProperties =>
          val location = ByteStreamLocationProvider.build(hjson.location)
          val is = textInputStream(location)
          try {
            hjsonInferrer.infer(is, hjson.maybeEncoding, hjson.maybeSampleSize)
          } finally {
            is.close()
          }
        case hjson: ManyHjsonInferrerProperties =>
          val location = FileSystemLocationProvider.build(hjson.location)
          val files = location.ls()
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
        case json: JsonInferrerProperties =>
          val location = ByteStreamLocationProvider.build(json.location)
          val is = textInputStream(location)
          try {
            jsonInferrer.infer(is, json.maybeEncoding, json.maybeSampleSize)
          } finally {
            is.close()
          }
        case json: ManyJsonInferrerProperties =>
          val location = FileSystemLocationProvider.build(json.location)
          val files = location.ls()
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
        case xml: XmlInferrerProperties =>
          val location = ByteStreamLocationProvider.build(xml.location)
          val is = textInputStream(location)
          try {
            xmlInferrer.infer(is, xml.maybeEncoding, xml.maybeSampleSize)
          } finally {
            is.close()
          }
        case xml: ManyXmlInferrerProperties =>
          val location = FileSystemLocationProvider.build(xml.location)
          val files = location.ls()
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
        case auto: AutoInferrerProperties =>
          if (ByteStreamLocationProvider.isSupported(auto.location)) {
            val location = ByteStreamLocationProvider.build(auto.location)
            autoInferrer.infer(location, auto.maybeSampleSize)
          } else if (JdbcTableLocationProvider.isSupported(auto.location.url)) {
            val location = JdbcTableLocationProvider.build(auto.location)
            val tipe = jdbcInferrer.getTableType(location)
            SqlTableInputFormatDescriptor(location.vendor, location.dbName, location.maybeSchema, location.table, tipe)
          } else {
            throw new LocalInferrerException("unsupported location for auto inference")
          }
        case auto: ManyAutoInferrerProperties =>
          val location = FileSystemLocationProvider.build(auto.location)
          val files = location.ls()
          readMany(files, auto.maybeSampleFiles, file => autoInferrer.infer(file, auto.maybeSampleSize))
      }
    } catch {
      case ex: LocalInferrerException => throw ex
      case ex: RawException =>
        // Errors such as e.g. accessing a data source are seen as inferrer errors.
        logger.debug(
          s"""Inferrer failed gracefully.
            |Location: ${properties.location.url}""".stripMargin,
          ex
        )
        throw new LocalInferrerException(ex.getMessage, ex)
      case NonFatal(t) => throw new LocalInferrerException("inference failed unexpectedly", t)
    }
  }

  private val prettyPrinter = new SourceTypePrettyPrinter

  def prettyPrint(sourceType: SourceType): String = {
    prettyPrinter.format(sourceType)
  }

  private class SourceTypePrettyPrinter extends PrettyPrinter {

    override val defaultIndent = 2

    override val defaultWidth = 60

    implicit class extraDocOps(private val d: Doc) extends Doc(d.f) {
      def ?<>(cond: Boolean, other: => Doc): Doc = if (cond) this <> other else this
      def ?<+>(cond: Boolean, other: => Doc): Doc = if (cond) this <+> other else this
      //    def ?<>(v: Option[Doc]): Doc = if (v.isDefined) this <> v.get else this
      //    def ?<+>(v: Option[Doc]): Doc = if (v.isEmpty) this <+> v.get else this
    }

    def format(t: SourceType): String = pretty(toDoc(t)).layout

    def toDoc(t: SourceType): Doc = t match {
      case _: SourceNothingType => text("nothing")
      case _: SourceAnyType => text("any")
      case _: SourceNullType => text("null")
      case SourceByteType(nullable) => text("byte") ?<+> (nullable, "nullable")
      case SourceShortType(nullable) => text("short") ?<+> (nullable, "nullable")
      case SourceIntType(nullable) => text("int") ?<+> (nullable, "nullable")
      case SourceLongType(nullable) => text("long") ?<+> (nullable, "nullable")
      case SourceFloatType(nullable) => text("float") ?<+> (nullable, "nullable")
      case SourceDoubleType(nullable) => text("double") ?<+> (nullable, "nullable")
      case SourceDecimalType(nullable) => text("decimal") ?<+> (nullable, "nullable")
      case SourceBoolType(nullable) => text("bool") ?<+> (nullable, "nullable")
      case SourceStringType(nullable) => text("string") ?<+> (nullable, "nullable")
      case SourceDateType(fmt, nullable) => text("date") <> parens(text(fmt.getOrElse(""))) ?<+> (nullable, "nullable")
      case SourceTimeType(fmt, nullable) => text("time") <> parens(text(fmt.getOrElse(""))) ?<+> (nullable, "nullable")
      case SourceTimestampType(fmt, nullable) =>
        text("timestamp") <> parens(text(fmt.getOrElse(""))) ?<+> (nullable, "nullable")
      case SourceIntervalType(nullable) => text("interval") ?<+> (nullable, "nullable")
      case SourceBinaryType(nullable) => text("blob") ?<+> (nullable, "nullable")
      case SourceOrType(tipes) => tipes.tail.foldLeft(toDoc(tipes.head)) { case (acc, t) => acc <+> "or" <+> toDoc(t) }
      case SourceRecordType(atts, nullable) =>
        val attsDoc = atts.map(att => backquote <> text(att.idn) <> backquote <> ":" <+> toDoc(att.tipe))
        text("record") <> parens(group(nest(lsep(attsDoc, ",")))) ?<+> (nullable, "nullable")
      case SourceCollectionType(inner, nullable) =>
        text("collection") <> parens(group(nest(toDoc(inner)))) ?<+> (nullable, "nullable")
    }
  }

  /**
   * TODO (msb): This method should *also* do a metadata call to ensure it is a directory?
   *  Or should listFiles throw if not a directory? listFiles right now is 'ls' so works for both.
   */
  private def readMany(
      locations: Iterator[ByteStreamLocation],
      maybeSampleFiles: Option[Int],
      doInference: ByteStreamLocation => InputFormatDescriptor
  ): InputFormatDescriptor = {
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
            case ex: RawException =>
              // Annotate actual failing file in message
              throw new LocationException(s"failed inferring '${loc.rawUri}' with error '${ex.getMessage}'", ex)
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
      l: Seq[InputFormatDescriptor]
  ): InputFormatDescriptor = {
    l.tail.foldLeft(l.head)((acc, desc) => mergeDescriptors(acc, desc))
  }

  private def mergeDescriptors(x: InputFormatDescriptor, y: InputFormatDescriptor): InputFormatDescriptor =
    (x, y) match {
      case (
            TextInputStreamFormatDescriptor(ec1, conf1, format1),
            TextInputStreamFormatDescriptor(ec2, conf2, format2)
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
                JsonInputFormatDescriptor(t1, sp1, tf1, df1, tsf1),
                JsonInputFormatDescriptor(t2, sp2, tf2, df2, tsf2)
              ) =>
            if (tf1 != tf2 || df1 != df2 || tsf1 != tsf2)
              throw new LocalInferrerException("incompatible json files found")

            JsonInputFormatDescriptor(MergeTypes.maxOf(t1, t2), sp1 || sp2, tf1, df1, tsf1)
          case (
                XmlInputFormatDescriptor(t1, sampled1, tf1, df1, tsf1),
                XmlInputFormatDescriptor(t2, sampled2, tf2, df2, tsf2)
              ) =>
            if (tf1 != tf2 || df1 != df2 || tsf1 != tsf2)
              throw new LocalInferrerException("incompatible json files found")
            XmlInputFormatDescriptor(XmlMergeTypes.maxOf(t1, t2), sampled1 || sampled2, tf1, df1, tsf1)
          case (
                HjsonInputFormatDescriptor(t1, sp1, tf1, df1, tsf1),
                HjsonInputFormatDescriptor(t2, sp2, tf2, df2, tsf2)
              ) =>
            if (tf1 != tf2 || df1 != df2 || tsf1 != tsf2)
              throw new LocalInferrerException("incompatible hjson files found")

            HjsonInputFormatDescriptor(MergeTypes.maxOf(t1, t2), sp1 || sp2, tf1, df1, tsf1)
          case (LinesInputFormatDescriptor(t1, r1, sp1), LinesInputFormatDescriptor(t2, r2, sp2)) =>
            // if the regexes are not the same then it defaults to simple text without a regex
            if (r1 == r2) LinesInputFormatDescriptor(MergeTypes.maxOf(t1, t2), r1, sp1 || sp2)
            else LinesInputFormatDescriptor(SourceCollectionType(SourceStringType(false), false), None, sp1 || sp2)
          case (
                CsvInputFormatDescriptor(
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
                CsvInputFormatDescriptor(
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
              case SourceCollectionType(SourceRecordType(_, _), _) => CsvInputFormatDescriptor(
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
          case _ => LinesInputFormatDescriptor(SourceCollectionType(SourceStringType(false), false), None, false)
        }
        TextInputStreamFormatDescriptor(encoding, confidence, merge)
      case (
            ExcelInputFormatDescriptor(t1, sheet1, x01, y01, x11, y11),
            ExcelInputFormatDescriptor(t2, sheet2, x02, y02, x12, y12)
          ) =>
        if (sheet1 != sheet2 || x01 != x02 || y01 != y02 || x11 != x12 || y11 != y12) {
          throw new LocalInferrerException("incompatible excel files found")
        }
        ExcelInputFormatDescriptor(MergeTypes.maxOf(t1, t2), sheet1, x01, y01, x11, y11)
      case _ => throw new LocalInferrerException(s"incompatible formats found")
    }

  override def doStop(): Unit = {}

}
