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

package raw.inferrer.local.excel

import java.io.{BufferedInputStream, InputStream}
import com.typesafe.scalalogging.StrictLogging
import org.apache.poi.ss.usermodel._
import raw.inferrer.api.{
  ExcelInputFormatDescriptor,
  SourceAttrType,
  SourceBoolType,
  SourceCollectionType,
  SourceDoubleType,
  SourceNothingType,
  SourceNullType,
  SourceRecordType,
  SourceStringType,
  SourceTimestampType,
  SourceType
}
import raw.inferrer.local._

class ExcelInferrer extends InferrerErrorHandler with MergeTypes with StrictLogging {

  private val atSplit = "([A-Za-z]*)(\\d*):?([A-Za-z]*)(\\d*)".r

  // TODO: Take out maybeAt and make it into separate elements to ease API!

  def infer(
      is: InputStream,
      maybeSheet: Option[String],
      maybeHasHeader: Option[Boolean],
      maybeAt: Option[String]
  ): ExcelInputFormatDescriptor = {
    val wb = WorkbookFactory.create(new BufferedInputStream(is))
    val sheet = maybeSheet.map(wb.getSheet).getOrElse(wb.getSheetAt(0)) // If not specified, open first sheet

    if (sheet == null) {
      val message = maybeSheet match {
        case Some(name) => s"sheet $name not found in Excel"
        case None => "no sheet found in Excel"
      }
      throw new LocalInferrerException(message)
    }

    // some string parsing to extract coordinates as integers
    val atSplit(sx0, sy0, sx1, sy1) =
      maybeAt.getOrElse(":") // by default we assume no range, all bits will be empty strings
    val x0: Int = if (sx0.isEmpty) 0 else columnIndex(sx0)
    // if not specified start at the top
    val y0 = if (sy0.nonEmpty) sy0.toInt - 1 else 0
    // loop through letters and sum as if they would represent digits between 0 and 25, if empty use None
    val ox1: Option[Int] = if (sx1.nonEmpty) Some(columnIndex(sx1)) else None
    // go until the end if not specified
    val y1: Int = if (sy1.nonEmpty) sy1.toInt - 1 else sheet.getLastRowNum

    val hasHeader = maybeHasHeader.getOrElse(true) // header=true by default
    // vertical index range where values are (header implies to skip first, go til the end or whatever was specified)
    val rowRange = (if (hasHeader) y0 + 1 else y0) to y1

    val evaluator = wb.getCreationHelper.createFormulaEvaluator

    def rawType(c: Cell, value: CellValue): SourceType = {
      if (value == null) SourceNullType()
      else value.getCellType match {
        case CellType.BLANK => SourceNullType()
        case CellType.STRING => SourceStringType(false)
        case CellType.NUMERIC =>
          if (DateUtil.isCellDateFormatted(c)) SourceTimestampType(None, false) else SourceDoubleType(false)
        case CellType.BOOLEAN => SourceBoolType(false)
        case CellType.ERROR => throw new LocalInferrerException("cell type with error is not supported")
        case CellType.FORMULA => throw new LocalInferrerException("cell type with formula is not supported")
        case CellType._NONE => throw new LocalInferrerException("cell type with unknown content is not supported")
      }
    }

    val rowTypes = for (i <- rowRange) yield {
      val row = sheet.getRow(i)
      if (row == null) Seq()
      else {
        val cells = for (x <- x0 to ox1.getOrElse(row.getLastCellNum)) yield {
          val c = row.getCell(x)
          if (c != null) (c, evaluator.evaluate(c))
          else (null, null)
        }
        cells.reverse.dropWhile(_._1 == null).reverseMap { case (c, v) => rawType(c, v) }
      }
    }

    val header = {
      val row = sheet.getRow(y0)
      if (hasHeader && row == null)
        throw new LocalInferrerException(s"no header found in Excel at row ${coordinates(x0, y0)}")
      if (hasHeader) {
        // replace all empty/null cells by a 'null' cell in order to avoid adding null headers
        val allValues = for (i <- x0 to ox1.getOrElse(row.getLastCellNum)) yield {
          val cell = row.getCell(i)
          if (cell == null || cell.getCellType == CellType.BLANK) null else evaluator.evaluate(cell)
        }
        for ((value, i) <- allValues.reverse.dropWhile(_ == null).reverse.zipWithIndex) yield {
          if (value == null || value.getCellType == CellType.BLANK) s"_${i + 1}"
          else {
            value.getCellType match {
              case CellType.BLANK => null
              case CellType.STRING => value.getStringValue
              case CellType.NUMERIC => value.getNumberValue.toString
              case CellType.BOOLEAN => value.getBooleanValue.toString
              case CellType.ERROR => throw new LocalInferrerException("cell type with error is not supported")
              case CellType.FORMULA => throw new LocalInferrerException("cell type with formula is not supported")
              case CellType._NONE => throw new LocalInferrerException("cell type with unknown content is not supported")
            }
          }
        }
      } else {
        Iterable.range(0, ox1.getOrElse(x0 + rowTypes.map(_.length).max) - x0).map(i => s"_${i + 1}")
      }
    }

    val mergedTypes: Seq[SourceType] =
      rowTypes.foldLeft(Seq.fill(header.size)(SourceNothingType()): Seq[SourceType])(maxOfRow)
    val tipe = SourceCollectionType(
      SourceRecordType(mergedTypes.zip(header).map { case (t, h) => SourceAttrType(h, t) }.toVector, false),
      false
    )
    ExcelInputFormatDescriptor(
      tipe,
      sheet.getSheetName,
      x0,
      if (hasHeader) y0 + 1 else y0,
      ox1.getOrElse(x0 + mergedTypes.length),
      y1
    )
  }

  // given vertical and horizontal indexes, returns the excel coordinate (1,2) => "B
  private def coordinates(x_index: Int, y_index: Int) = s"${column(x_index)}${y_index + 1}"

  // computation of the index (0 -> ...) from the string column name
  // I make it public because it's tricky and there is a unit-test.
  private def column(v: Int): String = {
    val alphaDigits = (0 to 'Z' - 'A').map(x => ('A' + x).toChar)
    val base = alphaDigits.length
    if (v < base) alphaDigits(v).toString
    else column(v / base - 1) :+ alphaDigits(v % base)
  }

  private def columnIndex(str: String) = {
    str.toUpperCase.foldLeft(0) { case (v, c) => 26 * v + (c - 'A' + 1) } - 1
  }

}
