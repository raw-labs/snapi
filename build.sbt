import java.nio.file.{Files, Path, Paths, StandardOpenOption}
import java.nio.file.attribute.PosixFilePermission
import collection.JavaConverters._

import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._

import sbt.Keys._
import sbt.Tests.{Group, SubProcess}
import sbt._

import java.io._
import java.util.jar._
import java.time.Year

import Dependencies._

publish / skip := true // don't publish the root project

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
sonatypeProfileName := "com.raw-labs"

val licenseHeader = s"""Copyright ${Year.now.getValue} RAW Labs S.A.

Use of this software is governed by the Business Source License
included in the file licenses/BSL.txt.

As of the Change Date specified in that file, in accordance with
the Business Source License, use of this software will be governed
by the Apache License, Version 2.0, included in the file
licenses/APL.txt."""

val scalacOptSettings = Seq(
  // See scalac -opt:help for possible values.
)

val truffleExports = Seq(
  "--add-exports",
  "java.base/jdk.internal.module=ALL-UNNAMED",
  "--add-exports",
  "org.graalvm.sdk/org.graalvm.polyglot=ALL-UNNAMED",
  "--add-exports",
  "org.graalvm.truffle/com.oracle.truffle.api=ALL-UNNAMED",
  "--add-exports",
  "org.graalvm.truffle/com.oracle.truffle.api.nodes=ALL-UNNAMED",
  "--add-exports",
  "org.graalvm.truffle/com.oracle.truffle.api.frame=ALL-UNNAMED",
  "--add-exports",
  "org.graalvm.truffle/com.oracle.truffle.api.source=ALL-UNNAMED",
  "--add-exports",
  "org.graalvm.truffle/com.oracle.truffle.api.object=ALL-UNNAMED",
  "--add-exports",
  "org.graalvm.truffle/com.oracle.truffle.api.library=ALL-UNNAMED",
  "--add-exports",
  "org.graalvm.truffle/com.oracle.truffle.api.dsl=ALL-UNNAMED",
  "--add-exports",
  "org.graalvm.truffle/com.oracle.truffle.api.instrumentation=ALL-UNNAMED",
  "--add-exports",
  "org.graalvm.truffle/com.oracle.truffle.api.exception=ALL-UNNAMED",
  "--add-exports",
  "org.graalvm.truffle/com.oracle.truffle.api.interop=ALL-UNNAMED"
)

headerLicense := Some(HeaderLicense.Custom(licenseHeader))

