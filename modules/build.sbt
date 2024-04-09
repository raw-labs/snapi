import sbt.Keys._
import sbt._

import snapi.modules.build.BuildSettings._
import snapi.modules.build.Dependencies._

import scala.sys.process.Process

import com.jsuereth.sbtpgp.PgpKeys.{publishSigned}

ThisBuild / publish / skip := true

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
ThisBuild / sonatypeProfileName := "com.raw-labs"

lazy val snapiFrontend = (project in file("snapi-frontend"))
  .settings(
    commonSettings,
    snapiFrontendCompileSettings,
    testSettings,
    libraryDependencies ++= Seq(
      rawClient % "compile->compile;test->test",
      rawParsers % "compile->compile;test->test",
      commonsLang,
      commonsText,
      apacheHttpClient,
      icuDeps,
      woodstox,
      kiama,
      dropboxSDK,
      aws,
      jwtApi,
      jwtImpl,
      jwtCore,
      postgresqlDeps,
      mysqlDeps,
      mssqlDeps,
      snowflakeDeps,
      commonsCodec,
      springCore,
      kryo
    ) ++
      poiDeps
  )

lazy val snapiTruffle = (project in file("snapi-truffle"))
  .dependsOn(
    snapiFrontend % "compile->compile;test->test"
  )
  .enablePlugins(JavaAnnotationProcessorPlugin)
  .settings(
    commonSettings,
    snapiTruffleCompileSettings,
    testSettings,
    libraryDependencies ++= Seq(
      rawUtils % "compile->compile;test->test"
      ) ++ truffleCompiler ++ scalaCompiler,
  )

lazy val snapiClient = (project in file("snapi-client"))
  .dependsOn(
    snapiFrontend % "compile->compile;test->test",
    snapiTruffle % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    snapiClientCompileSettings,
    testSettings,
    libraryDependencies ++= Seq(
      rawClient % "compile->compile;test->test"
    )
  )
