resolvers += Classpaths.sbtPluginReleases

autoCompilerPlugins := true

addDependencyTreePlugin

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

addSbtPlugin("nl.gn0s1s" % "sbt-dotenv" % "3.0.0")

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.10.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.1.3")

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.12")

addSbtPlugin("com.github.sbt" % "sbt-dynver" % "5.0.1")

libraryDependencies += "commons-io" % "commons-io" % "2.11.0"
libraryDependencies += "org.antlr" % "antlr-complete" % "4.12.0" from "https://github.com/antlr/website-antlr4/raw/gh-pages/download/antlr-4.12.0-complete.jar"

addSbtPlugin("com.lightbend.sbt" % "sbt-java-formatter" % "0.8.0")