lazy val buildSettings = Seq(
  homepage := Some(url("https://www.raw-labs.com/")),
  organization := "com.raw-labs",
  organizationName := "RAW Labs SA",
  organizationHomepage := Some(url("https://www.raw-labs.com/")),
  name := baseDirectory.value.getName, // All modules are named after their directories
  developers := List(Developer("raw-labs", "RAW Labs","engineering@raw-labs.com", url("https://github.com/raw-labs"))),
  licenses := List(
  "Business Source License 1.1" -> new URL("https://raw.githubusercontent.com/raw-labs/snapi/main/licenses/BSL.txt")
  ),
  startYear := Some(2023),
  headerLicense := Some(HeaderLicense.Custom(licenseHeader)),
  scalaVersion := Dependencies.scalacVersion,
  // avoid including scala version in artifact name
  javacOptions ++= Seq(
    "-source",
    "11",
    "-target",
    "11"
  ),
  scalacOptions ++= scalacOptSettings ++ Seq(
    "-feature",
    "-unchecked",
    // When compiling in encrypted drives in Linux, the max size of a name is reduced to around 140.
    "-Xmax-classfile-name",
    "140",
    "-deprecation",
    "-Xlint:-stars-align,_",
    "-Ywarn-dead-code",
    // Fix for false warning of unused implicit arguments in traits/interfaces.
    "-Ywarn-macros:after",
    "-Ypatmat-exhaust-depth",
    "160"
  ),
  // Use cached resolution of dependencies
  updateOptions := updateOptions.in(Global).value.withCachedResolution(true),
  // Ensure Java annotations get compiled first, so that they are accessible from Scala.
  compileOrder := CompileOrder.JavaThenScala,
  // The tests are run in a forked JVM.
  // System properties given to sbt are not automatically passed to the forked VM
  // Here we copy any "raw." system properties to the java options passed to the forked JVMs.
  Test / fork := true,
  Test / parallelExecution := true,
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
  Test / javaOptions ++=
    truffleExports ++ Seq(
      // Increasing stack size for Kiama chain/rewrites and codegen.
      "-Xss64m",
      // Limit overall memory and force crashing hard and early.
      // Useful for debugging memleaks.
      "-Xmx4G",
      "-XX:+CrashOnOutOfMemoryError",
      // Required for running with Java 17
      "--add-opens",
      "java.base/java.lang=ALL-UNNAMED",
      "--add-opens",
      "java.base/java.util=ALL-UNNAMED",
      "--add-opens",
      "java.base/java.nio=ALL-UNNAMED",
      "--add-opens",
      "java.base/java.lang.invoke=ALL-UNNAMED",
      "--add-opens",
      "java.base/sun.nio.ch=ALL-UNNAMED",
      "--add-opens",
      "java.base/sun.util.calendar=ALL-UNNAMED",
      // Truffle test settings.
//      "-Dpolyglot.engine.Inlining=false",
      "-Dpolyglot.engine.CompileImmediately=true",
      "-Dpolyglot.engine.AllowExperimentalOptions=true",
      "-Dgraal.Dump=Truffle:2",
      "-Dgraal.DumpPath=/tmp/graal_dumps",
      "-Dgraal.PrintGraph=Network",
      "-Dgraal.CompilationFailureAction=Diagnose",
      "-Dgraalvm.locatorDisabled=true",
      "-Dpolyglot.engine.BackgroundCompilation=false",
      "-Dpolyglot.engine.TraceCompilation=true",
      "-Dpolyglot.engine.TraceCompilationDetails=true",
      "-Dpolyglot.engine.TraceInlining=true"
    ),
  // Group tests by the scala package of the test
  Test / testGrouping := {
    // This is called for each sbt project
    val tests = (Test / definedTests).value
    val projectName = name.value
    var javaOptionsTest = (Test / javaOptions).value
    val logsRootDir = Paths.get(sys.env.getOrElse("SBT_FORK_OUTPUT_DIR", "target/test-results"))
    val executorLogsDir = logsRootDir.resolve("executor-logs")
    val heapDumpsRoot = logsRootDir.resolve("heap-dumps")
    // Generating a random directory in order the case when tests run in parallel
    for (file <- Seq(executorLogsDir, heapDumpsRoot)) {
      Files.createDirectories(file)
    }
    javaOptionsTest = javaOptionsTest ++ Seq(
      "-XX:+HeapDumpOnOutOfMemoryError",
      s"-XX:HeapDumpPath=$heapDumpsRoot"
    )

    var outputLogsDir: Option[java.nio.file.Path] = None
    val currentLogFileName: (String, String) => String = (a, b) => s"$a-$b.log"

    def resolvePath(
        executorLogsDir: Path,
        logFileName: String
    ): Path = {
      executorLogsDir.resolve(logFileName)
    }

    val testExecutionLogGroups = resolvePath(executorLogsDir, "test-groups.list")
    val testActor = "executor"

    // This is run for each sbt project
    tests
      .grouped(10)
      .zipWithIndex
      .map {
        case (testGroup, i) => {
          val testGroupName = s"$projectName-testgroup$i"
          val names = testGroup.map(td => td.name).mkString(s"Group: $testGroupName\n", "\n", "\n\n")

          Files.write(testExecutionLogGroups, names.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE)

          val outputFile = resolvePath(
            executorLogsDir,
            currentLogFileName.apply(testActor, testGroupName)
          )
          println(s"Writing output of test group $testGroupName to $outputFile")
          Files.deleteIfExists(outputFile)
          val options = ForkOptions()
            .withRunJVMOptions(javaOptionsTest.to)
            .withOutputStrategy(Some(CustomOutput(new BufferedOutputStream(Files.newOutputStream(outputFile)))))
          Group(testGroupName, testGroup, SubProcess(options))
        }
      }
      .toSeq
  },
  // Enable running multiple test VMs in parallel.
  concurrentRestrictions in Global := Seq(
    Tags.limit(
      Tags.ForkedTestGroup,
      sys.env
        .get("UNIT_TESTS_PARALLELISM")
        .map(_.toInt)
        .getOrElse(Math.max(java.lang.Runtime.getRuntime.availableProcessors - 2, 1))
    ),
    Tags.limit(Tags.CPU, java.lang.Runtime.getRuntime.availableProcessors),
    Tags.limit(Tags.Network, 10),
    Tags.limit(
      Tags.Test,
      sys.env
        .get("UNIT_TESTS_PARALLELISM")
        .map(_.toInt)
        .getOrElse(Math.max(java.lang.Runtime.getRuntime.availableProcessors - 2, 1))
    ),
    Tags.limitAll(
      sys.env.get("SBT_MAX_PARALLELISM").map(_.toInt).getOrElse(java.lang.Runtime.getRuntime.availableProcessors)
    ),
    Tags.limit(Tags.Publish, 1)
  ),
  // Add dependency resolvers
  resolvers += Resolver.mavenLocal,
  resolvers += Resolver.sonatypeRepo("releases"),
  // Publish settings
  publish / skip := false,
  Test / publishArtifact := true
)

