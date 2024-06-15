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

package raw.client.rql2.truffle

import com.typesafe.scalalogging.StrictLogging

import java.lang.module.ModuleFinder
import java.nio.file.Paths
import scala.collection.JavaConverters._

/**
 * Create a custom class and module loader for the given module path.
 * This used to completely isolate the Truffle runtime from the rest of the system.
 */
trait CustomClassAndModuleLoader extends StrictLogging {

  /**
   * Create a custom class and module loader for the given module path.
   * This is fully isolated, including only the system packages and whatever JARs are in the module path passed
   * as an argument.
   *
   * @param modulePath the path to the module
   * @return a class loader that contains only the system packages and the modules in the module path
   */
  def createCustomClassAndModuleLoader(modulePath: String): ClassLoader = {

    // Create a custom module finder for the module path
    val modulePathFinder = ModuleFinder.of(Paths.get(modulePath))

    // Get all modules in the module path
    val modulesFromModulePathFinder = modulePathFinder
      .findAll()
      .toArray
      .map(_.asInstanceOf[java.lang.module.ModuleReference].descriptor().name())
      .toSet

    modulesFromModulePathFinder.foreach(f => logger.trace(s"Module path $f"))

    // Get the parent layer
    val parentLayer = ModuleLayer.boot()

    // Get all modules in the system module finder
    val systemModuleFinder = ModuleFinder.ofSystem()
    val systemModules = parentLayer.configuration().modules().asScala.map(_.reference().descriptor().name()).toSet

    // Combine all modules
    val allModules = modulesFromModulePathFinder ++ systemModules

    // Creating a configuration, starting from the parent layer, and resolving all modules.
    // The parent layer is the boot layer, so it contains all the system modules.
    val configuration = parentLayer
      .configuration()
      .resolveAndBind(
        modulePathFinder,
        systemModuleFinder,
        allModules.asJava
      )

    // Get the root classloader, which is the platform classloader
    val rootClassLoader = ClassLoader.getPlatformClassLoader

    // Create a module layer with the custom configuration, using the parent layer and the root classloader
    val controller = ModuleLayer.defineModulesWithOneLoader(configuration, List(parentLayer).asJava, rootClassLoader)
    val customLayer = controller.layer()

    // Because we used ModuleLayer.defineModulesWithOneLoader, all the modules share the same classloader.
    // So we find the first module (whatever that is), get its classloader, and that's the classloader for all of them.
    val moduleName = customLayer.modules().iterator().next().getName
    customLayer.findLoader(moduleName)
  }

}
