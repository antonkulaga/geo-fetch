package geo
import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}
import kantan.csv.{HeaderCodec, RowDecoder}
import io.circe.Json
import io.circe.Json.JArray
import kantan.csv._
import kantan.csv.ops._
import shapeless._
import shapeless.record._
import io.circe.generic.JsonCodec
import kantan.codecs.Encoder     // Enriches types with useful methods.


package object extras {
  implicit val customConfig: Configuration = Configuration.default.withDefaults

  @ConfiguredJsonCodec case class SalmonInfo(salmon_version: String,
                                             index: String,
                                             numBootstraps: String,
                                             threads: String,
                                             libType: String,
                                             seqBias: List[String] = Nil,
                                             gcBias: List[String] = Nil,
                                             validateMappings: List[String] = Nil,
                                             rangeFactorizationBins: String = "transcripts_quant",
                                             output: String,
                                             unmatedReads: String = "",
                                             auxDir: String = "aux_info",
                                             modified: String = ""
                                            )

  object RunAnnotation {
    implicit val runAnnotationCodec: HeaderCodec[RunAnnotation] = HeaderCodec.caseCodec(
      "bioproject", "series",		"run",
      "organism",	"taxid",
      "sample_name",	"sequencer",
      "library_strategy", "library_layout",	"library_selection",
      "study", "study_title",
      "characteristics","source","age", "sex", "tumor", "protocol"
      )(RunAnnotation.apply)(RunAnnotation.unapply)
  }
  case class RunAnnotation(
                        bioproject: String, series: String, run: String,
                        organism: String, taxid: String,
                        sample_name: String,  sequencer: String,
                        library_strategy: String, library_layout: String, library_selection: String,
                        study: String, study_title: String,
                        characteristics: String, source: String, age: String, sex: String, tumor: String, protocol: String
                      )

  object QuantAnnotation {
    implicit val quantCodec: HeaderCodec[QuantAnnotation] = HeaderCodec.caseCodec(
       "salmon_version",
      "index", "genes", "transcripts", "quant",  "percent_mapped","libType", "numBootstraps", "modified")(QuantAnnotation.apply)(QuantAnnotation.unapply)
    lazy val empty = QuantAnnotation("", "", "", "", "", "", "", "", "")
  }

  case class QuantAnnotation(salmon_version: String,
                             index: String, genes: String, transcripts: String, quant: String,
                             percent_mapped: String, libType: String, numBootstraps: String, modified: String)  {
    def withSalmonInfo(salmonInfo: SalmonInfo): QuantAnnotation = {
      copy(salmon_version = salmonInfo.salmon_version,  index = salmonInfo.index, libType = salmonInfo.libType,
        numBootstraps = salmonInfo.numBootstraps)
    }

    def isEmpty: Boolean = QuantAnnotation.empty == this
  }

  object AnnotatedRun {
    val runInfoEncoder: HeaderEncoder[AnnotatedRun]  = new HeaderEncoder[AnnotatedRun]{
      override def header: Option[Seq[String]] = RunAnnotation.runAnnotationCodec.header.flatMap(h => QuantAnnotation.quantCodec.header.map(hh=> h++hh))

      override def rowEncoder: RowEncoder[AnnotatedRun] = new RowEncoder[AnnotatedRun]{
        override def encode(d: AnnotatedRun): Seq[String] = {
          RunAnnotation.runAnnotationCodec.rowEncoder.encode(d.runAnnotation) ++ QuantAnnotation.quantCodec.rowEncoder.encode(d.quantAnnotation) //++ Seq(d.protocol)
        }
      }
    }

    val runInfoDecoder: HeaderDecoder[AnnotatedRun]  = new HeaderDecoder[AnnotatedRun]{
    override def fromHeader(headers: Seq[String])  =
      // Turns the header into row decoders for Model1 and Model2
      for {
        a <- RunAnnotation.runAnnotationCodec.fromHeader(headers)
        q <- QuantAnnotation.quantCodec.fromHeader(headers)
      } yield
        // Merge these two decoders into a single RowDecoder
        RowDecoder.from { row =>
          for {
            runAnnotation <- a.decode(row)
            quantAnnotation <- q.decode(row)
            //protocol <- Right(row.last)
          } yield AnnotatedRun(runAnnotation, quantAnnotation) //, protocol)
        }

      override def noHeader = sys.error("Attempting to decode RunInfo without a header.")
    }

    implicit val runInfoCodec = HeaderCodec.from(runInfoDecoder)(runInfoEncoder)
  }
  case class AnnotatedRun(
                          runAnnotation: RunAnnotation, quantAnnotation: QuantAnnotation//, protocol: String
                         ) {
    lazy val notQuantified: Boolean = quantAnnotation.isEmpty
  }
}