lazy val strictBuildSettings = buildSettings ++ Seq(
  scalacOptions ++= Seq(
    "-Xfatal-warnings"
  )
)

lazy val strictBuildExceptDeprecationsSettings = buildSettings ++ Seq(
  scalacOptions ++= Seq(
    "-Wconf:cat=deprecation:ws,any:e"
  )
)

lazy val temporaryDirectory: String = {
  // Create temp files on dast directories by default.
  val fastTmp = Paths.get("/mnt/jenkins/tmp")
  if (Files.isDirectory(fastTmp) && Files.isWritable(fastTmp)) {
    fastTmp.toAbsolutePath.toString
  } else {
    System.getProperty("java.io.tmpdir")
  }
}

lazy val rawUtils = (project in file("raw-utils"))
  .settings(
    strictBuildSettings,
    libraryDependencies ++= Seq(
      scalaLogging,
      logbackClassic,
      guava,
      scalaJava8Compat,
      typesafeConfig,
      loki4jAppender,
      jmxPrometheusAgent,
      commonsIO,
      commonsLang,
      apacheHttpClient,
      scalatest % Test
    ) ++
      slf4j ++
      jacksonDeps
  )

lazy val rawAuthApi = (project in file("raw-auth-api"))
  .dependsOn(rawUtils % "compile->compile;test->test")
  .settings(strictBuildSettings)

lazy val rawInferrerApi = (project in file("raw-inferrer-api"))
  .dependsOn(
    rawUtils % "compile->compile;test->test",
    rawSourcesApi % "compile->compile;test->test",
    rawRuntimeApi % "compile->compile;test->test"
  )
  .settings(strictBuildSettings)

lazy val rawInferrerLocal = (project in file("raw-inferrer-local"))
  .dependsOn(
    rawInferrerApi % "compile->compile;test->test",
    rawSourcesS3 % "compile->compile;test->test",
    rawSourcesLocal % "test->test"
  )
  .settings(strictBuildSettings, libraryDependencies ++= Seq(icuDeps, woodstox) ++ poiDeps ++ kiama)

lazy val rawCredsApi = (project in file("raw-creds-api"))
  .dependsOn(rawUtils % "compile->compile;test->test")
  .settings(strictBuildSettings)

lazy val rawSourcesApi = (project in file("raw-sources"))
  .dependsOn(
    rawUtils % "compile->compile;test->test",
    rawCredsApi % "compile->compile;test->test"
  )
  .settings(strictBuildSettings, libraryDependencies += ehCache)

lazy val rawSourcesLocal = (project in file("raw-sources-local"))
  .dependsOn(rawSourcesApi % "compile->compile;test->test")
  .settings(strictBuildSettings)

lazy val rawSourcesMock = (project in file("raw-sources-mock"))
  .dependsOn(rawSourcesApi % "compile->compile;test->test")
  .settings(strictBuildSettings)

lazy val rawSourcesDropbox = (project in file("raw-sources-dropbox"))
  .dependsOn(rawSourcesApi % "compile->compile;test->test")
  .settings(
    strictBuildSettings,
    libraryDependencies ++= Seq(dropboxSDK)
  )

lazy val rawSourcesS3 = (project in file("raw-sources-s3"))
  .dependsOn(rawSourcesApi % "compile->compile;test->test")
  .settings(
    strictBuildSettings,
    libraryDependencies += aws
  )

