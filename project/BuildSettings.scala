package raw.build

import sbt.Keys._
import sbt._

import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._
import java.time.Year
import com.jsuereth.sbtpgp.PgpKeys.{publishSigned}

object BuildSettings {
  lazy val commonSettings = Seq(
    name := "raw-" + baseDirectory.value.getName,
    homepage := Some(url("https://www.raw-labs.com/")),
    organization := "com.raw-labs",
    organizationName := "RAW Labs SA",
    startYear := Some(2023),
    organizationHomepage := Some(url("https://www.raw-labs.com/")),
    developers := List(Developer("raw-labs", "RAW Labs", "engineering@raw-labs.com", url("https://github.com/raw-labs"))),
    licenses := List(
      "Business Source License 1.1" -> new URI(
        "https://raw.githubusercontent.com/raw-labs/snapi/main/licenses/BSL.txt"
      ).toURL
    ),
    headerSources / excludeFilter := HiddenFileFilter,
    resolvers += Resolver.mavenLocal,
    resolvers ++= Resolver.sonatypeOssRepos("snapshots"),
    resolvers ++= Resolver.sonatypeOssRepos("releases"),
    updateOptions := updateOptions.in(Global).value.withCachedResolution(true),
    publish / skip := false,
    publishSigned / skip  := false,
    publishLocal / skip := false,
    publishTo := Some("GitHub raw-labs Apache Maven Packages" at "https://maven.pkg.github.com/raw-labs/snapi"),
    publishMavenStyle := true
  )

  lazy val commonCompileSettings = Seq(
    javacOptions ++= Seq(
      "-source",
      "21",
      "-target",
      "21"
    ),
    // Add all the classpath to the module path.
    Compile / javacOptions ++= Seq(
      "--module-path",
      (Compile / dependencyClasspath).value.files.absString
    ),
    Compile / doc / sources := {
      (Compile / doc / sources).value.filterNot(_.getName.endsWith(".java"))
    },
    Compile / packageSrc / publishArtifact := true
  )

  lazy val scalaCompileSettings = commonCompileSettings ++ Seq(
    scalaVersion := "2.12.18",
    scalacOptions ++= Seq(
      "-feature",
      "-unchecked",
      "-Xmax-classfile-name",
      "140",
      "-deprecation",
      "-Xlint:-stars-align,_",
      "-Ywarn-dead-code",
      "-Ywarn-macros:after",
      "-Ypatmat-exhaust-depth",
      "160",
      "-Xfatal-warnings"
    ),
    compileOrder := CompileOrder.ScalaThenJava,
  )

  lazy val nonStrictScalaCompileSettings = scalaCompileSettings ++ Seq(
    scalacOptions := {
      val baseOptions = scalacOptions.value
      val updatedOptions = baseOptions.map {
        case "-Xfatal-warnings" => ""
        case other => other
      }
      updatedOptions
    },
  )

  lazy val missingInterpolatorCompileSettings = scalaCompileSettings ++ Seq(
    scalacOptions := {
      val baseOptions = scalacOptions.value
      val updatedOptions = baseOptions.map {
        case "-Xlint:-stars-align,_" => "-Xlint:-stars-align,-missing-interpolator,_"
        case other => other
      }
      updatedOptions
    },
  )


  lazy val snapiTruffleCompileSettings = scalaCompileSettings ++ Seq(
    compileOrder := CompileOrder.JavaThenScala,
    Compile / unmanagedSourceDirectories += baseDirectory.value / "target" / "java-processed-sources",
    Compile / unmanagedResourceDirectories += baseDirectory.value / "target" / "java-processed-sources" / "META-INF",
    Compile / resourceDirectories += baseDirectory.value / "target" / "java-processed-sources" / "META-INF"
  )

  lazy val testSettings = Seq(
    Test / doc / sources := {
      (Compile / doc / sources).value.filterNot(_.getName.endsWith(".java"))
    },
    // The tests are run in a forked JVM.
    // System properties given to sbt are not automatically passed to the forked VM
    // Here we copy any "raw." system properties to the java options passed to the forked JVMs.
    Test / fork := true,
    Test / javaOptions ++= {
      import scala.collection.JavaConverters._
      val props = System.getProperties
      props
        .stringPropertyNames()
        .asScala
        .filter(p => p.startsWith("raw."))
        .map(key => s"-D$key=${props.getProperty(key)}")
        .to
    },
    Test / javaOptions ++= Seq(
      // Increasing stack size for Kiama chain/rewrites and codegen.
      "-Xss64m",
      // Enable assertions.
      //  "-ea",
      // Limit overall memory and force crashing hard and early.
      // Useful for debugging memleaks.
      "-Xmx8G",
      "-XX:+CrashOnOutOfMemoryError",
      "-Dpolyglotimpl.CompilationFailureAction=Throw"
    ),
    // Publish settings
    Test / publishArtifact := true,
    // Useful for debugging
    Test / packageSrc / publishArtifact := true
  )
}
