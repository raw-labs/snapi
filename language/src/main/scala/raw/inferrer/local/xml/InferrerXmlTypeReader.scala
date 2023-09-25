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

package raw.inferrer.local.xml

import com.ctc.wstx.api.WstxInputProperties
import com.ctc.wstx.stax.WstxInputFactory
import com.typesafe.scalalogging.StrictLogging
import org.codehaus.stax2.XMLInputFactory2
import raw.inferrer.local.{LocalInferrerException, TextTypeInferrer}
import raw.inferrer.api._

import java.io.{Reader, StringReader}
import javax.xml.stream.{XMLInputFactory, XMLStreamConstants}
import scala.collection.mutable
import raw.inferrer.local.xml.InferrerXmlTypeReader._

/**
 * Xml reader with some of our business logic.
 * It can get next obj as LinkedHashMap[String, Any]
 */
object InferrerXmlTypeReader {

  val factory: XMLInputFactory = {
    val f = new WstxInputFactory()
    // Enable multiple roots
    f.setProperty(WstxInputProperties.P_INPUT_PARSING_MODE, WstxInputProperties.PARSING_MODE_DOCUMENTS)
    f.setProperty(XMLInputFactory2.P_LAZY_PARSING, true)
    f.setProperty(XMLInputFactory.SUPPORT_DTD, false)
    f
  }
  val TEXT_FIELD_NAME = "#text"
  val ATTS_PREFIX = "@"
}