lazy val rawSourcesGithub = (project in file("raw-sources-github"))
  .dependsOn(
    rawSourcesApi % "compile->compile;test->test",
    rawSourcesHttp % "compile->compile;test->test"
  )
  .settings(strictBuildSettings)

lazy val rawSourcesHttp = (project in file("raw-sources-http"))
  .dependsOn(rawSourcesApi % "compile->compile;test->test")
  .settings(
    strictBuildExceptDeprecationsSettings,
    libraryDependencies ++= Seq(jwtApi, jwtImpl, jwtJackson)
  )

lazy val rawSourcesPgsql = (project in file("raw-sources-pgsql"))
  .dependsOn(rawSourcesApi % "compile->compile;test->test")
  .settings(
    strictBuildSettings,
    libraryDependencies ++= Seq(postgresqlDeps)
  )

lazy val rawSourcesMysql = (project in file("raw-sources-mysql"))
  .dependsOn(rawSourcesApi % "compile->compile;test->test")
  .settings(
    strictBuildSettings,
    libraryDependencies ++= Seq(mysqlDeps)
  )

lazy val rawSourcesMsSQL = (project in file("raw-sources-mssql"))
  .dependsOn(rawSourcesApi % "compile->compile;test->test")
  .settings(
    strictBuildSettings,
    libraryDependencies ++= Seq(mssqlDeps)
  )

lazy val rawSourcesSnowflake = (project in file("raw-sources-snowflake"))
  .dependsOn(rawSourcesApi % "compile->compile;test->test")
  .settings(
    strictBuildSettings,
    libraryDependencies ++= Seq(snowflakeDeps)
  )

lazy val rawSourcesSqlite = (project in file("raw-sources-sqlite"))
  .dependsOn(rawSourcesApi % "compile->compile;test->test")
  .settings(
    strictBuildSettings,
    libraryDependencies ++= Seq(sqliteDeps)
  )

lazy val rawRuntimeApi = (project in file("raw-runtime-api"))
  .dependsOn(
    rawSourcesApi % "compile->compile;test->test"
  )
  .settings(strictBuildSettings)

lazy val rawCompilerApi = (project in file("raw-compiler"))
  .dependsOn(
    rawInferrerApi % "compile->compile;test->test",
    rawAuthApi % "compile->compile;test->test",
    rawSourcesApi % "compile->compile;test->test",
    rawRuntimeApi % "compile->compile;test->test"
  )
  .settings(
    strictBuildSettings,
    libraryDependencies ++= kiama ++ scalaCompiler ++ Seq(commonsCodec)
  )

lazy val rawCompilerCommon = (project in file("raw-compiler-common"))
  .dependsOn(
    rawCompilerApi % "compile->compile;test->test"
  )
  .settings(
    strictBuildSettings
  )

lazy val rawCompilerRql2 = (project in file("raw-compiler-rql2"))
  .dependsOn(
    rawCompilerCommon % "compile->compile;test->test",
    rawInferrerLocal % "test->test",
    rawSourcesDropbox % "test->test",
    rawSourcesHttp % "test->test",
    rawSourcesLocal % "test->test",
    rawSourcesMysql % "test->test",
    rawSourcesPgsql % "test->test",
    rawSourcesS3 % "test->test",
    rawSourcesSqlite % "test->test",
    rawSourcesMsSQL % "test->test",
    rawSourcesSnowflake % "test->test",
    rawSourcesMock % "test->test",
    rawSourcesGithub % "test->test"
  )
  .settings(
    buildSettings // TODO (msb): Promote this to strictBuildSettings and add bail-out annotations as needed,
  )

val dummyJavadocJarTask = taskKey[File]("Creates a dummy javadoc jar file")

