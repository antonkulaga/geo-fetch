package geo.cli
import cats.implicits._
import com.monovore.decline._
import geo.fetch.FetchGEO
import geo.models.RunInfo
import io.circe.syntax._

object CommandSra extends CommandSra
trait CommandSra extends FetchCommand {
  protected lazy val sra = Opts.argument[String]("sra")

  protected lazy val sraFormat = Opts.option[String](long = "format", short = "f", help = "runinfo, xml or json, soft by default").withDefault("runinfo")


  def fetchSRA(sra: String, key: String, format: String = "runinfo", o: String = ""): Unit = {
    val f = FetchGEO(key)
    format.toLowerCase match {
      case "soft" | "txt" | "text" | "runinfo" =>
        val run_str = f.get_sra_runinfo(sra).replace("\r\n", "\n")
        val info = RunInfo.fromCSV(run_str)
        if(o.endsWith("flat.json")){
          val strs = info.map(_.toFlatJSON.spaces2)
          if(strs.size == 1){
            printOrSave(strs.head, o)
          } else {
            println("WARNING: MORE THAN ONE SRR!")
            for( (s, i) <- strs.zipWithIndex)
            {
              printOrSave(s, o.replace("flat.json", s"${i}_flat.json"))
            }
          }
        }
        else
        if(o.endsWith("json")){
          val result = info.asJson.spaces2
          printOrSave(result, o)
        }
        else
        if(o.endsWith("csv")) {
          printOrSave(run_str, o)
        } else {
          printOrSave(run_str.replace(",", "\t"), o)
        }
      case "json" =>
        val result = f.unsafe(f.fetch_sra_json(sra)).toString
        printOrSave(result, o)

      case "xml" =>
        val result = f.unsafe(f.fetch_sra_xml(sra)).toString
        printOrSave(result, o)
      case _ => this.error(s"unknown format ${format} for ${sra} with output ${o}")

    }
  }

  protected lazy val fetch_sra: Command[Unit] = Command(
    name = "sra",
    header = "Fetch SRA"
  ) {
    (sra, key, sraFormat, output).mapN{fetchSRA}
  }

}
