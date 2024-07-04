/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.utils

import com.typesafe.scalalogging.StrictLogging

import java.lang.management.ManagementFactory
import javax.management.ObjectName
import scala.util.control.NonFatal

trait RawMBean {
  def getMBeanName: String
}

object RawMBeansManager extends StrictLogging {

  private val mbs = ManagementFactory.getPlatformMBeanServer
  private var mbeanMap = Map.empty[String, RawMBean]

  def registerMBean(mbean: RawMBean): Unit = {
    require(mbean != null, "MBean cannot be null")
    require(mbean.getMBeanName != null, "MBean name cannot be null")
    if (mbs.isRegistered(new ObjectName(mbean.getMBeanName))) {
      logger.warn(s"MBean ${mbean.getMBeanName} already registered")
    } else {
      val objectName = new ObjectName(mbean.getMBeanName)
      mbs.registerMBean(mbean, objectName)
      mbeanMap += (mbean.getMBeanName() -> mbean)
    }
  }

  def unregisterMBean(name: String): Unit = {
    val objectName = new ObjectName(name)
    if (mbs.isRegistered(objectName)) {
      mbs.unregisterMBean(objectName)
      mbeanMap -= name
    } else {
      logger.warn(s"MBean $name is not registered")
    }
  }

  def getMBeanCopy[T](name: String): Option[T] = {
    try {
      val objectName = new ObjectName(name)
      if (mbs.isRegistered(objectName)) {
        Some(mbeanMap(name).asInstanceOf[T])
      } else {
        None
      }
    } catch {
      case NonFatal(_) => None
    }
  }

}
