import AssemblyKeys._

assemblySettings

jarName in assembly := "project1.jar"

mainClass in assembly := Some("project1.Main")

name := "project1"

version := "1.0"

resolvers ++= Seq(
  "RoundEights" at "http://maven.spikemark.net/roundeights"
  ,Resolver.sonatypeRepo("public")
)

libraryDependencies ++= Seq(
  "com.roundeights" %% "hasher" % "1.0.0"
  ,"commons-codec" % "commons-codec" % "1.7"
  ,"com.typesafe.akka" %% "akka-remote" % "2.3.5"
  ,"com.typesafe.akka" %% "akka-slf4j" % "2.3.5"
  ,"com.github.scopt" %% "scopt" % "3.2.0"
  ,"ch.qos.logback" % "logback-classic" % "1.1.2"
)
    