private[xml] class InferrerXmlTypeReader(reader: Reader, maxRepeatedTags: Int)
    extends StrictLogging
    with XmlMergeTypes
    with TextTypeInferrer {

  import InferrerXmlTypeReader.factory

  def this(str: String, maxRepeatedTags: Int) = this(new StringReader(str), maxRepeatedTags)

  // issue with eventReader and multi document mode : https://github.com/FasterXML/woodstox/issues/42
  private val xmlReader = factory.createXMLStreamReader(reader)

  skipToNext()
  assert(xmlReader.getEventType == XMLStreamConstants.START_ELEMENT)
  val topLevelLabel = xmlReader.getLocalName

  /**
   * Gets the next event, skipping whitespace, comments and xml processing instructions.
   */
  def skipToNext(): Unit = {
    var skip = true
    while (skip && xmlReader.hasNext) {
      xmlReader.next()
      skip = (xmlReader.getEventType == XMLStreamConstants.SPACE ||
        (xmlReader.getEventType == XMLStreamConstants.CHARACTERS && xmlReader.getText.trim == "") ||
        xmlReader.getEventType == XMLStreamConstants.PROCESSING_INSTRUCTION ||
        xmlReader.getEventType == XMLStreamConstants.COMMENT ||
        xmlReader.getEventType == XMLStreamConstants.END_DOCUMENT ||
        xmlReader.getEventType == XMLStreamConstants.START_DOCUMENT ||
        xmlReader.getEventType == XMLStreamConstants.DTD)
    }
  }

  def skipObj(): Unit = {
    val objLabel = xmlReader.getLocalName
    skipToNext()
    var inObj = true
    while (inObj) {
      val ev = xmlReader.getEventType
      ev match {
        case XMLStreamConstants.START_ELEMENT => skipObj()
        case XMLStreamConstants.CHARACTERS | XMLStreamConstants.ENTITY_REFERENCE => skipToNext()
        case XMLStreamConstants.END_ELEMENT =>
          inObj = false
          val label = xmlReader.getLocalName
          assert(label == objLabel)
      }
    }
    skipToNext()
  }

  def atEndOfObj() = {
    xmlReader.getEventType == XMLStreamConstants.END_ELEMENT
  }

  def atEndOfDocument() = {
    xmlReader.getEventType == XMLStreamConstants.END_DOCUMENT
  }

  private def getCurrentTypeOfTextField(idn: String, currentType: SourceType) = {
    def getTypeFromAtts(atts: Vector[SourceAttrType]) = {
      atts.find(att => att.idn == idn).map(_.tipe).getOrElse(SourceNothingType()) match {
        case t: SourcePrimitiveType => t
        case SourceCollectionType(t: SourcePrimitiveType, n) => SourceNullableType.cloneNullable(t, t.nullable || n)
        case _ => SourceNothingType()
      }
    }

    currentType match {
      case SourceRecordType(atts, _) => getTypeFromAtts(atts)
      case SourceCollectionType(SourceRecordType(atts, _), _) => getTypeFromAtts(atts)
      case _ => SourceNothingType()
    }
  }

  def nextObj(currentType: SourceType): SourceType = {
    if (xmlReader.getEventType != XMLStreamConstants.START_ELEMENT) {
      throw new LocalInferrerException(s"unexpected XML element ${xmlReader.getEventType}")
    }

    val objLabel = xmlReader.getLocalName
    var obj: SourceType = SourceNullType()
    val tagCounter: mutable.Map[String, Int] = mutable.HashMap()
    if (xmlReader.getAttributeCount > 0) {
      val atts = (0 until xmlReader.getAttributeCount).map { n =>
        val id = ATTS_PREFIX + xmlReader.getAttributeName(n).getLocalPart
        val fieldType = getCurrentTypeOfTextField(id, currentType)
        SourceAttrType(id, getType(xmlReader.getAttributeValue(n), fieldType))
      }
      obj = SourceRecordType(atts.to, false)
    }

    skipToNext()
    var inObj = true
    while (inObj) {

      val ev = xmlReader.getEventType
      ev match {
        case XMLStreamConstants.START_ELEMENT =>
          val label = xmlReader.getLocalName
          if (tagCounter.getOrElseUpdate(label, 0) >= maxRepeatedTags && maxRepeatedTags > 0) {
            skipObj()
          } else {
            tagCounter(label) += 1
            // If it is the first time we see this tag then use currentType
            // If not use the obj which is the propagated value already
            val currentRecord = if (tagCounter(label) == 1) currentType else obj

            val fieldType = currentRecord match {
              case SourceRecordType(atts, _) =>
                atts.find(att => att.idn == label).map(_.tipe).getOrElse(SourceNothingType())
              case SourceCollectionType(SourceRecordType(atts, _), _) =>
                atts.find(att => att.idn == label).map(_.tipe).getOrElse(SourceNothingType())
              case _ => SourceNothingType()
            }

            val objType = nextObj(fieldType)
            obj = obj match {
              case _: SourceNullType => SourceRecordType(Vector(SourceAttrType(label, objType)), false)
              case record: SourceRecordType => addElemToObj(record, label, objType)
              // this should be a primitive only
              case primitive => SourceRecordType(
                  Vector(
                    SourceAttrType(TEXT_FIELD_NAME, primitive),
                    SourceAttrType(label, objType)
                  ),
                  false
                )
            }
          }
        case XMLStreamConstants.CHARACTERS | XMLStreamConstants.ENTITY_REFERENCE | XMLStreamConstants.CDATA =>
          val fieldType = currentType match {
            case t: SourcePrimitiveType => t
            case record: SourceRecordType => getCurrentTypeOfTextField(TEXT_FIELD_NAME, record)
            case SourceCollectionType(t: SourcePrimitiveType, n) => SourceNullableType.cloneNullable(t, t.nullable || n)
            case _ => SourceNothingType()
          }

          obj = obj match {
            case SourceNullType() => getType(xmlReader.getText, fieldType)
            case record: SourceRecordType =>
              val x = getType(xmlReader.getText, fieldType)
              addElemToObj(record, TEXT_FIELD_NAME, x)
            case _ => maxOf(obj, getType(xmlReader.getText, fieldType))
          }
          skipToNext()
        case XMLStreamConstants.END_ELEMENT =>
          inObj = false
          val label = xmlReader.getLocalName
          assert(label == objLabel)
      }
    }
    skipToNext()
    maxOf(obj, currentType)
  }

  def hasNext(): Boolean = xmlReader.hasNext

  private def addElemToObj(obj: SourceRecordType, label: String, value: SourceType): SourceRecordType = {
    // if the an element already exists with the same name makes a maxOf
    // else just adds the element to the record
    obj.atts
      .find(_.idn == label)
      .map { objElement =>
        val newAtts = obj.atts.filter(_.idn != label)
        val merged = objElement.tipe match {
          case _: SourceCollectionType => maxOf(objElement.tipe, value)
          case _ => SourceCollectionType(maxOf(objElement.tipe, value), false)
        }
        SourceRecordType(newAtts ++ Vector(SourceAttrType(label, merged)), obj.nullable)
      }
      .getOrElse {
        SourceRecordType(obj.atts ++ Vector(SourceAttrType(label, value)), obj.nullable)
      }
  }
}
