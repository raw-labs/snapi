import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.io.BufferedWriter
import java.io.FileWriter

import sys.process._

val jwtCore = "com.github.jwt-scala" %% "jwt-core" % "9.4.4"

val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"

val jacksonModuleScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2"

libraryDependencies ++= Seq(
  jwtCore,
  scalaLogging,
  jacksonModuleScala
)

// Map of artifact ID to module name
val moduleNames = Map(
  "jwt-core" -> "jwt.core",
  "scala-logging" -> "typesafe.scalalogging",
  "jackson-module-scala" -> "com.fasterxml.jackson.scala",
)

// Task to patch dependencies
val patchDependencies = taskKey[Unit]("Patch dependencies")

patchDependencies := {
  val log = streams.value.log
  val updateReport = update.value

  // Obtain map of path to  groupID, artifactID, veresion for all dependency
  val jars = updateReport.configurations.flatMap(_.modules).map { module =>
    val id = module.module
    val groupID = id.organization
    val artifactID = id.name
    val version = id.revision

    // Obtain the path to the JAR file
    val path = module.artifacts.head._2

    // Create a key-value pair with the module name as the key and the groupID and artifactID as the value
    (path -> (groupID, artifactID, version))
  }.toMap

  // Filter out all JAR files that are in the map of module names
  val jarsToPatch = jars
    .filter { case (jar, _) =>
      val path = jar.getAbsolutePath

      // Make sure path matches one of the moduleName keys
      moduleNames.keys.exists(path.contains)
    }
    
  // Patch all JARs that need to be patched
  jarsToPatch
    .foreach { case (jar, (groupID, artifactID, version)) =>
    val path = jar.getAbsolutePath  

    // Get the name of the JAR file
    val name = jar.getName
    val newName = name.replace(".jar", "-rawlabs.jar")
    // Copy the JAR file to a new JAR file with the same name but with the suffix '-rawlabs'
    val newJar = new File(newName)
    Files.copy(jar.toPath, newJar.toPath, StandardCopyOption.REPLACE_EXISTING)

    // Create a file called manifest.txt and add 'Automatic-Module-Name: sdk.http.ahc'
    val manifest = new File("manifest.txt")
    val bw = new BufferedWriter(new FileWriter(manifest))
    bw.write(s"Automatic-Module-Name: ${moduleNames.find { case (k, v) => path.contains(k) }.get._2}\n")
    bw.close()

    // Patch the new JAR file with jar --update --file <jar> --manifest=manifest.txt
    // Check that patching was successful
    val patch = s"jar --update --file $newJar --manifest=manifest.txt"
    val patchExitCode = patch.!
    if (patchExitCode != 0) {
      throw new Exception(s"Failed to patch JAR $newJar")
    }

    // Publish the patched JAR file to the local Maven repository
    val publish = s"mvn install:install-file -Dfile=$newJar -DgroupId=$groupID -DartifactId=$artifactID -Dversion=$version-rawlabs -Dpackaging=jar"
    val publishExitCode = publish.!
    if (publishExitCode != 0) {
      throw new Exception(s"Failed to publish JAR $newJar")
    }
  }
}




