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

/**
 * Base Trait for MBeans
 */
trait RawMBean {

  /**
   * Get the name of the MBean
   *
   * @return the name of the MBean
   */
  def getMBeanName: String
}

object RawMBeansManager extends StrictLogging {

  private val mbs = ManagementFactory.getPlatformMBeanServer
  private var mbeanMap = Map.empty[String, RawMBean]
  private val mbeanLock = new Object

  /**
   * Register an MBean
   *
   * @param mbean the MBean to register
   */
  def registerMBean(mbean: RawMBean): Unit = {
    require(mbean != null, "MBean cannot be null")
    require(mbean.getMBeanName != null, "MBean name cannot be null")
    mbeanLock.synchronized {
      if (mbs.isRegistered(new ObjectName(mbean.getMBeanName))) {
        logger.warn(s"MBean ${mbean.getMBeanName} already registered")
      } else {
        val objectName = new ObjectName(mbean.getMBeanName)
        mbs.registerMBean(mbean, objectName)
        mbeanMap += (mbean.getMBeanName -> mbean)
      }
    }
  }

  /**
   * Unregister an MBean
   *
   * @param name the name of the MBean to unregister
   */
  def unregisterMBean(name: String): Unit = {
    require(name != null, "MBean name cannot be null")
    mbeanLock.synchronized {
      val objectName = new ObjectName(name)
      if (mbs.isRegistered(objectName)) {
        mbs.unregisterMBean(objectName)
        mbeanMap -= name
      } else {
        logger.warn(s"MBean $name is not registered")
      }
    }
  }

  /**
   * Get a copy of an MBean
   *
   * @param name the name of the MBean to get
   * @tparam T the type of the MBean
   * @return an option containing the MBean if it exists, None otherwise
   */
  def getMBeanCopy[T](name: String): Option[T] = {
    require(name != null, "MBean name cannot be null")
    mbeanLock.synchronized {
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

}
