scalaVersion := "2.10.6"
// scalaVersion := "2.12.2"

PB.targets in Compile := Seq(
  PB.gens.java -> (sourceManaged in Compile).value,
  scalapb.gen(javaConversions = true) -> (sourceManaged in Compile).value
)

// addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.8")
// libraryDependencies += "com.trueaccord.scalapb" %% "compilerplugin" % "0.6.0-pre5"

// val scalazVersion = "7.2.11"

// PB.targets in Compile := Seq(
//   scalapb.gen() -> (sourceManaged in Compile).value
// )

// libraryDependencies += "com.mdialog" %% "scala-zeromq" % "1.2.0"
resolvers += "Sonatype (releases)" at "https://oss.sonatype.org/content/repositories/releases/"
libraryDependencies += "org.zeromq" %% "zeromq-scala-binding" % "0.0.6"
libraryDependencies ++= Seq(
  "io.netty" % "netty" % "3.7.0.Final",
  "org.rogach" %% "scallop" % "3.0.3",
  "com.github.scopt" %% "scopt" % "3.6.0"
)
// libraryDependencies += "org.rogach" %% "scallop" % "3.0.3"

// libraryDependencies += "org.zeromq" %% "jeromq" % "0.4.0"
// addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.8")
// libraryDependencies += "com.trueaccord.scalapb" %% "compilerplugin" % "0.6.0-pre4"
