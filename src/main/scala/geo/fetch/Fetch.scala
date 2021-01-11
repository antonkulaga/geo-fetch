package geo.fetch
import geo.models.RunInfo
import org.asynchttpclient.{AsyncHttpClient, DefaultAsyncHttpClientConfig}
import zio.internal.Platform
import io.circe.Xml._
import io.circe._
import sttp.client3.asynchttpclient.zio._
import sttp.model.Uri
import zio._

import scala.xml.Elem
import org.asynchttpclient.Dsl.asyncHttpClient
import sttp.client3.asynchttpclient

trait FetchJSON extends FetchXML {

  def fetch_bioproject_json(id: String): Task[Json] = fetch_json("bioproject", id)
  def fetch_biosample_json(id: String): Task[Json] = fetch_json("biosample", id)
  def fetch_biosystems_json(id: String): Task[Json] = fetch_json("biosystems", id)
  def fetch_books_json(id: String): Task[Json] = fetch_json("books", id)
  def fetch_cdd_json(id: String): Task[Json] = fetch_json("cdd", id)
  def fetch_gap_json(id: String): Task[Json] = fetch_json("gap", id)
  def fetch_dbvar_json(id: String): Task[Json] = fetch_json("dbvar", id)
  def fetch_epigenomics_json(id: String): Task[Json] = fetch_json("epigenomics", id)
  def fetch_nucest_json(id: String): Task[Json] = fetch_json("nucest", id)
  def fetch_gene_json(id: String): Task[Json] = fetch_json("gene", id)
  def fetch_genome_json(id: String): Task[Json] = fetch_json("genome", id)
  def fetch_gds_json(id: String): Task[Json] = fetch_json("gds", id)
  def fetch_geoprofiles_json(id: String): Task[Json] = fetch_json("geoprofiles", id)
  def fetch_nucgss_json(id: String): Task[Json] = fetch_json("nucgss", id)
  def fetch_homologene_json(id: String): Task[Json] = fetch_json("homologene", id)
  def fetch_mesh_json(id: String): Task[Json] = fetch_json("mesh", id)
  def fetch_ncbisearch_json(id: String): Task[Json] = fetch_json("ncbisearch", id)
  def fetch_toolkit_json(id: String): Task[Json] = fetch_json("toolkit", id)
  def fetch_nlmcatalog_json(id: String): Task[Json] = fetch_json("nlmcatalog", id)
  def fetch_nuccore_json(id: String): Task[Json] = fetch_json("nuccore", id)
  def fetch_omia_json(id: String): Task[Json] = fetch_json("omia", id)
  def fetch_popset_json(id: String): Task[Json] = fetch_json("popset", id)
  def fetch_probe_json(id: String): Task[Json] = fetch_json("probe", id)
  def fetch_protein_json(id: String): Task[Json] = fetch_json("protein", id)
  def fetch_proteinclusters_json(id: String): Task[Json] = fetch_json("proteinclusters", id)
  def fetch_pcassay_json(id: String): Task[Json] = fetch_json("pcassay", id)
  def fetch_pccompound_json(id: String): Task[Json] = fetch_json("pccompound", id)
  def fetch_pcsubstance_json(id: String): Task[Json] = fetch_json("pcsubstance", id)
  def fetch_pubmed_json(id: String): Task[Json] = fetch_json("pubmed", id)
  def fetch_pmc_json(id: String): Task[Json] = fetch_json("pmc", id)
  def fetch_snp_json(id: String): Task[Json] = fetch_json("snp", id)
  def fetch_sra_json(id: String): Task[Json] = fetch_json("sra", id)

  def fetch_structure_json(id: String): Task[Json] = fetch_json("structure", id)
  def fetch_taxonomy_json(id: String): Task[Json] = fetch_json("taxonomy", id)
  def fetch_unigene_json(id: String): Task[Json] = fetch_json("unigene", id)
  def fetch_unists_json(id: String): Task[Json] = fetch_json("unists", id)

