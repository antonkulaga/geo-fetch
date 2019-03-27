import sbt.Keys._

import sbt._

import com.typesafe.sbt.packager.docker.{Cmd, DockerChmodType}

name := "geo-fetch"

organization := "group.aging-research"

scalaVersion :=  "2.12.8"

version := "0.0.3"

coursierMaxIterations := 200

isSnapshot := false

javacOptions ++= Seq("-Xlint", "-J-Xss5M", "-encoding", "UTF-8")

javaOptions ++= Seq("-Xms512M", "-Xmx4096M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")

resourceDirectory in Test := baseDirectory { _ / "files" }.value

unmanagedClasspath in Compile ++= (unmanagedResources in Compile).value

resolvers += Resolver.bintrayRepo("comp-bio-aging", "main")

addCompilerPlugin(("org.scalamacros" %% "paradise" % "2.1.1").cross(CrossVersion.full))

lazy val hammockVersion = "0.9.0"

lazy val circeVersion = "0.11.1"

lazy val kantanVersion = "0.5.0"

libraryDependencies ++= Seq(
 "org.typelevel" %% "cats-core" % "1.6.0",
 "com.monovore" %% "decline" % "0.6.0",
 "com.monovore" %% "decline-refined" % "0.6.0",
 "com.pepegar" %% "hammock-apache-http" % hammockVersion,
 "com.pepegar" %% "hammock-circe" % hammockVersion,
 "org.wvlet.airframe" %% "airframe-log" % "19.2.1",
 "com.github.pathikrit" %% "better-files" % "3.7.1",
 "org.scala-lang.modules" %% "scala-xml" % "1.1.1",
 "com.lihaoyi" %% "pprint" % "0.5.3",
 "com.lihaoyi" %% "fastparse" % "2.1.0",
 "io.circe" %% "circe-generic-extras" % circeVersion,
 "io.circe" %% "circe-parser" % circeVersion,
 "com.lihaoyi" %% "requests" % "0.1.7",
 // Automatic type class instances derivation.
 "com.nrinaudo" %% "kantan.csv-java8" % kantanVersion,
 "com.nrinaudo" %% "kantan.csv-cats" % kantanVersion,
 "com.nrinaudo" %% "kantan.csv-generic" % kantanVersion,
 "org.scalatest" %% "scalatest" % "3.0.6" % Test
)

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF")

exportJars := true

fork in run := true

parallelExecution in Test := false

bintrayRepository := "main"

bintrayOrganization := Some("comp-bio-aging")

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

dockerBaseImage := "openjdk:11-oracle"

daemonUserUid in Docker := None

daemonUser in Docker := "root"

dockerExposedVolumes := Seq("/data")

dockerUpdateLatest := true

dockerChmodType := DockerChmodType.UserGroupWriteExecute

maintainer in Docker := "Anton Kulaga <antonkulaga@gmail.com>"

maintainer := "Anton Kulaga <antonkulaga@gmail.com>"

dockerRepository := Some("quay.io/comp-bio-aging")

dockerCommands ++= Seq(
 Cmd("WORKDIR", "/data")
)

enablePlugins(JavaAppPackaging, DockerPlugin)


