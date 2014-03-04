name := "scala-react-line-drawing"

version := "1.0"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.2")

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.10.3"

autoCompilerPlugins := true

addCompilerPlugin("org.scala-lang.plugins" % "continuations" % "2.10.3")

scalacOptions += "-P:continuations:enable"
