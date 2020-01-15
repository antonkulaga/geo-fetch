package geo.cli
import cats.implicits._
import com.monovore.decline._
import geo.fetch.FetchGEO
import geo.models.{EssentialInfo, GSM}
import io.circe.Json
import io.circe.syntax._
import kantan.csv._
import kantan.csv.ops._

import scala.util.{Failure, Success, Try}

object CommandsBioProject extends CommandsBioProject
class CommandsBioProject extends FetchCommand with CommandSra with CommandsGSM {

  protected lazy val title = Opts.option[String](long = "title", help = "Title of the project").withDefault("")

  protected lazy val characteristics = Opts.option[String](long = "characteristics", help = "Characteristics").withDefault("")

  protected lazy val bioproject = Opts.argument[String]("bioproject")


  /**
    *
    * @param pro project name
    * @param output output file
    * @param f implicit fetcher
    * @return
    */
  protected def fetchBioJSON(pro: String, output: String)(implicit f: FetchGEO): Json = {
    println(s"fetching bioproject $pro json to ${output} ...")
    val bioXML = f.fetch_bioproject_xml(pro).unsafeRunSync()
    import io.circe.Xml._
    val bioJs = bioXML.toJson
    if(output.endsWith(".json")) printOrSave(bioXML.toString, output.replace(".json", ".xml"))
    printOrSave(bioJs.toString, output)
    bioJs
  }

  /**
    * Writes experiment
    * @param pro
    * @param key
    * @param o
    * @param runsPath
    * @param essential
    */
  def fetchExperiment(pro: String, key: String, o: String, runsPath: String, essential: Boolean): Unit = {
    implicit val f: FetchGEO =  FetchGEO(key)
    val bioJs = fetchBioJSON(pro, o)
    f.tryParseExperiment(bioJs) match {
      case Success(value) =>
        val (e, runs) = f.runsFromExperiment(value)
        if(essential) {
          val info = EssentialInfo.extract(pro, e.experiment.accession, runs, e.experiment.title, e.sample.sample_attributes.characteristics)
          //.asCsv(rfc.withCellSeparator('\t'))
          val str = if(runsPath.endsWith(".json")) info.asJson.spaces2 else info.asCsv(rfc.withCellSeparator('\t').withHeader)
          printOrSave(str, runsPath)
        } else printOrSave(runs.asJson.spaces2, runsPath)

      case Failure(exception) =>
        println(s"FAILED parsing experiment package ${pro}")
        println("error: "+exception.getMessage)
        println("JSON:")
        println(bioJs)
    }
  }

  def fetchBioProject(pro: String, key: String, o: String, runsPath: String, essential: Boolean): Unit = if(pro.toUpperCase.startsWith("SRX") || pro.toUpperCase.startsWith("ERX"))
  fetchExperiment(pro, key, o, runsPath, essential)
  else {
    implicit val f =  FetchGEO(key)
    val bioJs = fetchBioJSON(pro, o)
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
