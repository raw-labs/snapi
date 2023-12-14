import sbt._
import java.nio.file.{Files, Paths}

object Dependencies {

  def findHighestVersionOfRawUtils(): String = {
    val scalaBinaryVersion: String = scala.util.Properties.versionNumberString.split('.').take(2).mkString(".")

    // Define the path to the Ivy cache for raw-utils
    val ivyCachePath = Path.userHome / ".ivy2" / "local" / "com.raw-labs" / s"raw-utils_$scalaBinaryVersion"

    // Check if the path exists and list all versions
    if (ivyCachePath.exists) {
      ivyCachePath.listFiles()
        .filter(_.isDirectory)
        .map(_.name)
        .sorted
        .lastOption
        .getOrElse(throw new RuntimeException("No version found in Ivy cache for raw-utils"))
    } else {
      throw new RuntimeException("Ivy cache for raw-utils not found")
    }
  }

  val rawUtilsVersion = {
    val versionFilePath = "../utils/version"
    if (Files.exists(Paths.get(versionFilePath))) {
      IO.read(new File(versionFilePath)).trim
    } else {
      findHighestVersionOfRawUtils()
    }
  }

  val rawUtils = "com.raw-labs" %% "raw-utils" % rawUtilsVersion

  val trufflePolyglot = "org.graalvm.polyglot" % "polyglot" % "23.1.0"

}
