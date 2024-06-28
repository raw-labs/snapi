package raw.build

import sbt.Keys._
import sbt.{Def, _}
import sbt.plugins.JvmPlugin

import com.jsuereth.sbtpgp.PgpKeys.{publishSigned}
import java.io.{File, FileInputStream, IOException}
import java.net.URL
import java.security.MessageDigest
import scala.util.control.Breaks._

import scala.sys.process._

object GenParserPlugin extends AutoPlugin {
  override def requires = JvmPlugin
  override def trigger: PluginTrigger = noTrigger

  // Task keys
  object autoImport {
    val generateParser = taskKey[Unit]("Generated antlr4 base parser and lexer")
    val parserDefinitions = settingKey[List[(String, String, String, String)]]("List of parser definitions with output path first followed by the name of the generated package")
    val javaSrcBasePath = settingKey[String]("Java src relative path")
  }

  import autoImport._

  // Provide default settings
  override def projectSettings: Seq[Def.Setting[_]] = Seq(

    generateParser := {
      val s: TaskStreams = streams.value

      val basePath: String = javaSrcBasePath.value
      val parsers = parserDefinitions.value

      // Ensure antlr jar is available
      val antlrJarPath = (ThisBuild / baseDirectory).value / "antlr-4.12.0-complete.jar"
      val antlrJarChecksum = "88f18a2bfac0dde1009eda5c7dce358a52877faef7868f56223a5bcc15329e43"
      ensureAntlrJarAvailable(antlrJarPath.toString(), antlrJarChecksum, s)

      def deleteRecursively(file: File): Unit = {
        if (file.isDirectory) {
          file.listFiles.foreach(deleteRecursively)
        }
        if (file.exists && !file.delete()) {
          throw new IOException(s"Failed to delete ${file.getAbsolutePath}")
        }
      }

      // Java setup handling
      val javaHome = sys.env.getOrElse("JAVA_HOME", sys.props.getOrElse("java.home", ""))
      val javaBin = if (javaHome.nonEmpty) s"$javaHome/bin/java" else "java"

      s.log.info(s"JAVA_HOME: $javaHome")
      s.log.info(s"Using Java binary: $javaBin")

      if (!new File(javaBin).exists) {
        sys.error(s"Java binary not found at $javaBin")
      }

      parsers.foreach(parser => {
        val outputPath = parser._1
        val file = new File(outputPath)
        if (file.exists()) {
          deleteRecursively(file)
        }
        val packageName: String = parser._2
        val command: String = s"$javaBin -jar ${antlrJarPath} -visitor -package $packageName -o $outputPath"
        val output = new StringBuilder
        val logger = ProcessLogger(
          (o: String) => output.append(o + "\n"), // for standard output
          (e: String) => output.append(e + "\n") // for standard error
        )
        val grammarPath = parser._3
        val grammarName = parser._4
        val lexerResult = s"$command  $grammarPath/${grammarName}Lexer.g4".!(logger)
        if (lexerResult == 0) {
          s.log.info("Lexer code generated successfully")
        } else {
          s.log.error("Lexer code generation failed with exit code " + lexerResult)
          s.log.error("Output:\n" + output.toString)
        }
        val parserResult = s"$command $grammarPath/${grammarName}Parser.g4".!(logger)
        if (parserResult == 0) {
          s.log.info("Parser code generated successfully")
        } else {
          s.log.error("Parser code generation failed with exit code " + lexerResult)
          s.log.error("Output:\n" + output.toString)
        }
      })
    },
    parserDefinitions := List(),
    javaSrcBasePath := "",
    Compile / compile := (Compile / compile).dependsOn(generateParser).value,
    Test / test := (Test / test).dependsOn(generateParser).value,
    publish := publish.dependsOn(generateParser).value,
    publishSigned := publishSigned.dependsOn(generateParser).value
  )

  //Ensure ANTLR JAR is downloaded if not present
  def ensureAntlrJarAvailable(jarPath: String, expectedChecksum: String, s: TaskStreams, maxRetries: Int = 3): Unit = {
    val jarFile = new File(jarPath)
    if (!jarFile.exists() || !verifyChecksum(jarFile, expectedChecksum)) {
      val jarName = jarFile.getName
      val url = s"https://github.com/antlr/website-antlr4/raw/gh-pages/download/$jarName"
      breakable {
        for (attempt <- 1 to maxRetries) {
          try {
            s.log.info(s"Attempt $attempt: Downloading $jarName...")
            new URI(url).toURL() #> jarFile !!

            if (verifyChecksum(jarFile, expectedChecksum)) {
              s.log.info("JAR downloaded and checksum verified successfully.")
              break
            } else {
              s.log.warn("Checksum verification failed, retrying...")
              jarFile.delete()
            }
          } catch {
            case ex: Exception =>
              s.log.error(s"Failed to download the file: ${ex.getMessage}")
              if (attempt == maxRetries) {
                throw new IllegalStateException("Max retries reached, unable to download the file.")
              }
          }
        }
      }
    } else {
      s.log.info("JAR already present and checksum verified successfully.")
    }
  }

    def verifyChecksum(file: File, expectedChecksum: String): Boolean = {
      val buffer = new Array[Byte](64)
      val sha256 = MessageDigest.getInstance("SHA-256")
      val fis = new FileInputStream(file)

      Stream.continually(fis.read(buffer)).takeWhile(_ != -1).foreach { read =>
        sha256.update(buffer, 0, read)
      }
      fis.close()

      val fileChecksum = sha256.digest().map("%02x".format(_)).mkString
      fileChecksum.equalsIgnoreCase(expectedChecksum)
    }
}
