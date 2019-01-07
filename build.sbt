import sbt.Keys._

import sbt._

name := "geo-fetch"

organization := "group.aging-research"

scalaVersion :=  "2.12.8"

version := "0.0.2"

coursierMaxIterations := 200

isSnapshot := false

scalacOptions ++= Seq( "-target:jvm-1.8", "-feature", "-language:_" )

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint", "-J-Xss5M", "-encoding", "UTF-8")

javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")

resourceDirectory in Test := baseDirectory { _ / "files" }.value

unmanagedClasspath in Compile ++= (unmanagedResources in Compile).value

resolvers += Resolver.bintrayRepo("comp-bio-aging", "main")

addCompilerPlugin(("org.scalamacros" %% "paradise" % "2.1.1").cross(CrossVersion.full))

lazy val circeVersion = "0.10.0"
lazy val kantanVersion = "0.5.0"

libraryDependencies ++= Seq(
 "org.typelevel" %% "cats-core" % "1.5.0",
 "com.monovore" %% "decline" % "0.6.0",
 "com.monovore" %% "decline-refined" % "0.6.0",
 "com.pepegar" %% "hammock-core" % "0.8.7", 
 "com.pepegar" %% "hammock-circe" % "0.8.7",
 "org.wvlet.airframe" %% "airframe-log" % "0.77",
 "com.github.pathikrit" %% "better-files" % "3.7.0",
 "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
 "com.lihaoyi" %% "pprint" % "0.5.3",
 "com.lihaoyi" %% "fastparse" % "2.1.0",
 "io.circe" %% "circe-generic-extras" % circeVersion,
 "io.circe" %% "circe-parser" % circeVersion,
 "com.lihaoyi" %% "requests" % "0.1.4",
 // Automatic type class instances derivation.
 "com.nrinaudo" %% "kantan.csv-java8" % kantanVersion,
 "com.nrinaudo" %% "kantan.csv-cats" % kantanVersion,
 "com.nrinaudo" %% "kantan.csv-generic" % kantanVersion,
 "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF")

exportJars := true

fork in run := true

parallelExecution in Test := false

bintrayRepository := "main"

bintrayOrganization := Some("comp-bio-aging")

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

maintainer in Docker := "Anton Kulaga <antonkulaga@gmail.com>"

maintainer := "Anton Kulaga <antonkulaga@gmail.com>"

dockerRepository := Some("quay.io/comp-bio-aging")

enablePlugins(JavaAppPackaging, DockerPlugin)



