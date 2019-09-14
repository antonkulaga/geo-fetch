package geo.cli
import cats.implicits._
import com.monovore.decline._
import geo.fetch.FetchGEO
import geo.models.{EssentialInfo, GSM}
import io.circe.Json
import io.circe.syntax._
import kantan.csv._
import kantan.csv.ops._

class CommandsBioProject extends FetchCommand with CommandSra with CommandsGSM {

  protected lazy val title = Opts.option[String](long = "title", help = "Title of the project").withDefault("")

  protected lazy val characteristics = Opts.option[String](long = "characteristics", help = "Characteristics").withDefault("")

  protected lazy val bioproject = Opts.argument[String]("bioproject")


  protected def fetchBioJSON(pro: String, key: String, o: String)(implicit f: FetchGEO): Json = {
    val f =  FetchGEO(key)
    println(s"fetching bioproject $pro json to ${o} ...")
    val bioJs = f.fetch_bioproject_json(pro).unsafeRunSync()
    printOrSave(bioJs.toString, o)
    bioJs
  }

  def fetchExperiment(pro: String, key: String, o: String, runsPath: String, essential: Boolean): Unit = {
    implicit val f =  FetchGEO(key)
    val bioJs = fetchBioJSON(pro, key, o)
    val (e, runs) = f.runsFromExperiment(f.parseExperiment(bioJs))
    if(essential) {
      val info = EssentialInfo.extract(pro, e.experiment.accession, runs, e.experiment.title, e.sample.sample_attributes.characteristics)
      //.asCsv(rfc.withCellSeparator('\t'))
      val str = if(runsPath.endsWith(".json")) info.asJson.spaces2 else info.asCsv(rfc.withCellSeparator('\t').withHeader)
      printOrSave(str, runsPath)
    } else printOrSave(runs.asJson.spaces2, runsPath)
  }

  def fetchBioProject(pro: String, key: String, o: String, runsPath: String, essential: Boolean): Unit = if(pro.toUpperCase.startsWith("SRX") || pro.toUpperCase.startsWith("ERX"))
  fetchExperiment(pro, key, o, runsPath, essential)
  else {
    implicit val f =  FetchGEO(key)
    val bioJs = fetchBioJSON(pro, key, o)
    val runs = f.runsFromExperiments(f.parseBioProject(bioJs))
    //val bioPro = f.parseBioProject(bioJs)
    println(s"saving runs to ${runsPath}")
    if(essential) {
      val info = runs.flatMap { case (e, rs) => EssentialInfo.extract(pro, e.experiment.accession, rs, e.experiment.title, e.sample.sample_attributes.characteristics) }
      //.asCsv(rfc.withCellSeparator('\t'))
      val str = if(runsPath.endsWith(".json")) info.asJson.spaces2 else info.asCsv(rfc.withCellSeparator('\t').withHeader)
      printOrSave(str, runsPath)
    }
    else {
      printOrSave(runs.asJson.spaces2, runsPath)
    }
  }

  protected lazy val fetch_experiment: Command[Unit] = Command(
    name = "experiment",
    header = "Fetch Experiment"){
    (bioproject, key, output, runs, essential).mapN{fetchExperiment}
  }

  protected lazy val fetch_bioproject: Command[Unit] = Command(
    name = "bioproject",
    header = "Fetch BioProject"){
    (bioproject, key, output, runs, essential).mapN{fetchBioProject}
  }
}
