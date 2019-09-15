package geo.fetch
import cats.effect.IO
import io.circe.generic.auto._
import hammock.marshalling._
import hammock._
import hammock.apache.ApacheInterpreter
import hammock.circe.implicits._
import io.circe._
import Xml._
import geo.models.RunInfo

import scala.xml.Elem

trait FetchJSON extends FetchXML {

  def fetch_bioproject_json(id: String): IO[Json] = fetch_json("bioproject", id)
  def fetch_biosample_json(id: String): IO[Json] = fetch_json("biosample", id)
  def fetch_biosystems_json(id: String): IO[Json] = fetch_json("biosystems", id)
  def fetch_books_json(id: String): IO[Json] = fetch_json("books", id)
  def fetch_cdd_json(id: String): IO[Json] = fetch_json("cdd", id)
  def fetch_gap_json(id: String): IO[Json] = fetch_json("gap", id)
  def fetch_dbvar_json(id: String): IO[Json] = fetch_json("dbvar", id)
  def fetch_epigenomics_json(id: String): IO[Json] = fetch_json("epigenomics", id)
  def fetch_nucest_json(id: String): IO[Json] = fetch_json("nucest", id)
  def fetch_gene_json(id: String): IO[Json] = fetch_json("gene", id)
  def fetch_genome_json(id: String): IO[Json] = fetch_json("genome", id)
  def fetch_gds_json(id: String): IO[Json] = fetch_json("gds", id)
  def fetch_geoprofiles_json(id: String): IO[Json] = fetch_json("geoprofiles", id)
  def fetch_nucgss_json(id: String): IO[Json] = fetch_json("nucgss", id)
  def fetch_homologene_json(id: String): IO[Json] = fetch_json("homologene", id)
  def fetch_mesh_json(id: String): IO[Json] = fetch_json("mesh", id)
  def fetch_ncbisearch_json(id: String): IO[Json] = fetch_json("ncbisearch", id)
  def fetch_toolkit_json(id: String): IO[Json] = fetch_json("toolkit", id)
  def fetch_nlmcatalog_json(id: String): IO[Json] = fetch_json("nlmcatalog", id)
  def fetch_nuccore_json(id: String): IO[Json] = fetch_json("nuccore", id)
  def fetch_omia_json(id: String): IO[Json] = fetch_json("omia", id)
  def fetch_popset_json(id: String): IO[Json] = fetch_json("popset", id)
  def fetch_probe_json(id: String): IO[Json] = fetch_json("probe", id)
  def fetch_protein_json(id: String): IO[Json] = fetch_json("protein", id)
  def fetch_proteinclusters_json(id: String): IO[Json] = fetch_json("proteinclusters", id)
  def fetch_pcassay_json(id: String): IO[Json] = fetch_json("pcassay", id)
  def fetch_pccompound_json(id: String): IO[Json] = fetch_json("pccompound", id)
  def fetch_pcsubstance_json(id: String): IO[Json] = fetch_json("pcsubstance", id)
  def fetch_pubmed_json(id: String): IO[Json] = fetch_json("pubmed", id)
  def fetch_pmc_json(id: String): IO[Json] = fetch_json("pmc", id)
  def fetch_snp_json(id: String): IO[Json] = fetch_json("snp", id)
  def fetch_sra_json(id: String): IO[Json] = fetch_json("sra", id)
  def fetch_structure_json(id: String): IO[Json] = fetch_json("structure", id)
  def fetch_taxonomy_json(id: String): IO[Json] = fetch_json("taxonomy", id)
  def fetch_unigene_json(id: String): IO[Json] = fetch_json("unigene", id)
  def fetch_unists_json(id: String): IO[Json] = fetch_json("unists", id)

  def fetch_json(db: String, id: String): IO[Json] = fetch_xml(db, id).map(e=>e.toJson)
}

/**
  * See https://www.ncbi.nlm.nih.gov/books/NBK25499/#chapter4.ESearch for more info
  */
trait FetchXML extends Fetch {


