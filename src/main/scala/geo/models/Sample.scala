package geo.models

import io.circe.Json
import io.circe.generic.JsonCodec
import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec, JsonKey}     // Enriches types with useful methods.

object GSM {

  def apply(gsm: String, mp: Map[String, Seq[String]]): GSM = {

    def prop(property: String, delimiter: String = "\n", default: String = "") = mp(property) match {
      case seq if seq.isEmpty => default
      case seq => seq.reduce(_ + delimiter + _)
    }

    def propMap(property: String): Map[String, String] = mp(property).filter(i=>i.contains(":")).map{ v=>
      v.substring(0, v.indexOf(":")).trim -> v.substring(v.indexOf(":")+1).trim
    }.toMap


    val lib = Library(
      prop("Sample_library_strategy"),
      prop("Sample_library_selection"),
      prop("Sample_library_source")
    )

    val extraction = Extraction(
      prop("Sample_source_name_ch1"),
      prop("Sample_molecule_ch1"),
      prop("Sample_extract_protocol_ch1"),
      prop("Sample_data_processing")
    )
    val gsm = prop("Sample_geo_accession")
    val organism = Organism(
       prop("Sample_organism_ch1"),
      prop("Sample_taxid_ch1")
    )
    val title = prop("Sample_title")
    val sequencer = prop("Sample_instrument_model")
    val status = Status(
      prop("Sample_submission_date"),
      prop("Sample_last_update_date")
    )
    val series = prop("Sample_series_id").split("\n").toList

    //val gpl = prop("Sample_platform_id", ";")
    val tp = prop("Sample_type")

    val characteristics = propMap("Sample_characteristics_ch1")


    val relations = propMap("Sample_relation")

    GSM(gsm, series, title, tp, organism, sequencer, characteristics, lib, extraction, relations, status, Vector.empty)
  }

}

@JsonCodec case class Extraction(
                     source: String,
                     molecule: String,
                     protocol: String,
                     processing: String
                     )

@JsonCodec case class Organism(
                   name: String,
                   taxid: String
                   )
/*
@JsonCodec case class Relations(relations: Map[String, String]) {

  private def getTerm(str: String) = str.substring(str.indexOf("term=") + "term=".length)

  lazy val bioSample: Option[String] = relations.get("BioSample")
  lazy val srx: Option[String] = relations.get("SRX").map(getTerm).orElse(sra.filter(_.contains("SRX")))
  lazy val sra: Option[String] = relations.get("SRA").map(getTerm)
}
*/

object Library{

/*
  implicit val model1Decoder: HeaderDecoder[Library] =
    HeaderDecoder.decoder("LibraryName","LibraryStrategy","LibrarySelection","LibrarySource","LibraryLayout"
  )(Library.apply)
*/
}
@JsonCodec case class Library(
                  strategy: String,
                  selection: String,
                  source: String
                  )


@JsonCodec case class Status(submitted: String, updated: String)

/*
@JsonCodec case class Characteristics(characteristics: Map[String, String]) {

  def ageRelated = characteristics.filter(_._1.toLowerCase.contains("age"))
}
*/

trait GSMLike {
  def id: String
  def gse: List[String]
  def title: String
  def sampleType: String
  def organism: Organism
  def sequencer: String
  def characteristics: Map[String, String]//Characteristics
  def library: Library
  def extraction: Extraction
  def relations: Map[String, String]
  def status: Status


  private def getTerm(str: String) = str.substring(str.indexOf("term=") + "term=".length)

  lazy val bioSample: Option[String] = relations.get("BioSample")
  lazy val srx: Option[String] = relations.get("SRX").map(getTerm).orElse(sra.filter(_.contains("SRX")))
  lazy val sra: Option[String] = relations.get("SRA").map(getTerm)
}

@JsonCodec case class GSM(id: String, gse: List[String], title: String,
               sampleType: String,
               organism: Organism,
               sequencer: String,
               characteristics: Map[String, String], //Characteristics
               library: Library, extraction: Extraction,
               relations: Map[String, String], status: Status, runs: Vector[RunInfo]) extends GSMLike

object BioProject {

  val withUpperCaseKeys: String ⇒ String =  str => str.toUpperCase
  implicit val customConfig: Configuration = Configuration.default.withDefaults.copy(transformMemberNames = withUpperCaseKeys)


  import io.circe._
  import io.circe.generic.JsonCodec




