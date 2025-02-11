package raw.build

import sbt._

object Dependencies {

  val utilsCore = "com.raw-labs" %% "utils-core" % "0.50.0"

  val utilsSources = "com.raw-labs" %% "utils-sources" % "0.51.1"

  val protocolCompiler = "com.raw-labs" %% "protocol-compiler" % "0.55.0"

  val commonsText = "org.apache.commons" % "commons-text" % "1.11.0"

  val commonsCodec = "commons-codec" % "commons-codec" % "1.16.0"

  val jacksonDeps = Seq(
    "com.fasterxml.jackson.core" % "jackson-core" % "2.15.2",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.15.2",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.15.2",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.15.2",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.15.2",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % "2.15.2",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % "2.15.2",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.15.2",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2"
  )

  val trufflePolyglot = "org.graalvm.polyglot" % "polyglot" % "23.1.0"

  val antlr4Runtime = "org.antlr" % "antlr4-runtime" % "4.12.0"

  val kiama = "com.raw-labs" %% "kiama" % "2.5.1-2"

  val woodstox = "com.fasterxml.woodstox" % "woodstox-core" % "6.5.1"

  val kryo = "com.esotericsoftware" % "kryo" % "5.5.0"

  val commonsLang = "org.apache.commons" % "commons-lang3" % "3.13.0"

  val icuDeps = "com.ibm.icu" % "icu4j" % "73.2"

  val truffleCompiler = Seq(
    "org.graalvm.truffle" % "truffle-api" % "23.1.0",
    "org.graalvm.truffle" % "truffle-compiler" % "23.1.0",
    "org.graalvm.truffle" % "truffle-nfi" % "23.1.0",
    "org.graalvm.truffle" % "truffle-nfi-libffi" % "23.1.0",
    "org.graalvm.truffle" % "truffle-runtime" % "23.1.0"
  )

}
