package geo.models

import io.circe.Json
import io.circe.Json.JArray
import kantan.csv._
import kantan.csv.ops._
import shapeless._
import shapeless.record._
import io.circe.generic.JsonCodec     // Enriches types with useful methods.



object RunLibrary{

  implicit val runLibraryDecoder: HeaderDecoder[RunLibrary] =
    HeaderDecoder.decoder("LibraryName","LibraryStrategy","LibrarySelection","LibrarySource","LibraryLayout"
    )(RunLibrary.apply)

}

@JsonCodec case class RunLibrary(
                                  LibraryName: String,	LibraryStrategy: String,	LibrarySelection: String,
                                  LibrarySource: String,	LibraryLayout: String
                                )

object RunStats {
  implicit val runStatsDecoder: HeaderDecoder[RunStats] =
    HeaderDecoder.decoder("spots","bases","spots_with_mates","avgLength","size_MB"
    )(RunStats.apply)
}

@JsonCodec case class RunStats(
                                spots: Long,
                                bases: Long,
                                spots_with_mates: Long,
                                avgLength: Long,
                                size_MB: Double
                              )

object GeneralSampleInfo {
  implicit val generalSampleDecoder: HeaderDecoder[GeneralSampleInfo] =
    HeaderDecoder.decoder("Platform","Model","SRAStudy","BioProject","Study_Pubmed_id","ProjectID","Sample","BioSample","SampleType","TaxID","ScientificName",
    "SampleName"
    )(GeneralSampleInfo.apply)
}

@JsonCodec case class GeneralSampleInfo(
                              Platform: String,
                              Model: String,
                              SRAStudy: String,
                              BioProject: String,
                              Study_Pubmed_id: String,
                              ProjectID: String,
                              Sample: String,
                              BioSample: String,
                              SampleType: String,
                              TaxID: String,
                              ScientificName: String,
                              SampleName: String,
                            )
object SubjectInfo {
  implicit val subjectInfoDecoder: HeaderDecoder[SubjectInfo] =
    HeaderDecoder.decoder("Subject_ID","Sex","Disease","Tumor","Affection_Status","Analyte_Type",
      "Histological_Type","Body_Site"
    )(SubjectInfo.apply)
  protected val labeledGen =  LabelledGeneric[SubjectInfo]

  protected val gen = Generic[SubjectInfo]

  def asMap(info: SubjectInfo) = labeledGen.to(info).toMap//[String, String]

}

@JsonCodec case class SubjectInfo(
                                   Subject_ID: String,
                                   Sex: String,
                                   Disease: String,
                                   Tumor: String,
                                   Affection_Status: String,
                                   Analyte_Type: String,
                                   Histological_Type: String,
                                   Body_Site: String
                                 )
{
  lazy val asMap= SubjectInfo.asMap(this)
}
object MainRunInfo {
  implicit val mainRunInfoDecoder: HeaderDecoder[MainRunInfo] =
    HeaderDecoder.decoder("Run",
      "ReleaseDate",
      "LoadDate",
      "AssemblyName",
      "download_path",
      "Experiment",
    )(MainRunInfo.apply)
}

@JsonCodec case class MainRunInfo(
                        Run: String,
                        ReleaseDate: String,
                        LoadDate: String,
                        AssemblyName: String,
                        download_path: String,
                        Experiment: String,
                      )
object OtherRunInfo {
  implicit val otherRunInfo: HeaderDecoder[OtherRunInfo] =
    HeaderDecoder.decoder(
      "InsertSize",
      "InsertDev",
      "g1k_pop_code",
      "source",
      "g1k_analysis_group",
      "CenterName",
      "Submission",
      "dbgap_study_accession",
      "Consent",
      "RunHash",
      "ReadHash"
    )(OtherRunInfo.apply)
}
@JsonCodec case class OtherRunInfo(
                         InsertSize: String,
                         InsertDev: String,
                         g1k_pop_code: String,
                         source: String,
                         g1k_analysis_group: String,
                         CenterName: String,
                         Submission: String,
                         dbgap_study_accession: String,
                         Consent: String,
                         RunHash: String,
                         ReadHash: String
                       )
