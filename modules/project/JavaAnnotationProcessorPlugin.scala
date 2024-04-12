package snapi.modules.build

import sbt._
import sbt.Keys._
import scala.sys.process.Process

import com.jsuereth.sbtpgp.PgpKeys.{publishSigned}

object JavaAnnotationProcessorPlugin extends AutoPlugin {
  // Task keys
  val calculateSnapiTruffleClasspath = taskKey[Seq[File]]("Calculate the full classpath")
  val runJavaAnnotationProcessor = taskKey[Unit]("Runs the Java annotation processor")
  val annotationProcessors = settingKey[String]("Annotation processors")

  // Automatically enable this plugin (set to noTrigger if you want to enable it manually)
  override def trigger = noTrigger

  // Provide default settings
  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    annotationProcessors := Seq(
      "com.oracle.truffle.dsl.processor.TruffleProcessor",
      "com.oracle.truffle.dsl.processor.verify.VerifyTruffleProcessor",
      "com.oracle.truffle.dsl.processor.LanguageRegistrationProcessor",
      "com.oracle.truffle.dsl.processor.InstrumentRegistrationProcessor",
      "com.oracle.truffle.dsl.processor.OptionalResourceRegistrationProcessor",
      "com.oracle.truffle.dsl.processor.InstrumentableProcessor",
      "com.oracle.truffle.dsl.processor.verify.VerifyCompilationFinalProcessor",
      "com.oracle.truffle.dsl.processor.OptionProcessor"
    ).mkString(","),

    calculateSnapiTruffleClasspath := {
      val dependencyFiles = (Compile / dependencyClasspath).value.files
      val unmanagedFiles = (Compile / unmanagedClasspath).value.files
      val classesDir = (Compile / classDirectory).value
      dependencyFiles ++ unmanagedFiles ++ Seq(classesDir)
    },

    runJavaAnnotationProcessor := {
      val log = streams.value.log
      log.info("Running Java annotation processor")
      val annotationProcessorJar = baseDirectory.value / "truffle-dsl-processor-23.1.0.jar"
      val javaSources = baseDirectory.value / "src" / "main" / "java"
      val targetDir = baseDirectory.value / "target" / "java-processed-sources"
      val projectClasspath = calculateSnapiTruffleClasspath.value.mkString(":")
      val javacOptions = Seq(
        "javac",
        "-source",
        "21",
        "-target",
        "21",
        "-d",
        targetDir.getAbsolutePath,
        "--module-path",
        projectClasspath,
        "-cp",
        annotationProcessorJar.getAbsolutePath,
        "-processor",
        annotationProcessors.value,
        "-proc:only"
      ) ++ (javaSources ** "*.java").get.map(_.absolutePath)
      targetDir.mkdirs()
      val result = Process(javacOptions).!
      if (result != 0) {
        throw new RuntimeException("Java annotation processing failed.")
      }
    },
    Compile / compile := {
      (Compile/ runJavaAnnotationProcessor).value
      (Compile / compile).value
    }
    Test / compile := {
      (Test / runJavaAnnotationProcessor).value
      (Test / compile).value
    }
  )
}
