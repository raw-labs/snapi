import java.nio.file.{Files, Paths, StandardCopyOption}
import java.io.{BufferedWriter, FileWriter, File}
import scala.io.Source
import scala.xml.{Node => XmlNode, Elem, Text, XML}
import scala.util.{Try, Success, Failure}
import sys.process._

val jwtCore = "com.github.jwt-scala" %% "jwt-core" % "9.4.4"

val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"

val jacksonModuleScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2"

val mysqlModule = "com.mysql" % "mysql-connector-j" % "8.1.0"

libraryDependencies ++= Seq(
  jwtCore,
  scalaLogging,
  jacksonModuleScala,
  mysqlModule
)

// Map of artifact ID to module name
val moduleNames = Map(
  "jwt-core" -> "jwt.core",
  "scala-logging" -> "typesafe.scalalogging",
  "jackson-module-scala" -> "com.fasterxml.jackson.scala",
  "mysql-connector-j" -> "mysql.connector.j",
)

def updatePom(pomFile: File, newVersion: String): Unit = {
  val pomXml = XML.loadFile(pomFile)

  // Function to remove unwanted nodes
  def removeUnwantedNodes(nodes: Seq[XmlNode]): Seq[XmlNode] = nodes.flatMap {
    case elem: Elem =>
      elem.label match {
        case "url" | "scm" | "licenses" | "developers" | "contributors" => None // Exclude these nodes
        case "properties" => Some(elem.copy(child = elem.child.filterNot(child => child.label == "info.apiUrl")))
        case _ => Some(elem.copy(child = removeUnwantedNodes(elem.child)))
      }
    case other => Some(other)
  }

  // Update version and remove unwanted nodes
  val updatedXml = pomXml.copy(child = removeUnwantedNodes(pomXml.child).map {
    case elem: Elem if elem.label == "version" => elem.copy(child = Text(newVersion))
    case other => other
  })

  // Save the updated XML to the file
  XML.save(pomFile.getAbsolutePath, updatedXml, "UTF-8", xmlDecl = true)

  // Read the file, remove blank lines, and rewrite
  val fileContent = Source.fromFile(pomFile).getLines
  val noBlankLines = fileContent.filter(_.trim.nonEmpty).mkString("\n")
  val writer = new BufferedWriter(new FileWriter(pomFile))
  try {
    writer.write(noBlankLines)
  } finally {
    writer.close()
  }
}

// Task to patch dependencies
val patchDependencies = taskKey[Unit]("Patch dependencies")

patchDependencies := {
  val log = streams.value.log
  val updateReport = update.value

  updateReport.configurations.flatMap(_.modules).distinct.foreach { module =>
    val groupID = module.module.organization
    val artifactID = module.module.name
    val version = module.module.revision
    val scalaVersionSuffixPattern = "_2\\.\\d{1,2}".r
    val normalizedArtifactId = scalaVersionSuffixPattern.replaceFirstIn(artifactID, "")
    if (moduleNames.contains(normalizedArtifactId)) {
      log.info(s"Found module $artifactID:$version")
      module.artifacts.find(_._1.extension == "jar").foreach { case (_, jarFile) =>
        try {
          val pomPath = jarFile.getAbsolutePath.replace(".jar", ".pom")
          val pomFile = new File(pomPath)
          if (!pomFile.exists()) {
            log.error(s"Expected POM file does not exist for $artifactID:$version at $pomPath")
          } else {
            log.info(s"Found POM file for $artifactID:$version at $pomPath")

            val newName = jarFile.getName.replace(".jar", "-rawlabs.jar")
            val newJarFile = new File(jarFile.getParent, newName)
            Files.copy(jarFile.toPath, newJarFile.toPath, StandardCopyOption.REPLACE_EXISTING)
            log.info(s"Patched JAR file created: $newName")

            // Handling the manifest
            val manifest = new File("manifest.txt")
            val bw = new BufferedWriter(new FileWriter(manifest))
            try {
              bw.write(s"Automatic-Module-Name: ${moduleNames(normalizedArtifactId)}\n")
            } finally {
              bw.close()
            }

            // Update the JAR file with the new manifest
            val patchCommand = s"jar --update --file ${newJarFile.getAbsolutePath} --manifest=manifest.txt"
            val patchExitCode = patchCommand.!
            if (patchExitCode == 0) {
              log.info(s"JAR file $newName patched successfully with new manifest.")
            } else {
              log.error(s"Failed to patch JAR file $newName with new manifest.")
            }

            // Update and publish the POM file
            updatePom(pomFile, version)
            val copiedPomFile = new File(pomFile.getParent, s"$artifactID-$version-rawlabs.pom")
            Files.copy(pomFile.toPath, copiedPomFile.toPath, StandardCopyOption.REPLACE_EXISTING)
            log.info(s"Updated POM file for $artifactID with version $version-rawlabs")

            val publishCommand = s"mvn install:install-file -Dfile=${newJarFile.getAbsolutePath} -DpomFile=${copiedPomFile.getAbsolutePath} -DgroupId=$groupID -DartifactId=$artifactID -Dversion=$version-rawlabs -Dpackaging=jar"
            val publishExitCode = publishCommand.!
            if (publishExitCode == 0) {
              log.info(s"Published patched JAR $newName with updated POM $artifactID-$version-rawlabs.")
            } else {
              log.error(s"Failed to publish JAR $newName and POM $artifactID-$version-rawlabs.")
            }
          }
        } catch {
          case e: Exception =>
            log.error(s"Error during the patching process for $artifactID:$version: ${e.getMessage}")
        }
      }
    }
  }
}


val createS3SyncScript = taskKey[Unit]("Create a bash script for syncing dependencies to S3")

val scriptFile = "s3-sync-deps.sh"

createS3SyncScript := {
  val dependencies = libraryDependencies.value

  val writer = new BufferedWriter(new FileWriter(scriptFile))

  try {
    writer.write("#!/bin/bash\n\n")
    dependencies.foreach { dep =>
      writer.write(s"aws s3 sync $$M2_HOME s3://$$BUCKET/maven --exclude '*' --include '**${dep.name}**'\n")
    }
  } finally {
    // Always close the writer to release resources
    writer.close()
  }

  // Make the script executable
  new File(scriptFile).setExecutable(true)

  // Notify that the task is completed
  println(s"Bash script created: $scriptFile")
}