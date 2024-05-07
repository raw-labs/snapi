package raw.build

import sbt.Keys._
import sbt.{Def, _}

import com.jsuereth.sbtpgp.PgpKeys.{publishSigned}
import sbt.plugins.JvmPlugin
import java.io.{File, IOException}
import java.net.URL

import sys.process._

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
      val basePath: String = javaSrcBasePath.value
      val parsers = parserDefinitions.value

      // Ensure antlr jar is available
      val antlrJarPath = (ThisBuild / baseDirectory).value / "antlr-4.12.0-complete.jar"
      ensureAntlrJarAvailable(antlrJarPath.toString())

      def deleteRecursively(file: File): Unit = {
        if (file.isDirectory) {
          file.listFiles.foreach(deleteRecursively)
        }
        if (file.exists && !file.delete()) {
          throw new IOException(s"Failed to delete ${file.getAbsolutePath}")
        }
      }

      val s: TaskStreams = streams.value
      parsers.foreach(parser => {
        val outputPath = parser._1
        val file = new File(outputPath)
        if (file.exists()) {
          deleteRecursively(file)
        }
        val packageName: String = parser._2
        val command: String = s"java -jar ${antlrJarPath} -visitor -package $packageName -o $outputPath"
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
  def ensureAntlrJarAvailable(jarPath: String): Unit = {
    val jarFile = new File(jarPath)
    if (!jarFile.exists()) {
      val jarName = jarFile.getName
      new URL(s"https://github.com/antlr/website-antlr4/raw/gh-pages/download/$jarName") #> new File(jarPath) !!
    }
  }
}
