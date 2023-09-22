import sbt.*

import java.io.FileInputStream
import java.util.Properties

object Dependencies {

  val rawLanguageVersion = {
    val properties = new Properties()
    val fs = new FileInputStream("../version.properties")
    try {
      properties.load(fs)
      properties.getProperty("language.version")
    } finally {
      fs.close()
    }
  }

  val rawLanguage = "com.raw-labs" %% "raw-language" % rawLanguageVersion

}