lazy val rawRuntimeRql2Truffle = (project in file("raw-runtime-rql2-truffle"))
  .dependsOn(
    rawCompilerRql2 % "compile->compile;test->test"
  )
  .settings(
    buildSettings, // TODO (msb): Promote this to strictBuildSettings and add bail-out annotations as needed,
    // Do not generate scala docs for this project.
    Compile / doc / sources := Seq.empty,
    libraryDependencies ++= truffleDeps ++ Seq(kryo, woodstox, commonsText) ++ poiDeps
  )
  .settings(
    name := "raw-runtime-rql2-truffle",
    dummyJavadocJarTask := {
      val artifactName = "raw-runtime-rql2-truffle"
      val projectVersion = version.value
      val targetDir = (Compile / target).value

      targetDir.mkdirs()  // ensure directory exists

      val jarFile = targetDir / s"$artifactName-$projectVersion-javadoc.jar"

      val readme = new File(targetDir, "README")
      if (!readme.exists()) {
        readme.createNewFile()
      }

      val writer = new PrintWriter(readme)
      writer.write("This is a placeholder for javadoc.")
      writer.close()

      val jar = new JarOutputStream(new FileOutputStream(jarFile))
      val entry = new JarEntry("README")
      jar.putNextEntry(entry)

      val in = new FileInputStream(readme)
      val buffer = new Array[Byte](1024)
      var n = in.read(buffer)
      while(n != -1) {
        jar.write(buffer, 0, n)
        n = in.read(buffer)
      }
      in.close()

      jar.closeEntry()
      jar.close()

      jarFile
    },
    Compile / packageDoc := dummyJavadocJarTask.value
  )

lazy val rawCompilerRql2Truffle = (project in file("raw-compiler-rql2-truffle"))
  .dependsOn(
    rawCompilerRql2 % "compile->compile;test->test",
    rawRuntimeRql2Truffle % "compile->compile;test->test;provided->provided"
  )
  .settings(
    buildSettings, // TODO (msb): Promote this to strictBuildSettings and add bail-out annotations as needed,
    libraryDependencies += awsSdk,
    Test / javaOptions ++= Seq(
      s"-Xlog:gc*,thread*:file=$temporaryDirectory/raw-compiler-rql2-truffle-tests-%t-%p.log", // GC logging.
      s"-Djava.io.tmpdir=$temporaryDirectory"
    )
  )

lazy val rawCli = (project in file("raw-cli"))
  .dependsOn(
    rawCompilerRql2Truffle,
    rawInferrerLocal,
    rawSourcesLocal,
    rawSourcesHttp,
    rawSourcesDropbox,
    rawSourcesMysql,
    rawSourcesPgsql,
    rawSourcesS3,
    rawSourcesSqlite,
    rawSourcesMsSQL,
    rawSourcesSnowflake,
    rawSourcesGithub
  )
  .settings(
    buildSettings,
    publish / skip := false,
    libraryDependencies ++= jline,
    assembly / mainClass := Some("raw.cli.RawCli"),
    assembly / assemblyMergeStrategy := {
      //case "module-info.class" => MergeStrategy.discard
      case PathList("jakarta", "activation", xs @ _*) => MergeStrategy.first
      //case PathList("META-INF", _*) => MergeStrategy.discard
      case PathList("META-INF", "io.netty.versions.properties", xs @ _*) => MergeStrategy.first
      case PathList("META-INF", "versions", "9", "module-info.class", xs @ _*) => MergeStrategy.first
      case PathList("META-INF", "versions", "11", "module-info.class", xs @ _*) => MergeStrategy.first
      case PathList("META-INF", "mailcap.default", xs @ _*) => MergeStrategy.first
      case PathList("META-INF", "mimetypes.default", xs @ _*) => MergeStrategy.first
      case PathList("mozilla", "public-suffix-list.txt", xs @ _*) => MergeStrategy.first
      case "module-info.class" => MergeStrategy.first
      case "mime.types" => MergeStrategy.first
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    },
    excludeDependencies += "commons-logging" % "commons-logging"
  )

val buildSnapi = taskKey[Unit]("Builds the snapi script.")

buildSnapi := {
  println("Starting build. It will take a few minutes to complete...")

  val assemblyOutput: File = (rawCli / Compile / assembly).value

  val path = Paths.get("snapi")
  val script = s"""#!/bin/bash
    |java ${truffleExports
    .mkString(" ")} -Dgraalvm.locatorDisabled=true -jar ${assemblyOutput.getPath} $$@
    |""".stripMargin

  Files.write(path, script.getBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

  val perms = Files.getPosixFilePermissions(path)
  val newPerms = perms.asScala + PosixFilePermission.OWNER_EXECUTE
  Files.setPosixFilePermissions(path, newPerms.asJava)

  println(s"Build complete. Run. You can use the script at: ${path.toAbsolutePath}")
}