  object ExperimentSet {
    @ConfiguredJsonCodec case class Experiment(@JsonKey("accession") accession: String,
                                               @JsonKey("alias") alias: String,
                                               identifiers: Json,
                                               title: String,
                                               study_ref: Json,
                                               design: Json,
                                               platform: Json,
                                               @JsonKey("center_name") centerName: Option[String],
                                               processing: Option[Json]
                                              )


    @ConfiguredJsonCodec case class Study(
                      @JsonKey("accession") accession: String,
                      @JsonKey("alias") alias: String,
                      @JsonKey("center_name") centerName: String,
                      identifiers: Json,
                      descriptor: Json

                    )

    @ConfiguredJsonCodec case class SampleAttribute(tag: String, value: String)
    @ConfiguredJsonCodec case class SampleAttributes(
                                 sample_attribute: Vector[SampleAttribute]
                               )
    {
      lazy val characteristics = sample_attribute.map(a=>a.tag + ":" + a.value).mkString(";")
    }

    @ConfiguredJsonCodec case class SampleName(
                           taxon_id: Int,
                           scientific_name: String
                         )

    @ConfiguredJsonCodec case class Sample(
                                            @JsonKey("accession") accession: String,
                                            @JsonKey("alias") alias: String,
                                            identifiers: Json,
                                            title: Option[String],
                                            sample_name: SampleName,
                                            sample_links: Option[Json],
                                            sample_attributes: SampleAttributes
                                          )
    /*
 "SAMPLE" : {
[info]         "accession" : "SRS4436074",
[info]         "alias" : "36",
[info]         "IDENTIFIERS" : {
[info]           "PRIMARY_ID" : "SRS4436074",
[info]           "EXTERNAL_ID" : "SAMN11044060",
[info]           "namespace" : "BioSample"
[info]         },
[info]         "TITLE" : "SAMN36",
[info]         "SAMPLE_NAME" : {
[info]           "TAXON_ID" : "9031",
[info]           "SCIENTIFIC_NAME" : "Gallus gallus"
[info]         },
[info]         "SAMPLE_LINKS" : {
[info]           "SAMPLE_LINK" : [
[info]             {
[info]               "XREF_LINK" : {
[info]                 "DB" : "bioproject",
[info]                 "ID" : "3",
[info]                 "LABEL" : "PRJNA3"
[info]               }
[info]             },
[info]             {
[info]               "XREF_LINK" : {
[info]                 "DB" : "bioproject",
[info]                 "ID" : "525241",
[info]                 "LABEL" : "PRJNA525241"
[info]               }
[info]             }
[info]           ]
[info]         },
[info]         "SAMPLE_ATTRIBUTES" : {
[info]           "SAMPLE_ATTRIBUTE" : [
[info]             {
[info]               "TAG" : "breed",
[info]               "VALUE" : "rooster"
[info]             },
[info]             {
[info]               "TAG" : "ecotype",
[info]               "VALUE" : "hongjiang"
[info]             },
[info]             {
[info]               "TAG" : "age",
[info]               "VALUE" : "200days"
[info]             },
[info]             {
[info]               "TAG" : "dev_stage",
[info]               "VALUE" : "adult cock"
[info]             },
[info]             {
[info]               "TAG" : "sex",
[info]               "VALUE" : "male"
[info]             },
[info]             {
[info]               "TAG" : "tissue",
[info]               "VALUE" : "blood"
[info]             },
[info]             {
[info]               "TAG" : "individual",
[info]               "VALUE" : "36"
[info]             },
[info]             {
[info]               "TAG" : "BioSampleModel",
[info]               "VALUE" : "Model organism or animal"
[info]             }
[info]           ]
[info]         }
[info]       },
     */

    @ConfiguredJsonCodec case class ExperimentPackage(
                                                       experiment: Experiment,
                                                       submission: Json,
                                                       @JsonKey("Organization") organization: Json,
                                                       study: Study,
                                                       sample: Sample,
                                                       @JsonKey("Pool") pool: Json,
                                                       run_set: Json)
  }
  import ExperimentSet._
  @ConfiguredJsonCodec case class ExperimentSet(experiments: Vector[ExperimentPackage])
  {
    lazy val experimentIds: Vector[String] = experiments.map(_.experiment.accession)
    lazy val experimentMap: Vector[(String, ExperimentPackage)] = experiments.map(e=>e.experiment.accession -> e)
  }

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