enablePlugins(JavaAppPackaging)

name := "PokeMapBackend"
organization := "com.theiterators"
version := "1.0"
scalaVersion := "2.11.8"


scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "io"           at "https://jitpack.io"
)


libraryDependencies ++= {
  val akkaV       = "2.4.3"
  val scalaTestV  = "2.2.6"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
    "com.github.Grover-c13" %% "PokeGOAPI-Java" % "master-SNAPSHOT",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaV,
    "net.databinder.dispatch"    %% "dispatch-core"          % "0.11.3" exclude("com.ning", "async-http-client"),
    "com.ning"                    % "async-http-client"      % "1.9.38",
    "net.databinder.dispatch"    %% "dispatch-json4s-native" % "0.11.3",
    "org.scalatest"     %% "scalatest" % scalaTestV % "test"
  )
}

Revolver.settings
