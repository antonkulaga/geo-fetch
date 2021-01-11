import sbt.Keys._

import sbt._

import com.typesafe.sbt.packager.docker.{Cmd, DockerChmodType}

name := "geo-fetch"

organization := "group.aging-research"

scalaVersion :=  "2.13.4"

version := "0.1.1"

isSnapshot := false

javacOptions ++= Seq("-Xlint", "-J-Xss5M", "-encoding", "UTF-8")

javaOptions ++= Seq("-Xms512M", "-Xmx4096M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")

resourceDirectory in Test := baseDirectory { _ / "files" }.value

unmanagedClasspath in Compile ++= (unmanagedResources in Compile).value

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



lazy val circeVersion = "0.13.0"

lazy val kantanVersion = "0.6.1"

lazy val sttpClient = "3.0.0-RC15"//"2.2.9"

libraryDependencies ++= Seq(
 "com.monovore" %% "decline" % "1.3.0",
 "com.monovore" %% "decline-refined" % "1.3.0",
 "org.wvlet.airframe" %% "airframe-log" % "21.1.0",
 "com.github.pathikrit" %% "better-files" % "3.9.1",
 "org.scala-lang.modules" %% "scala-xml" % "1.3.0",
 "com.lihaoyi" %% "pprint" % "0.6.0",
 "com.lihaoyi" %% "fastparse" % "2.3.0",
 "io.circe" %% "circe-generic-extras" % circeVersion,
 "io.circe" %% "circe-optics" % circeVersion,
 "com.nrinaudo" %% "kantan.csv-java8" % kantanVersion,
 "com.nrinaudo" %% "kantan.csv-cats" % kantanVersion,
 "com.nrinaudo" %% "kantan.csv-generic" % kantanVersion,
 "com.softwaremill.sttp.client3" %% "core" % sttpClient,
 "com.softwaremill.sttp.client3" %% "circe" % sttpClient,
 "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % sttpClient,
 "org.json4s" %% "json4s-xml" % "3.6.10",
 "org.json4s" %% "json4s-native" % "3.6.10",
 "com.lihaoyi" %% "ammonite-ops" % "2.3.8",
 "com.github.cb372" %% "cats-retry" % "2.1.0",

"org.scalatest" %% "scalatest" % "3.2.3" % Test
)

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF")

exportJars := true

fork in run := true

parallelExecution in Test := false

bintrayRepository := "main"

bintrayOrganization := Some("comp-bio-aging")

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

dockerBaseImage := 	"oracle/graalvm-ce:20.3.0-java11"

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

