package geo.fetch
import cats.effect.IO
import io.circe.generic.auto._
import hammock._
import hammock.marshalling._
import hammock._
import hammock.apache.ApacheInterpreter
import hammock.circe.implicits._
import io.circe._
import Xml._
import fastparse.Parsed
import geo.models.BioProject.ExperimentSet.ExperimentPackage
import geo.models.{BioProject, GSM, RunInfo}
import hammock.Entity.StringEntity
import io.circe.Decoder.Result
import io.circe.optics.JsonPath.root
import kantan.csv.HeaderCodec
import org.apache.http.impl.client.{CloseableHttpClient, HttpClientBuilder}

import better.files.File
import geo.cli.CommandsBioProject
import geo.models.RunInfo
import kantan.csv.{CsvConfiguration, HeaderCodec, rfc}
import geo.extras._
import scala.xml.Elem

case class FetchGEO(apiKey: String = "") extends FetchGeoJSON {
  //implicit val client: CloseableHttpClient = HttpClientBuilder.create().disableCookieManagement().disableContentCompression().build()
  implicit val interpreter: InterpTrans[IO] = ApacheInterpreter.instance


  def getRunAnnotation(run: String, series: String = "") = {
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


  //def fetchGSM(gsm: String): IO[Result[MINiML.Container]] = get_gsm_json(gsm).map(_.as[MINiML.Container])

  def get_query_json(target: String, id: String): IO[Json] = get_query_xml(target, id).map(_.toJson)
  def get_gse_json(id: String): IO[Json] = get_query_json("gse", id)
  def get_gsm_json(id: String): IO[Json] = get_query_json("gsm", id)

  def get_query_soft(target: String, id: String): IO[Json] = get_query_xml(target, id).map(_.toJson)


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



  def runsFromExperiment(e: BioProject.ExperimentSet.ExperimentPackage) = {
    e -> getSRA(e.experiment.accession)
  }

  def runsFromExperiments(exp: BioProject.ExperimentSet): Vector[(ExperimentPackage, Vector[RunInfo])] = {
    exp.experiments.map(runsFromExperiment)
  }


  def parseExperiment(json: Json): ExperimentPackage = {
    exp.getOption(json).map(_.as[BioProject.ExperimentSet.ExperimentPackage]).map{
      case Right(value) => value
    }.get
  }

  def getExperiment(id: String): ExperimentPackage = {
    val json = fetch_bioproject_json(id).unsafeRunSync()
    parseExperiment(json)
  }

  def parseBioProject(json: Json): BioProject.ExperimentSet = {
    val decoded: Vector[Result[BioProject.ExperimentSet.ExperimentPackage]] = expArr.getOption(json).map(v=> v.map(_.as[BioProject.ExperimentSet.ExperimentPackage])).getOrElse(Vector.empty)
    for( Left(e) <- decoded.filter(_.isLeft)) println(s"ERROR in decoding bioproject: ${e.toString()}")
    val packages = decoded.collect{ case Right(v) => v}
    BioProject.ExperimentSet(packages)
  }

  def getBioProject(id: String): BioProject.ExperimentSet = {
    val json = fetch_bioproject_json(id).unsafeRunSync()
    parseBioProject(json)
  }
}

trait FetchGeoXML extends Fetch {


  def get_query(id: String, target: String, form: String, view: String = "full"): IO[String] =
    Hammock
      .request(Method.GET, uri"https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=${id}&targ=${target}&form=${form}&view=${view}${apiKeyAddition}", Map()) // In the `request` method, you describe your HTTP request
      .map{r=> r.entity match {
          case hammock.Entity.StringEntity(body, contentType) => body
          case other => throw new Exception(s"request ${r.toString} gave non string entity ${other}!")
        }
      }
      .exec[IO]

  def get_query_xml(id: String, target: String): IO[Elem] = get_query(id, target, "xml", "full").map(r=> scala.xml.XML.loadString(r))

  def get_query_text(id: String, target: String): String = {
    val form = "text"
    val view = "full"
    requests.get(s"https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=${id}&targ=${target}&form=${form}&view=${view}${apiKeyAddition}").text()
  }

  def get_gse_xml(id: String): IO[Elem] = get_query_xml(id, "gse")
  def get_gsm_xml(id: String): IO[Elem] = get_query_xml(id, "gsm")

  /**
    *
    * ^	caret lines	entity indicator line
    * !	bang lines	entity attribute line
    * #	hash lines	data table header description line
    * n/a	data lines	data table row
    */
  //protected def parseGSM(text: String) = text.split("!")

}

/*
^SAMPLE = GSM1698570
!Sample_title = Biochain_Adult_Kidney
!Sample_geo_accession = GSM1698570
!Sample_status = Public on May 29 2015
!Sample_submission_date = May 29 2015
!Sample_last_update_date = Jun 01 2015
!Sample_type = SRA
!Sample_channel_count = 1
!Sample_source_name_ch1 = Biochain Adult Kidney
!Sample_organism_ch1 = Homo sapiens
!Sample_taxid_ch1 = 9606
!Sample_characteristics_ch1 = vendor: Biochain
!Sample_characteristics_ch1 = tissue: Kidney
!Sample_characteristics_ch1 = gender: Male
!Sample_characteristics_ch1 = number of donors: 1
!Sample_characteristics_ch1 = age: 26 years old
!Sample_characteristics_ch1 = isolate: Lot no.: B106007
!Sample_molecule_ch1 = total RNA
!Sample_extract_protocol_ch1 = 2 different fetal normal tissues and 6 different adult normal tissues were purchased from different sources (Agilent, Biochain and OriGene). The qualities of these total RNA were tested using the Agilent Bioanalyzer 2100 Eukaryote Total RNA Nano Series II. Only total RNAs with a RIN score of more than 7 were used for RNA-Seq library construction
!Sample_extract_protocol_ch1 = Ribosomal RNA (rRNA) was removed from total RNA using the RiboMinus™ Eukaryote Kit for RNA-Seq from Ambion. The ribosomal RNA depleted RNA fraction is termed the RiboMinus™ RNA fraction and is enriched in polyadenylated (polyA) mRNA, non-polyadenylated RNA, pre-processed RNA, tRNA, and may also contain regulatory RNA molecules such as microRNA (miRNA) and short interfering RNA (siRNA), snRNA, and other RNA transcripts of yet unknown function. Ambion RiboMinus rRNA depletion was performed as described in the manufacturer’s protocol (Pub. Part no.: 100004590, Rev. date 2 December 2011) following the standard protocol.
!Sample_extract_protocol_ch1 = TruSeq RNA Sample Preparation was performed on the RiboMinus™ RNA fraction as described in the manufacturer’s protocol (Pub. Part no.: 15026495 Rev. F March 2014) following the low sample protocol.
!Sample_extract_protocol_ch1 = The libraries were sequenced on Illumina’s HiSeq 2000 instrument following standard protocol.
!Sample_data_processing = Data quality check using fastQC version 0.11.2.
!Sample_data_processing = Alignment of unpaired unstranded reads using STAR version 2.4.0.
!Sample_data_processing = Quantification of transcripts and isoforms using RSEM version 1.2.21 using rsem-calculate-expression, both alignment and quantification was done using the STAR_RSEM.sh pipeline (https://github.com/ENCODE-DCC/long-rna-seq-pipeline/blob/master/DAC/STAR_RSEM.sh)
!Sample_data_processing = The programe featurecounts version 1.4.6-p2 from the SourceForge Subread package was used to produce a summary file of counts from all the alignement .bam files.
!Sample_data_processing = The summary file of counts (RNAseq.counts) was used to plot the multidimensional scaling plot using edgeR version 3.1.3.
!Sample_data_processing = The *.osc.gz files were loaded into the genome browser ZENBU and was used visualize the transcripts. Screen shots were captured.
!Sample_data_processing = Genome_build: hg19 with Gencode V19 annotation
!Sample_data_processing = Supplementary_files_format_and_content: .osc files are simple tab delimited files. They were generated by combining the isoform.results files outputed by RSEM with the gencode v19 .gtf file. It contains abundance measurements and transcript isoforms. It also contains metadata that is inputed into ZENBU.
!Sample_data_processing = Supplementary_files_format_and_content: RNAseq.counts is a simple tab delimited file containing the counts for all the RNA-seq libraries for each gene (summary file of counts).
!Sample_platform_id = GPL11154
!Sample_contact_name = Melissa,Jane,Fullwood
!Sample_contact_laboratory = Fullwood Lab
!Sample_contact_department = Centre for Translational Medicine
!Sample_contact_institute = Cancer Science Institute of Singapore
!Sample_contact_address = 14 Medical Drive, #12-01
!Sample_contact_city = Singapore
!Sample_contact_zip/postal_code = 117599
!Sample_contact_country = Singapore
!Sample_instrument_model = Illumina HiSeq 2000
!Sample_library_selection = cDNA
!Sample_library_source = transcriptomic
!Sample_library_strategy = RNA-Seq
!Sample_relation = BioSample: https://www.ncbi.nlm.nih.gov/biosample/SAMN03610552
!Sample_relation = SRA: https://www.ncbi.nlm.nih.gov/sra?term=SRX1020497
!Sample_supplementary_file_1 = ftp://ftp.ncbi.nlm.nih.gov/geo/samples/GSM1698nnn/GSM1698570/suppl/GSM1698570_Biochain_Adult_Kidney_latest.osc.txt.gz
!Sample_series_id = GSE69360
!Sample_data_row_count = 0
 */
