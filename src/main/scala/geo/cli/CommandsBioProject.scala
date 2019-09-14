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


  def fetchBioProject(pro: String, key: String, o: String, runsPath: String, essential: Boolean): Unit = {
    val f =  FetchGEO(key)
    println(s"fetching bioproject $pro json to ${o} ...")
    val bioJs = f.fetch_bioproject_json(pro).unsafeRunSync()
    printOrSave(bioJs.toString, o)
    val runs = if(pro.toUpperCase.startsWith("SRX") || pro.toUpperCase.startsWith("ERX"))
      Vector(f.runsFromExperiment(f.parseExperiment(bioJs))) else f.runsFromExperiments(f.parseBioProject(bioJs))
    //val bioPro = f.parseBioProject(bioJs)
    println(s"saving runs to ${runsPath}")
    runs match {
      case p if essential =>
        val info = runs.flatMap { case (e, rs) => EssentialInfo.extract(pro, e.experiment.accession, rs, e.experiment.title, e.sample.sample_attributes.characteristics) }
        //.asCsv(rfc.withCellSeparator('\t'))
        val str = if(p.endsWith(".json")) info.asJson.spaces2 else info.asCsv(rfc.withCellSeparator('\t').withHeader)
        printOrSave(str, runsPath)

      case _ => printOrSave(runs.asJson.spaces2, runsPath)
    }
  }

  protected lazy val fetch_bioproject: Command[Unit] = Command(
    name = "bioproject",
    header = "Fetch BioProject"){
    (bioproject, key, output, runs, essential).mapN{fetchBioProject}
  }
}
