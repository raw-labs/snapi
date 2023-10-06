import sbt.*

import java.io.FileInputStream
import java.util.Properties

object Dependencies {

  val versionFile = new File("../language/version")

  val rawLanguageVersion: String =
    if (versionFile.exists()) {
      IO.read(versionFile).trim
    } else {
      "0.0.0" // Default version number if the file doesn't exist.
    }

  val rawLanguage = "com.raw-labs" %% "raw-language" % rawLanguageVersion

  // Read version to use

  import java.util.Properties
  import java.io.FileInputStream

  val rawLanguageExtensionsVersion: String = {
    val properties = new Properties()
    val fs = new FileInputStream("../version.properties")
    try {
      properties.load(fs)
      properties.getProperty("extensions.version")
    } finally {
      fs.close()
    }
  }

  val rawLanguageExtensions = "com.raw-labs" %% "raw-language-extensions" % rawLanguageExtensionsVersion

}