@JsonCodec case class RunInfo(
                    run: MainRunInfo,
                    stats: RunStats,
                    library: RunLibrary,
                    sample: GeneralSampleInfo,
                    subject: SubjectInfo,
                    other: OtherRunInfo
                    )
{
  /*
  lazy val asRecord = RunInfo.labeledGen.to(this)
  def asMap = asRecord.toMap
  def keys = asRecord.keys
  def fieldNames = keys.toList.map(_.toString.replace("'", ""))

  lazy val asGen = RunInfo.gen.to(this)
  def asList = asGen.toList
  def asStringList = asList.map(_.toString)
  */

  def toFlatJSON: Json = {
    import io.circe.syntax._
    this.run.asJson
      .deepMerge(this.subject.asJson)
      .deepMerge(this.sample.asJson)
      .deepMerge(this.library.asJson)
      .deepMerge(this.stats.asJson)
      .deepMerge(this.other.asJson)
  }

}
/*
case class RunInfo(
                    Run: String,
                    ReleaseDate: String,
                    LoadDate: String,
                    stats: RunStats,
                    AssemblyName: String,
                    download_path: String,
                    Experiment: String,
                    library: RunLibrary,
                    InsertSize: String,
                    InsertDev: String,
                    generalSampleInfo: GeneralSampleInfo,
                    g1k_pop_code: String,
                    source: String,
                    g1k_analysis_group: String,
                    subjectInfo: SubjectInfo,
                    CenterName: String,
                    Submission: String,
                    dbgap_study_accession: String,
                    Consent: String,
                    RunHash: String,
                    ReadHash: String)
 */

object RunInfo {

  implicit val runInfoDecoder: HeaderDecoder[RunInfo] = new HeaderDecoder[RunInfo] {

    override def fromHeader(headers: Seq[String]) =
    // Turns the header into row decoders for Model1 and Model2
      for {
        mainInfoDecoder <- MainRunInfo.mainRunInfoDecoder.fromHeader(headers)
        statsDecoder <- RunStats.runStatsDecoder.fromHeader(headers)
        libraryDecoder <- RunLibrary.runLibraryDecoder.fromHeader(headers)
        generalDecoder <- GeneralSampleInfo.generalSampleDecoder.fromHeader(headers)
        subjectInfoDecoder <- SubjectInfo.subjectInfoDecoder.fromHeader(headers)
        otherDecoder <- OtherRunInfo.otherRunInfo.fromHeader(headers)
      } yield
        // Merge these two decoders into a single RowDecoder
        RowDecoder.from { row =>
          for {
            mainInfo <- mainInfoDecoder.decode(row)
            stats <- statsDecoder.decode(row)
            library <- libraryDecoder.decode(row)
            general <- generalDecoder.decode(row)
            subjectInfo <- subjectInfoDecoder.decode(row)
            other <- otherDecoder.decode(row)
          } yield RunInfo(mainInfo, stats, library, general, subjectInfo, other)
        }

    override def noHeader = sys.error("Attempting to decode RunInfo without a header.")
  }
  /*
  implicit val runinfoCodec: HeaderCodec[RunInfo] = HeaderCodec.caseCodec(
    "Run","ReleaseDate","LoadDate","spots","bases","spots_with_mates","avgLength","size_MB","AssemblyName","download_path",
    "Experiment","LibraryName","LibraryStrategy","LibrarySelection","LibrarySource","LibraryLayout","InsertSize","InsertDev",
    "Platform","Model","SRAStudy","BioProject","Study_Pubmed_id","ProjectID","Sample","BioSample","SampleType","TaxID","ScientificName",
    "SampleName","g1k_pop_code","source","g1k_analysis_group","Subject_ID","Sex","Disease","Tumor","Affection_Status","Analyte_Type",
    "Histological_Type","Body_Site","CenterName","Submission","dbgap_study_accession","Consent","RunHash","ReadHash"
  )//(RunInfo.apply)(RunInfo.unapply)
  */
  protected val labeledGen =  LabelledGeneric[RunInfo]

  protected val gen = Generic[RunInfo]

  def asMap(info: RunInfo) = labeledGen.to(info).toMap

  def fromCSV(str: String, separator: Char = ','): List[RunInfo] = {
    val s = str.replaceAll("\n+", "\n")
    s.asCsvReader[RunInfo](rfc.withHeader.withCellSeparator(separator)).map{
      case Right(r)=> r
      case Left(e) => throw new Exception(s"Cannot parse runinfo with error ${e}!")
    }.toList
  }
}