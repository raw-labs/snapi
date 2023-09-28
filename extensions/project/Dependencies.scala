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

}
