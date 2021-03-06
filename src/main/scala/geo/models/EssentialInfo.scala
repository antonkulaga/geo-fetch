package geo.models

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}
import kantan.csv.HeaderCodec

import kantan.csv._
import kantan.csv.ops._
import shapeless._
import shapeless.record._
import io.circe.generic.JsonCodec     // Enriches types with useful methods.

object EssentialInfo {

  implicit val sampleCodec: HeaderCodec[EssentialInfo] = HeaderCodec.caseCodec(
    "gsm", "series", "run",  "path",
    "organism", "model", "layout", "strategy",
    "title", "name", "characteristics"
    )(EssentialInfo.apply)(EssentialInfo.unapply)

  def extract(gsm: GSM): Vector[EssentialInfo] = {
    gsm.runs.map{
      case RunInfo( run: MainRunInfo, _, library: RunLibrary, sample: GeneralSampleInfo, _, _) =>
        EssentialInfo(gsm.id, gsm.gse.mkString(";"), run.Run, run.download_path,
          sample.ScientificName, sample.Model, library.LibraryLayout, library.LibraryStrategy,
          gsm.title, sample.SampleName, gsm.characteristics.mkString(";")

        )
    }
  }

  def extract(project: String, experiment: String, runs: Vector[RunInfo], title: String, characteristics: String): Vector[EssentialInfo] = {
    runs.map{
      case RunInfo( run: MainRunInfo, _, library: RunLibrary, sample: GeneralSampleInfo, _, _) =>
        EssentialInfo(project, experiment, run.Run, run.download_path,
          sample.ScientificName, sample.Model, library.LibraryLayout, library.LibraryStrategy,
          title, sample.SampleName, characteristics
        )
    }
  }
}
@JsonCodec case class EssentialInfo(gsm: String, series: String, run: String,  path: String,
                                    organism: String, model: String, layout: String, strategy: String,
                                    title: String, name: String, characteristics: String)