  def fetch_json(db: String, id: String): Task[Json] = fetch_xml(db, id).map(e=>e.toJson)
  def xml2json(xml: Task[Elem]): Task[Json] = xml.map(e=>e.toJson)
}

import sttp.client3._
import zio.duration._
import zio.{Schedule, ZIO}


/**
  * See https://www.ncbi.nlm.nih.gov/books/NBK25499/#chapter4.ESearch for more info
  */
trait FetchXML extends Fetch {


  def fetch_bioproject_xml(id: String): Task[Elem] = fetch_xml("bioproject", id)
  def fetch_biosample_xml(id: String): Task[Elem] = fetch_xml("biosample", id)
  def fetch_biosystems_xml(id: String): Task[Elem] = fetch_xml("biosystems", id)
  def fetch_books_xml(id: String): Task[Elem] = fetch_xml("books", id)
  def fetch_cdd_xml(id: String): Task[Elem] = fetch_xml("cdd", id)
  def fetch_gap_xml(id: String): Task[Elem] = fetch_xml("gap", id)
  def fetch_dbvar_xml(id: String): Task[Elem] = fetch_xml("dbvar", id)
  def fetch_epigenomics_xml(id: String): Task[Elem] = fetch_xml("epigenomics", id)
  def fetch_nucest_xml(id: String): Task[Elem] = fetch_xml("nucest", id)
  def fetch_gene_xml(id: String): Task[Elem] = fetch_xml("gene", id)
  def fetch_genome_xml(id: String): Task[Elem] = fetch_xml("genome", id)
  def fetch_gds_xml(id: String): Task[Elem] = fetch_xml("gds", id)
  def fetch_geoprofiles_xml(id: String): Task[Elem] = fetch_xml("geoprofiles", id)
  def fetch_nucgss_xml(id: String): Task[Elem] = fetch_xml("nucgss", id)
  def fetch_homologene_xml(id: String): Task[Elem] = fetch_xml("homologene", id)
  def fetch_mesh_xml(id: String): Task[Elem] = fetch_xml("mesh", id)
  def fetch_ncbisearch_xml(id: String): Task[Elem] = fetch_xml("ncbisearch", id)
  def fetch_toolkit_xml(id: String): Task[Elem] = fetch_xml("toolkit", id)
  def fetch_nlmcatalog_xml(id: String): Task[Elem] = fetch_xml("nlmcatalog", id)
  def fetch_nuccore_xml(id: String): Task[Elem] = fetch_xml("nuccore", id)
  def fetch_omia_xml(id: String): Task[Elem] = fetch_xml("omia", id)
  def fetch_popset_xml(id: String): Task[Elem] = fetch_xml("popset", id)
  def fetch_probe_xml(id: String): Task[Elem] = fetch_xml("probe", id)
  def fetch_protein_xml(id: String): Task[Elem] = fetch_xml("protein", id)
  def fetch_proteinclusters_xml(id: String): Task[Elem] = fetch_xml("proteinclusters", id)
  def fetch_pcassay_xml(id: String): Task[Elem] = fetch_xml("pcassay", id)
  def fetch_pccompound_xml(id: String): Task[Elem] = fetch_xml("pccompound", id)
  def fetch_pcsubstance_xml(id: String): Task[Elem] = fetch_xml("pcsubstance", id)
  def fetch_pubmed_xml(id: String): Task[Elem] = fetch_xml("pubmed", id)
  def fetch_pmc_xml(id: String): Task[Elem] = fetch_xml("pmc", id)
  def fetch_snp_xml(id: String): Task[Elem] = fetch_xml("snp", id)
  def fetch_sra_xml(id: String): Task[Elem] = fetch_xml("sra", id)
  def fetch_sra_runinfo(id: String): Task[String]= {
    val db = "sra"
    val returnType = "runinfo"
    val url = parseUri(s"http://trace.ncbi.nlm.nih.gov/Traces/sra/sra.cgi?save=efetch&db=${db}&rettype=${returnType}&term=${id}${apiKeyAddition}")
    quickRequest.get(url).mapResponse(_.replace("\r\n", "\n")).send(sttpBackend).map(_.body)

  }
  def get_sra_runinfo(id: String) = unsafe(fetch_sra_runinfo(id))




