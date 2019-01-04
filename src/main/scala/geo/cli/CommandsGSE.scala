package geo.cli
import java.nio.file.{Path, Paths}

import wvlet.log.LogSupport
import java.nio.file.Path

import better.files._
import cats.implicits._
import com.monovore.decline._
import geo.fetch.FetchGEO
import geo.models.RunInfo
import wvlet.log.LogSupport
import pprint.PPrinter.BlackWhite
/*
class CommandsGSE extends CommandsGSM {


  protected lazy val gse = Opts.argument[String]("gse")

  protected lazy val fetch_gse: Command[Unit] = Command(
    name = "gse",
    header = "Fetch GSE"
  ) {
    (gse, key, output, runs).mapN{getGSE}
  }
}
*/