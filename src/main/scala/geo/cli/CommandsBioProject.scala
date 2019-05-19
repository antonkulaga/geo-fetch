package geo.cli
import cats.implicits._
import com.monovore.decline._
import geo.fetch.FetchGEO
import geo.models.{EssentialInfo, GSM}
import io.circe.syntax._
import kantan.csv._
import kantan.csv.ops._

class CommandsBioProject extends FetchCommand with CommandSra with CommandsGSM {

  protected lazy val title = Opts.option[String](long = "title", help = "Title of the project").withDefault("")

  protected lazy val characteristics = Opts.option[String](long = "characteristics", help = "Characteristics").withDefault("")

  protected lazy val bioproject = Opts.argument[String]("bioproject")


  def fetchBioProject(pro: String, key: String, o: String, runsPath: String, essential: Boolean, title: String, characteristics: String): Unit = {
    val f =  FetchGEO(key)
    println(s"fetching bioproject $pro json to ${o} ...")
    printOrSave(f.fetch_sra_json(pro).unsafeRunSync().toString, o)
    println(s"saving runs to ${runsPath}")
    val runs = f.getSRA(pro)
    runs match {
      case p if essential =>
        val info = EssentialInfo.extract(pro,runs,title, characteristics)
        //.asCsv(rfc.withCellSeparator('\t'))
        val str = if(p.endsWith(".json")) info.asJson.spaces2 else info.asCsv(rfc.withCellSeparator('\t').withHeader)
        printOrSave(str, runsPath)

      case _ => printOrSave(runs.asJson.spaces2, runsPath)
    }
  }

  protected lazy val fetch_bioproject: Command[Unit] = Command(
    name = "bioproject",
    header = "Fetch BioProject"){
    (bioproject, key, output, runs, essential, title, characteristics).mapN{fetchBioProject}
  }
}
