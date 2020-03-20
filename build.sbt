import sbt.Keys._

import sbt._

import com.typesafe.sbt.packager.docker.{Cmd, DockerChmodType}

name := "geo-fetch"

organization := "group.aging-research"

scalaVersion :=  "2.13.1"

version := "0.1.0"

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

lazy val kantanVersion = "0.6.0"

libraryDependencies ++= Seq(
 "org.typelevel" %% "cats-core" % "2.1.1",
 "com.monovore" %% "decline" % "1.0.0",
 "com.monovore" %% "decline-refined" % "1.0.0",
 "org.wvlet.airframe" %% "airframe-log" % "20.3.0",
 "com.github.pathikrit" %% "better-files" % "3.8.0", "com.github.pathikrit" %% "better-files" % "3.8.0",
 "org.scala-lang.modules" %% "scala-xml" % "1.3.0",
 "com.lihaoyi" %% "pprint" % "0.5.9",
 "com.lihaoyi" %% "fastparse" % "2.2.4",
 "io.circe" %% "circe-generic-extras" % circeVersion,
 "io.circe" %% "circe-optics" % circeVersion,
 "com.nrinaudo" %% "kantan.csv-java8" % kantanVersion,
 "com.nrinaudo" %% "kantan.csv-cats" % kantanVersion,
 "com.nrinaudo" %% "kantan.csv-generic" % kantanVersion,
 "com.softwaremill.sttp.client" %% "core" % "2.0.6",
 "com.softwaremill.sttp.client" %% "circe" % "2.0.6",
 "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % "2.0.6",
"org.json4s" %% "json4s-xml" % "3.6.7",
 "org.json4s" %% "json4s-native" % "3.6.7",
 "com.lihaoyi" %% "ammonite-ops" % "2.0.4",
 "com.github.cb372" %% "cats-retry" % "1.1.0",

"org.scalatest" %% "scalatest" % "3.1.1" % Test
)

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF")

exportJars := true

fork in run := true

parallelExecution in Test := false

bintrayRepository := "main"

bintrayOrganization := Some("comp-bio-aging")

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

dockerBaseImage := 	"oracle/graalvm-ce:19.3.1-java11"

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

