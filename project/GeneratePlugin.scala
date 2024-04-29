package raw.build

import sbt._
import sbt.Keys._
import scala.sys.process._

import java.io.IOException

import com.jsuereth.sbtpgp.PgpKeys.{publishSigned}

object Generate extends AutoPlugin {
  // Task keys
  val generateParser = taskKey[Unit]("Generated antlr4 base parser and lexer")

  // Automatically enable this plugin (set to noTrigger if you want to enable it manually)
  override def trigger = noTrigger

  // Provide default settings
  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    generateParser := {

      // List of output paths
      val basePath: String = s"${baseDirectory.value}/src/main/java"
      val parsers = List(
        (s"${basePath}/raw/compiler/rql2/generated", "raw.compiler.rql2.generated", s"$basePath/raw/snapi/grammar", "Snapi"),
      )

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

        val jarName = "antlr-4.12.0-complete.jar"

        val command: String = s"java -jar $basePath/antlr4/$jarName -visitor -package $packageName -o $outputPath"

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
    }
  )
}
