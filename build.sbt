
name := """pokemapbackend"""
version := "1.0"
version := "1.0"
scalaVersion := "2.11.8"

lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging)


scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies ++= {
  val akkaV       = "2.4.3"
  val scalaTestV  = "2.2.6"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
    //"com.github.Grover-c13" % "PokeGOAPI-Java" % "0.3",
    "com.github.svarzee" % "gpsoauth-java" % "v0.3.0",
    "com.squareup.okio" % "okio" % "1.9.0",
    "com.squareup.moshi" % "moshi" % "1.2.0",
    "com.annimon" % "stream" % "1.1.1",
    //"com.squareup.okhttp3" % "okhttp" % "3.4.1",
    "com.squareup.okhttp3" % "okhttp" % "3.4.0-RC1",
    "com.google.protobuf" % "protobuf-java" % "3.0.0-beta-3",
    "io.reactivex" % "rxjava" % "1.1.8",
    "net.jpountz.lz4" % "lz4" % "1.3.0",
    "com.ning" % "async-http-client" % "1.9.38",
    "com.google.api-client" % "google-api-client" % "1.22.0",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaV,
    "net.databinder.dispatch"    %% "dispatch-core"          % "0.11.3" exclude("com.ning", "async-http-client"),
    "com.ning"                    % "async-http-client"      % "1.9.38",
    "net.databinder.dispatch"    %% "dispatch-json4s-native" % "0.11.3",
    "org.scalatest"     %% "scalatest" % scalaTestV % "test"
  )
}

//Revolver.settings
