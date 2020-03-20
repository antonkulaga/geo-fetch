package geo.fetch
import fastparse.Parsed
import geo.extras._
import geo.models.BioProject.ExperimentSet.ExperimentPackage
import geo.models.{BioProject, GSM, RunInfo}
import io.circe.Decoder.Result
import io.circe.Xml._
import io.circe._
import io.circe.optics.JsonPath.root
import sttp.client._
import zio._

import scala.util.{Failure, Success, Try}
import scala.xml.Elem



case class FetchGEO(apiKey: String = "",  retries: Int = 10) extends FetchGeoJSON {



    /*.flatMap { implicit backend =>
      val localhostRequest = basicRequest
        .get(uri"http://localhost/test")
        .response(asStringAlways)

      val sendWithRetries: ZIO[Clock, Throwable, Response[String]] = localhostRequest
        .send()
        .either
        .repeat(
          Schedule.spaced(1.second) *>
            Schedule.recurs(retries) *>
            Schedule.doWhile(result => RetryWhen.Default(localhostRequest, result))
        )
        .absolve

      sendWithRetries.ensuring(backend.close().catchAll(_ => ZIO.unit))
    }
*/
  //implicit val client: CloseableHttpClient = HttpClientBuilder.create().disableCookieManagement().disableContentCompression().build()
  //implicit val interpreter: InterpTrans[IO] = ApacheInterpreter.instance


  def getRunAnnotation(run: String, series: String = ""): RunAnnotation = {
    val runInfo = getSRA(run).head
    val e = getExperiment(runInfo.run.Experiment)
    val s = if(series!="") series else runInfo.run.Experiment
    val sample = e.sample
    val subject = runInfo.subject
    val att = sample.sample_attributes.attributes
    val age = att.getOrElse("age", "")
    val sex = att.get("sex").orElse(att.get("gender")).getOrElse(runInfo.subject.Sex)
    val characteristics: String = (att ++ subject.asMap.filter(_._2!="")).map(a=>a._1+":" + a._2).mkString(";")
    val source: String = att.get("tissue").orElse(att.get("cell type")).orElse(att.get("cell_type")).orElse(att.get("source_name")).getOrElse(subject.Body_Site)


    RunAnnotation(
      runInfo.sample.BioProject, s, runInfo.run.Run,
      runInfo.sample.ScientificName, runInfo.sample.TaxID,
      runInfo.sample.SampleName, runInfo.sample.Model,
      runInfo.library.LibraryStrategy, runInfo.library.LibraryLayout, runInfo.library.LibrarySelection,
      e.study.accession, e.study.descriptor.study_title,
      characteristics, source, age,  sex , subject.Tumor,
      e.experiment.design.library_descriptor.library_construction_protocol
    )
  }

}

/**
  * Fetches GEO information as JSON (often converted from xml results)
  */
trait FetchGeoJSON extends FetchGeoXML with FetchJSON {


  //def fetchGSM(gsm: String): Task[Result[MINiML.Container]] = get_gsm_json(gsm).map(_.as[MINiML.Container])

  def get_query_json(target: String, id: String): Task[Json] = get_query_xml(target, id).map(_.toJson)
  def get_gse_json(id: String): Task[Json] = get_query_json("gse", id)
  def get_gsm_json(id: String): Task[Json] = get_query_json("gsm", id)

  def get_query_soft(target: String, id: String): Task[Json] = get_query_xml(target, id).map(_.toJson)


  def getSOFT(id: String, target: String = "self"): Parsed[Seq[(String, String, Map[String, Seq[String]])]] =  {
    val txt = get_query_text(id, target).replace("\r\n", "\n")
    SoftParser.parseSOFT(txt)
  }
  //def get_gse_text(id: String) = GSM(getSOFT(id, "gse"))

  def getGSM(id: String, withRunInfo: Boolean = true): GSM = {
    val result: Seq[(String, String, Map[String, Seq[String]])] = getSOFT(id, "gsm").get.value
    val gsm = result.map{ case (_, gsm, seq)=> GSM(gsm, seq)}.head
    if(withRunInfo && gsm.srx.nonEmpty)  {
      val runs =  getSRA(gsm.srx.get)
      gsm.copy(runs = runs)
    } else gsm
  }

