package geo.cli
import cats.implicits._
import com.monovore.decline._
import geo.fetch.FetchGEO
import geo.models.{EssentialInfo, GSM}
import io.circe.syntax._
import kantan.csv._
import kantan.csv.ops._
class CommandsGSM extends FetchCommand with CommandSra {

  protected lazy val gsm = Opts.argument[String]("gsm")

  //protected lazy val geoFormat = Opts.option[String](long = "format", short = "f", help = "soft, xml or json").withDefault("soft")
  //protected  lazy val runs = Opts.flag("runs", help = "Include SRA runs to the output").withDefault(true)
  protected lazy val essential: Opts[Boolean] = Opts.flag("essential", help = "Include only the most important information about the run", "e").orFalse
  protected lazy val runs = Opts.option[String](long = "runs", short = "r", help = "Where to put SRA output").withDefault("")
  //protected lazy val essential = Opts.option[String](long = "essential", short = "e", help = "Only essential information").withDefault("")



  def fetchGSM(gsm: String, key: String, o: String, runs: String, essential: Boolean): Unit = {
    val f =  FetchGEO(key)
    val g: GSM = f.getGSM(gsm, true)
    printOrSave(g.asJson.spaces2, o)
    runs match {
      case p if essential =>
        val info = EssentialInfo.extract(g)
        //.asCsv(rfc.withCellSeparator('\t'))
        val str = if(p.endsWith(".json")) info.asJson.spaces2 else info.asCsv(rfc.withCellSeparator('\t'))
        printOrSave(str, runs)
      case _ => printOrSave(g.runs.asJson.spaces2, runs)
    }
  }

  protected lazy val fetch_gsm: Command[Unit] = Command(
    name = "gsm",
    header = "Fetch GSM"
  ) {
    (gsm, key, output, runs, essential).mapN{fetchGSM}
  }
}
