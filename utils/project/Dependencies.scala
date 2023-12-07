import sbt._

object Dependencies {

  val scalatest = "org.scalatest" %% "scalatest" % "3.2.16"

  val slf4j = Seq(
    "org.slf4j" % "slf4j-api" % "2.0.5",
    "org.slf4j" % "log4j-over-slf4j" % "2.0.5",
    "org.slf4j" % "jcl-over-slf4j" % "2.0.5",
    "org.slf4j" % "jul-to-slf4j" % "2.0.5"
  )

  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5-rawlabs"

  val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.4.11"

  val loki4jAppender = "com.github.loki4j" % "loki-logback-appender" % "1.4.2"

  val commonsIO = "commons-io" % "commons-io" % "2.11.0"
  val commonsText = "org.apache.commons" % "commons-text" % "1.11.0"

  val commonsCodec = "commons-codec" % "commons-codec" % "1.16.0"

  val guava = "com.google.guava" % "guava" % "32.1.2-jre"

  val jacksonDeps = Seq(
    "com.fasterxml.jackson.core" % "jackson-core" % "2.15.2",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.15.2",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.15.2",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.15.2",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.15.2",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % "2.15.2",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % "2.15.2",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.15.2",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2-rawlabs",
    "com.fasterxml.jackson.jakarta.rs" % "jackson-jakarta-rs-json-provider" % "2.15.2"
  )

  // Required while we are on Scala 2.12. It's built into Scala 2.13.
  val scalaJava8Compat = "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2"

  val typesafeConfig = "com.typesafe" % "config" % "1.4.2"

}