  protected val expArr = root.`EXPERIMENT_PACKAGE_SET`.`EXPERIMENT_PACKAGE`.arr
  protected val exp = root.`EXPERIMENT_PACKAGE_SET`.`EXPERIMENT_PACKAGE`.json


  /**
    * Feathres runs based on accession taken from experiment
    * @param e
    * @return
    */
  def runsFromExperiment(e: BioProject.ExperimentSet.ExperimentPackage): (ExperimentPackage, Vector[RunInfo]) = {
    e -> getSRA(e.experiment.accession)
  }

  def runsFromExperiments(exp: BioProject.ExperimentSet): Vector[(ExperimentPackage, Vector[RunInfo])] = {
    exp.experiments.map(runsFromExperiment)
  }

  def tryParseExperiment(json: Json): Try[ExperimentPackage] = {
    exp.getOption(json).map(_.as[BioProject.ExperimentSet.ExperimentPackage]) match {
      case Some(Left(failure)) => Failure(failure)
      case Some(Right(value)) => Success(value)
      case None => Failure(new Exception("Experiment id not found in the json!"))
    }
  }

  def parseExperimentUnsafe(json: Json): ExperimentPackage = {
    exp.getOption(json).map(_.as[BioProject.ExperimentSet.ExperimentPackage]).map{
      case Right(value) => value
      case Left(e) => throw new Exception(s"cannot parse Experiment Package, \n the error is: ${e}")
    }.get
  }

  def getExperiment(id: String): ExperimentPackage = {
    val json = runtime.unsafeRunTask(fetch_bioproject_json(id))//.unsafeRunSync()
    parseExperimentUnsafe(json)
  }

  def parseBioProject(json: Json): BioProject.ExperimentSet = {
    val decoded: Vector[Result[BioProject.ExperimentSet.ExperimentPackage]] = expArr.getOption(json).map(v=> v.map(_.as[BioProject.ExperimentSet.ExperimentPackage])).getOrElse(Vector.empty)
    for( Left(e) <- decoded.filter(_.isLeft)) println(s"ERROR in decoding bioproject: ${e.toString()}")
    val packages = decoded.collect{ case Right(v) => v}
    BioProject.ExperimentSet(packages)
  }

  def getBioProject(id: String): BioProject.ExperimentSet = {
    val json = runtime.unsafeRunTask(fetch_bioproject_json(id))//.unsafeRunSync()
    parseBioProject(json)
  }
}

trait FetchGeoXML extends Fetch {

  import zio._
  def get_query(id: String, target: String, form: String, view: String = "full"): Task[String] = {

    val url = parseUri(s"https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=${id}&targ=${target}&form=${form}&view=${view}${apiKeyAddition}")
    val request = basicRequest.get(url).mapResponse{
      case Right(s)=> s
      case Left(r) => throw new Exception(s"request ${r.toString} gave non string entity!")
    }
    val resp = request.send()
      //.retry(Schedule.recurs(10).addDelay(i=> i second))
      .map(r => r.body)
    resp
    /*Hammock
      .request(Method.GET, uri"https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=${id}&targ=${target}&form=${form}&view=${view}${apiKeyAddition}", Map()) // In the `request` method, you describe your HTTP request
      .map{r=> r.entity match {
          case hammock.Entity.StringEntity(body, contentType) => body
          case other => throw new Exception(s"request ${r.toString} gave non string entity ${other}!")
        }
      }
      .exec[IO]

     */
  }

  def get_query_xml(id: String, target: String): Task[Elem] = get_query(id, target, "xml", "full").map(r=> scala.xml.XML.loadString(r))

  def get_query_text(id: String, target: String): String = {
    val form = "text"
    val view = "full"
    val url = parseUri(s"https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=${id}&targ=${target}&form=${form}&view=${view}${apiKeyAddition}")
    unsafe(quickRequest.get(url).send().map(_.body))
    //requests.get(s"https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=${id}&targ=${target}&form=${form}&view=${view}${apiKeyAddition}").text()
  }

  def get_gse_xml(id: String): Task[Elem] = get_query_xml(id, "gse")
  def get_gsm_xml(id: String): Task[Elem] = get_query_xml(id, "gsm")

  /**
    *
    * ^	caret lines	entity indicator line
    * !	bang lines	entity attribute line
    * #	hash lines	data table header description line
    * n/a	data lines	data table row
    */
  //protected def parseGSM(text: String) = text.split("!")

}