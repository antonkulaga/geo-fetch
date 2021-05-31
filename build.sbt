import sbt.Keys._

import sbt._

import com.typesafe.sbt.packager.docker.{Cmd, DockerChmodType}

name := "geo-fetch"

organization := "group.aging-research"

scalaVersion :=  "2.13.6"

version := "0.1.2"

isSnapshot := false

javacOptions ++= Seq("-Xlint", "-J-Xss5M", "-encoding", "UTF-8")
javaOptions ++= Seq("-Xms512M", "-Xmx4096M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")

Test / resourceDirectory := baseDirectory { _ / "files" }.value

Compile / unmanagedClasspath ++= (Compile / unmanagedResources).value

resolvers += Resolver.bintrayRepo("comp-bio-aging", "main")

scalacOptions ++= Seq(
 "-deprecation",
 "-feature",
 "-unchecked",
 "-language:implicitConversions",
 "-language:higherKinds",
 "-language:existentials",
 "-language:postfixOps",
 "-Ymacro-annotations"
)



lazy val circeVersion = "0.14.1"

lazy val kantanVersion = "0.6.1"

lazy val sttpClient = "3.3.5"//"2.2.9"

libraryDependencies ++= Seq(
 "com.monovore" %% "decline" % "2.0.0",
 "org.wvlet.airframe" %% "airframe-log" % "21.5.4",
 "com.github.pathikrit" %% "better-files" % "3.9.1",
 "org.scala-lang.modules" %% "scala-xml" % "2.0.0",
 "com.lihaoyi" %% "pprint" % "0.6.6",
 "com.lihaoyi" %% "fastparse" % "2.3.2",
 "io.circe" %% "circe-generic-extras" % circeVersion,
 "io.circe" %% "circe-optics" % circeVersion,
 "com.nrinaudo" %% "kantan.csv-java8" % kantanVersion,
 "com.nrinaudo" %% "kantan.csv-cats" % kantanVersion,
 "com.nrinaudo" %% "kantan.csv-generic" % kantanVersion,
 "com.softwaremill.sttp.client3" %% "core" % sttpClient,
 "com.softwaremill.sttp.client3" %% "circe" % sttpClient,
 "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % sttpClient,
 "org.json4s" %% "json4s-xml" % "4.0.0",
 "org.json4s" %% "json4s-native" % "4.0.0",
 "com.lihaoyi" %% "ammonite-ops" % "2.3.8",
 "com.github.cb372" %% "cats-retry" % "3.0.0",
 "org.scalatest" %% "scalatest" % "3.2.9" % Test
)

Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oF")

exportJars := true

run / fork := true

Test / parallelExecution := false

bintrayRepository := "main"

bintrayOrganization := Some("comp-bio-aging")

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

dockerBaseImage := 	"ghcr.io/graalvm/graalvm-ce:latest"

Docker / daemonUserUid := None

Docker / daemonUser := "root"

Docker / maintainer := "Anton Kulaga <antonkulaga@gmail.com>"

dockerExposedVolumes := Seq("/data")

dockerUpdateLatest := true

dockerChmodType := DockerChmodType.UserGroupWriteExecute

maintainer := "Anton Kulaga <antonkulaga@gmail.com>"

dockerRepository := Some("quay.io/comp-bio-aging")

dockerCommands ++= Seq(
  Cmd("WORKDIR", "/data")
)

enablePlugins(JavaAppPackaging, DockerPlugin)

