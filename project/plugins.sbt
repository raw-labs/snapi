resolvers += Classpaths.sbtPluginReleases

autoCompilerPlugins := true

addDependencyTreePlugin

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.8")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.1")

addSbtPlugin("nl.gn0s1s" % "sbt-dotenv" % "3.0.0")

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.10.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.1.1")

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.12")

addSbtPlugin("com.github.sbt" % "sbt-dynver" % "5.0.1")

libraryDependencies += "commons-io" % "commons-io" % "2.11.0"
