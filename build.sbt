name := """play-getting-started"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "jitpack" at "https://jitpack.io"
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

scalaVersion := "2.11.8"
val akkaV       = "2.4.3"



val scalaTestV  = "2.2.6"
libraryDependencies ++= Seq(
  jdbc,
  cache,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-slf4j" % akkaV,
  "com.typesafe.akka" %% "akka-stream" % akkaV,
  "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
  //"com.github.Grover-c13" % "PokeGOAPI-Java" % "0.3" % "provided",
  "com.github.svarzee" % "gpsoauth-java" % "v0.3.0",
  "com.google.api-client" % "google-api-client" % "1.22.0",
  "com.corundumstudio.socketio" % "netty-socketio" % "1.7.11",
  "com.google.protobuf" % "protobuf-java" % "3.0.0",
  "com.squareup.okio" % "okio" % "1.9.0",
  "com.squareup.moshi" % "moshi" % "1.2.0",
  "com.annimon" % "stream" % "1.1.1",
  "com.squareup.okhttp3" % "okhttp" % "3.4.0-RC1",
  "io.reactivex" % "rxjava" % "1.1.8",
  "net.jpountz.lz4" % "lz4" % "1.3.0",
  "org.postgresql" % "postgresql" % "9.4.1209",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",

  "com.typesafe.akka" %% "akka-http-testkit" % akkaV,
  "net.databinder.dispatch"    %% "dispatch-core"          % "0.11.3" exclude("com.ning", "async-http-client"),
  "com.ning"                    % "async-http-client"      % "1.9.38",
  "net.databinder.dispatch"    %% "dispatch-json4s-native" % "0.11.3",
  "org.scalatest"     %% "scalatest" % scalaTestV % "test",
  ws
)

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _ )

assemblyMergeStrategy in assembly := {
  // Archivos en conflicto en las dependencia
  case PathList("META-INF", "org", "apache", "logging", "log4j", "core", "config", "plugins", "Log4j2Plugins.dat") =>
    MergeStrategy.discard
  case PathList("org", "apache", "commons", xs@_*) => MergeStrategy.first
  case PathList("com", "squareup", "okhttp3",  xs@_*) => MergeStrategy.first
  case PathList("javax", "ws", xs@_*) => MergeStrategy.first
  case PathList("javax", "mail", xs@_*) => MergeStrategy.first
  case PathList("javax", "inject", xs@_*) => MergeStrategy.first
  case PathList("javax", "annotation", xs@_*) => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

assemblyOutputPath in assembly := file("../DSCatcherApp.jar")

mainClass in assembly := Option("controllers.Application")
