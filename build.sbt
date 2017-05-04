name := """PokeDex"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

//libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test

val reactiveMongoVer = "0.11.14"

libraryDependencies ++= Seq(
  ws,
  "org.reactivemongo" %% "play2-reactivemongo" % reactiveMongoVer,
  "org.mindrot" % "jbcrypt" % "0.3m"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
