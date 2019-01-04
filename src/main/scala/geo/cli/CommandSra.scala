package geo.cli
import java.nio.file.{Path, Paths}

import wvlet.log.LogSupport
import java.nio.file.Path
import io.circe.syntax._
import better.files._
import cats.implicits._
import com.monovore.decline._
import geo.fetch.FetchGEO
import geo.models.RunInfo
import wvlet.log.LogSupport
import pprint.PPrinter.BlackWhite

trait CommandSra extends FetchCommand {
  protected lazy val sra = Opts.argument[String]("sra")

  protected lazy val sraFormat = Opts.option[String](long = "format", short = "f", help = "runinfo, xml or json, soft by default").withDefault("runinfo")

  def fetchSRA(sra: String, key: String, format: String = "runinfo", o: String = ""): Unit = {
    val f = FetchGEO(key)
    format.toLowerCase match {
      case "soft" | "txt" | "text" =>
        val run_str = f.fetch_sra_runinfo(sra).replace("\n\n", "\n")

        import kantan.csv._
        import kantan.csv.ops._
        import kantan.csv.generic._

        val info = RunInfo.fromCSV(run_str)
        if(o.endsWith("json")){
          val result = info.asJson.spaces2
          printOrSave(result, o)
        } else
        if(o.endsWith("csv")) {
          printOrSave(run_str, o)
        } else {
          printOrSave(run_str.replace(",", "\t"), o)
        }

      case "json" =>
        val result = f.fetch_sra_json(sra).unsafeRunSync().toString
        printOrSave(result, o)
      case "xml" =>
        val result = f.fetch_sra_xml(sra).unsafeRunSync().toString
        printOrSave(result, o)
      case _ => this.error("unknown ")

    }
  }

  protected lazy val fetch_sra: Command[Unit] = Command(
    name = "sra",
    header = "Fetch SRA"
  ) {
    (sra, key, sraFormat, output).mapN{fetchSRA}
  }

}