  def getSRA(id: String): Vector[RunInfo] = {
    val run_str = get_sra_runinfo(id: String).replace("\r\n", "\n")
    RunInfo.fromCSV(run_str).toVector
  }

  def fetch_structure_xml(id: String): Task[Elem] = fetch_xml("structure", id)
  def fetch_taxonomy_xml(id: String): Task[Elem] = fetch_xml("taxonomy", id)
  def fetch_unigene_xml(id: String): Task[Elem] = fetch_xml("unigene", id)
  def fetch_unists_xml(id: String): Task[Elem] = fetch_xml("unists", id)


  /**
    * @param db database
    * @param id id of the object
    * @return
    */
  def fetch_xml(db: String, id: String, returnType: String = "fullxml"): Task[Elem] = {
    val url = parseUri((s"http://trace.ncbi.nlm.nih.gov/Traces/sra/sra.cgi?save=efetch&db=${db}&rettype=${returnType}&term=${id}${apiKeyAddition}"))
    val timeout = scala.concurrent.duration.FiniteDuration(2, scala.concurrent.duration.MINUTES)
    val request = basicRequest.get(url)
      .followRedirects(true)
      .readTimeout(timeout).mapResponse{
      case Right(s)=> scala.xml.XML.loadString(s)
      case Left(r) => throw new Exception(s"request ${r.toString} error!")
    }
    val resp: Task[Elem]= request.send(sttpBackend)
      .map(r => r.body)
      //.retry(Schedule.recurs(10).addDelay(i=> i second))
    resp
  }
}

trait Fetch {

  protected val clientConfig: DefaultAsyncHttpClientConfig = new DefaultAsyncHttpClientConfig.Builder()
    .setFollowRedirect(true)
    .setReadTimeout(25000)
    .setConnectTimeout(25000)
    .setRequestTimeout(120000)
    .build()

  //println(clientConfig.getConnectTimeout + " CONNECTION TIMEOUT")
  //println(clientConfig.getReadTimeout + " REQUEST TIMEOUT")


  protected val client: AsyncHttpClient = asyncHttpClient(this.clientConfig)

  implicit val sttpBackend = AsyncHttpClientZioBackend.usingClient(zio.Runtime.default, client) //UPD
  //: SttpBackend[Task, Nothing, WebSocketHandler]

  //type Task[T] = ZTask[SttpClient, Throwable, T]
  val runtime = Runtime(client, Platform.default)

  def unsafe[A](task: =>ZIO[AsyncHttpClient, Throwable, A ]): A = {
    //runtime.unsafeRunTask(task.retry((Schedule.recurs(10)))) //UPD
    //runtime.unsafeRunTask(task.retry((Schedule.recurs(10))))
    runtime.unsafeRunTask(task.retryN(10))
    //runtime.unsafeRunTask(task.retry(Schedule.recurs(10)))
  }
  //type Env = Any with Clock with SttpClient


  //implicit def interpreter: InterpTrans[IO]

  protected def parseUri(str: String): Uri =     Uri.parse(str).toOption match {
    case Some(value) => value
    case None =>
      println(s"FAILED TO PARSE ${str} falling back to just http://cromwell:8000")
      uri"http://trace.ncbi.nlm.nih.gov/Traces/sra/sra.cgi?save=efetch"
  }



  def apiKey: String


  def apiKeyAddition: String = if(apiKey=="") "" else s"&api_key=${apiKey}"

}