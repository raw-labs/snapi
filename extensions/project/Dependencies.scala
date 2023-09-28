import sbt.*

import java.io.FileInputStream
import java.util.Properties

object Dependencies {

  val rawLanguageVersion = IO.read(new File("../language/version")).trim

  val rawLanguage = "com.raw-labs" %% "raw-language" % rawLanguageVersion

}
