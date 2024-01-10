import java.nio.file.{Files, Paths, StandardCopyOption}
import java.io.{BufferedWriter, FileWriter, File}
import scala.io.Source
import scala.xml.{Node => XmlNode, Elem, Text, XML}
import scala.util.{Try, Success, Failure}
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

def getCoursierCachePath: String = {
  // Standard path
  val standardPath = sys.props("user.home") + "/.cache/coursier/v1"

  // Check if the standard path exists
  if (Files.exists(Paths.get(standardPath))) {
    standardPath
  } else {
    // Check for a custom path in an environment variable
    val envVar = "COURSIER_CACHE"
    sys.env.get(envVar) match {
      case Some(customPath) =>
        if (Files.exists(Paths.get(customPath))) {
          customPath
        } else {
          throw new IllegalStateException(s"Custom Coursier cache path specified in $envVar does not exist: $customPath")
        }
      case None =>
        throw new IllegalStateException(s"Coursier cache path not found at $standardPath and no custom path specified in $envVar.")
    }
  }
}

def locateOriginalPom(groupID: String, artifactID: String, version: String): File = {
  val coursierCachePath = getCoursierCachePath
  // Convert groupID to URL-like structure (e.g., "com.fasterxml.jackson.module" to "com/fasterxml/jackson/module")
  val groupPath = groupID.replaceAll(".", "/")
  // Construct the path to the POM in the Coursier cache
  val pomPath = s"$coursierCachePath/https/repo1.maven.org/maven2/$groupPath/$artifactID/$version/$artifactID-$version.pom"
  new File(pomPath)
}

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
  (update.value) // Make sure update task is run before patchDependencies task
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

    // Locate and copy the original pom.xml
    val originalPom = locateOriginalPom(groupID, artifactID, version) // Implement this function
    val copiedPom = new File(originalPom.getParent, s"$artifactID-$version-rawlabs.pom")
    Files.copy(originalPom.toPath, copiedPom.toPath, StandardCopyOption.REPLACE_EXISTING)

    // Modify the copied pom.xml
    updatePom(copiedPom, version)

    // Publish the patched JAR file to the local Maven repository
    val publish = s"mvn install:install-file -Dfile=$newJar -DpomFile=${copiedPom.getAbsolutePath} -DgroupId=$groupID -DartifactId=$artifactID -Dversion=$version-rawlabs -Dpackaging=jar"
    val publishExitCode = publish.!
    if (publishExitCode != 0) {
      throw new Exception(s"Failed to publish JAR $newJar")
    }
  }
}