  def fetch_bioproject_xml(id: String): IO[Elem] = fetch_xml("bioproject", id)
  def fetch_biosample_xml(id: String): IO[Elem] = fetch_xml("biosample", id)
  def fetch_biosystems_xml(id: String): IO[Elem] = fetch_xml("biosystems", id)
  def fetch_books_xml(id: String): IO[Elem] = fetch_xml("books", id)
  def fetch_cdd_xml(id: String): IO[Elem] = fetch_xml("cdd", id)
  def fetch_gap_xml(id: String): IO[Elem] = fetch_xml("gap", id)
  def fetch_dbvar_xml(id: String): IO[Elem] = fetch_xml("dbvar", id)
  def fetch_epigenomics_xml(id: String): IO[Elem] = fetch_xml("epigenomics", id)
  def fetch_nucest_xml(id: String): IO[Elem] = fetch_xml("nucest", id)
  def fetch_gene_xml(id: String): IO[Elem] = fetch_xml("gene", id)
  def fetch_genome_xml(id: String): IO[Elem] = fetch_xml("genome", id)
  def fetch_gds_xml(id: String): IO[Elem] = fetch_xml("gds", id)
  def fetch_geoprofiles_xml(id: String): IO[Elem] = fetch_xml("geoprofiles", id)
  def fetch_nucgss_xml(id: String): IO[Elem] = fetch_xml("nucgss", id)
  def fetch_homologene_xml(id: String): IO[Elem] = fetch_xml("homologene", id)
  def fetch_mesh_xml(id: String): IO[Elem] = fetch_xml("mesh", id)
  def fetch_ncbisearch_xml(id: String): IO[Elem] = fetch_xml("ncbisearch", id)
  def fetch_toolkit_xml(id: String): IO[Elem] = fetch_xml("toolkit", id)
  def fetch_nlmcatalog_xml(id: String): IO[Elem] = fetch_xml("nlmcatalog", id)
  def fetch_nuccore_xml(id: String): IO[Elem] = fetch_xml("nuccore", id)
  def fetch_omia_xml(id: String): IO[Elem] = fetch_xml("omia", id)
  def fetch_popset_xml(id: String): IO[Elem] = fetch_xml("popset", id)
  def fetch_probe_xml(id: String): IO[Elem] = fetch_xml("probe", id)
  def fetch_protein_xml(id: String): IO[Elem] = fetch_xml("protein", id)
  def fetch_proteinclusters_xml(id: String): IO[Elem] = fetch_xml("proteinclusters", id)
  def fetch_pcassay_xml(id: String): IO[Elem] = fetch_xml("pcassay", id)
  def fetch_pccompound_xml(id: String): IO[Elem] = fetch_xml("pccompound", id)
  def fetch_pcsubstance_xml(id: String): IO[Elem] = fetch_xml("pcsubstance", id)
  def fetch_pubmed_xml(id: String): IO[Elem] = fetch_xml("pubmed", id)
  def fetch_pmc_xml(id: String): IO[Elem] = fetch_xml("pmc", id)
  def fetch_snp_xml(id: String): IO[Elem] = fetch_xml("snp", id)
  def fetch_sra_xml(id: String): IO[Elem] = fetch_xml("sra", id)
  def fetch_sra_runinfo(id: String): String = {
    val db = "sra"
    val returnType = "runinfo"
    requests.get(s"http://trace.ncbi.nlm.nih.gov/Traces/sra/sra.cgi?save=efetch&db=${db}&rettype=${returnType}&term=${id}${apiKeyAddition}")
      .text()
      .replace("\r\n", "\n")
  }

  def getSRA(id: String): Vector[RunInfo] = {
    val run_str = fetch_sra_runinfo(id: String).replace("\r\n", "\n")
    RunInfo.fromCSV(run_str).toVector
  }

  def fetch_structure_xml(id: String): IO[Elem] = fetch_xml("structure", id)
  def fetch_taxonomy_xml(id: String): IO[Elem] = fetch_xml("taxonomy", id)
  def fetch_unigene_xml(id: String): IO[Elem] = fetch_xml("unigene", id)
  def fetch_unists_xml(id: String): IO[Elem] = fetch_xml("unists", id)

  /**
    * @param db database
    * @param id id of the object
    * @return
    */

  def fetch_xml(db: String, id: String, returnType: String = "fullxml"): IO[Elem] = {
    Hammock
      .request(Method.GET, uri"http://trace.ncbi.nlm.nih.gov/Traces/sra/sra.cgi?save=efetch&db=${db}&rettype=${returnType}&term=${id}${apiKeyAddition}", Map()) // In the `request` method, you describe your HTTP request
      .map{r=> r.entity match {
        case hammock.Entity.StringEntity(body, _) => body
        case other => throw new Exception(s"request ${r.toString} gave non string entity ${other}!")
        }
      }
      .map(s=> scala.xml.XML.loadString(s))
      .exec[IO]
  }
}

trait Fetch {
  implicit def interpreter: InterpTrans[IO]

  def apiKey: String


  def apiKeyAddition: String = if(apiKey=="") "" else s"&api_key=${apiKey}"

}