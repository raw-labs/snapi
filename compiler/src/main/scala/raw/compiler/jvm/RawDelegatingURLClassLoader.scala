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

package raw.compiler.jvm

import com.typesafe.scalalogging.StrictLogging

import java.net.{URL, URLClassLoader}
import java.util
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Delegates classloading to a dynamic list of classloaders. Classloaders are searched sequentially
 * by registration order. It is possible to unregister classloaders, after which they will no longer
 * be considered in the search for the classes.
 *
 * Intended use: When an instance of Spark is created, it will keep a reference to the current
 * Thread classloader and use it internally during the lifetime of this instance. It is not possible
 * to change this classloader to add the JARs of the new queries to the classpath. We can, however,
 * pass an instance of RawDelegatingURLClassLoader and change its list of children with the classloaders
 * created for each individual query. It also allows us to unregister a classaloader once we are
 * done with the query.
 */
class RawDelegatingURLClassLoader(parent: ClassLoader) extends ClassLoader(parent) with StrictLogging {

  private val childClassLoaders = new CopyOnWriteArrayList[ClassLoader]()

  def registerClassLoader(cl: URLClassLoader): Unit = {
    logger.debug("Registering: " + cl.getURLs.mkString(", "))
    childClassLoaders.add(cl)
  }

  def unregisterClassLoader(cl: URLClassLoader): Boolean = {
    logger.debug("Unregistering: " + cl.getURLs.mkString(", "))
    childClassLoaders.remove(cl)
  }

  override def findClass(name: String): Class[_] = {
    val iter = childClassLoaders.iterator()
    while (iter.hasNext) {
      val cl = iter.next()
      try {
        return cl.loadClass(name)
      } catch {
        case _: ClassNotFoundException =>
      }
    }
    throw new ClassNotFoundException(name)
  }

  override protected def findResource(name: String): URL = {
    val iter = childClassLoaders.iterator()
    while (iter.hasNext) {
      val cl = iter.next()
      val res = cl.getResource(name)
      if (res != null) {
        return res
      }
    }
    null
  }

  override protected def findResources(name: String): util.Enumeration[URL] = {
    import scala.collection.JavaConverters._
    childClassLoaders
      .iterator()
      .asScala
      .flatMap(_.getResources(name).asScala)
      .asJavaEnumeration
  }
}
