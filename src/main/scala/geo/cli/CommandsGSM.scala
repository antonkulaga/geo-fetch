package geo.cli
import java.nio.file.{Path, Paths}

import wvlet.log.LogSupport
import java.nio.file.Path

import better.files._
import cats.implicits._
import com.monovore.decline._
import geo.fetch.FetchGEO
import geo.models.{GSM, RunInfo}
import wvlet.log.LogSupport
import pprint.PPrinter.BlackWhite
import io.circe.syntax._

class CommandsGSM extends FetchCommand with CommandSra {

  protected lazy val gsm = Opts.argument[String]("gsm")

  //protected lazy val geoFormat = Opts.option[String](long = "format", short = "f", help = "soft, xml or json").withDefault("soft")
  //protected  lazy val runs = Opts.flag("runs", help = "Include SRA runs to the output").withDefault(true)
  protected lazy val runs = Opts.option[String](long = "runs", short = "r", help = "Where to put SRA output").withDefault("")


  def fetchGSM(gsm: String, key: String, o: String, runs: String): Unit = {
    val f =  FetchGEO(key)
    val g: GSM = f.getGSM(gsm)
    printOrSave(g.asJson.spaces2, o)
    g.relations.srx match {
      case Some(srx) => fetchSRA(srx, key)
      case None =>
        error(s"cannot find runs for ${gsm}")
    }
  }

  protected lazy val fetch_gsm: Command[Unit] = Command(
    name = "gsm",
    header = "Fetch GSM"
  ) {
    (gsm, key, output, runs).mapN{fetchGSM}
  }
}
