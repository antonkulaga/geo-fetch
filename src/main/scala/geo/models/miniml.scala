package geo.models

import io.circe.Json
import io.circe.generic.extras._

object MINiML {
  implicit val config: Configuration = Configuration.default
  @ConfiguredJsonCodec  case class Container(@JsonKey("MINiML") content: MINiML)
  @ConfiguredJsonCodec  case class MINiML(
                                           version: String,
                                           schemaLocation: String,
                                           Contributor: Json,
                                           Database: Database,
                                           Platform: Json,
                                           Sample: Sample
                                         )

  @ConfiguredJsonCodec case class Sample(iid: String, Status: Status, Title: String, Accession: String, Type: String,
                                         Channel: Channel,
                                         @JsonKey("Data-Processing") dataProcessing: String,
                                         ref: String,
                                         @JsonKey("Library-Strategy") libraryStrategy: String,
                                         @JsonKey("Library-Source") librarySource: String,
                                         @JsonKey("Library-Selection") librarySelection: String,
                                         @JsonKey("Instrument-Model") instrument: Instrument,
                                         @JsonKey("Supplementary-Data") supplementaryData: String,
                                         target: String,
                                         `type`: String
                                        )
  {
    def srx: String = {
      val part = "term="
      val i = target.indexOf(part)
      target.substring(i + part.length)
    }
  }
  @ConfiguredJsonCodec case class Status(database: String,
                                         @JsonKey("Submission-Date") submissionDate: String,
                                         @JsonKey("Release-Date") releaseDate: String, @JsonKey("Last-Update-Date") lastUpdateDate: String
                                        )

  @ConfiguredJsonCodec case class Channel(
                                         position: Int,
                                         Source: String,
                                         Organism: String,
                                         Characteristics: String,
                                         Molecule: String,
                                         @JsonKey("Extract-Protocol") extractProtocol: String
                                         )

  @ConfiguredJsonCodec case class Instrument(Predefined: String)
  @ConfiguredJsonCodec case class Database(iid: String, Name: String,
                                           @JsonKey("Public-ID") publicId: String,
                                           Organization: String,
                                           @JsonKey("Web-Link") webLink: String,
                                           Email: String)
  @ConfiguredJsonCodec case class Platform(iid: String, Accession: String)


  }
/*
 "version" : "0.5.0",
    "schemaLocation" : "http://www.ncbi.nlm.nih.gov/geo/info/MINiML http://www.ncbi.nlm.nih.gov/geo/info/MINiML.xsd",
    "Contributor" : {
      "iid" : "contrib1",
      "Person" : {
        "First" : "Melissa",
        "Middle" : "Jane",
        "Last" : "Fullwood"
      },
      "Laboratory" : "Fullwood Lab",
      "Department" : "Centre for Translational Medicine",
      "Organization" : "Cancer Science Institute of Singapore",
      "Address" : {
        "Line" : "14 Medical Drive, #12-01",
        "City" : "Singapore",
        "Postal-Code" : 117599,
        "Country" : "Singapore"
      }
    },
    "Database" : {
      "iid" : "GEO",
      "Name" : "Gene Expression Omnibus (GEO)",
      "Public-ID" : "GEO",
      "Organization" : "NCBI NLM NIH",
      "Web-Link" : "http://www.ncbi.nlm.nih.gov/geo",
      "Email" : "geo@ncbi.nlm.nih.gov"
    },
    "Platform" : {
      "iid" : "GPL11154",
      "Accession" : "GPL11154"
    },
    "Sample" : {
      "iid" : "GSM1698570",
      "Status" : {
        "database" : "GEO",
        "Submission-Date" : "2015-05-29",
        "Release-Date" : "2015-05-29",
        "Last-Update-Date" : "2015-06-01"
      },
      "Title" : "Biochain_Adult_Kidney",
      "Accession" : "GSM1698570",
      "Type" : "SRA",
      "Channel-Count" : 1,
      "Channel" : {
        "position" : 1,
        "Source" : "Biochain Adult Kidney",
        "Organism" : "Homo sapiens",
        "Characteristics" : "Lot no.: B106007      ",
        "Molecule" : "total RNA",
        "Extract-Protocol" : "2 different fetal normal tissues and 6 different adult normal tissues were purchased from different sources (Agilent, Biochain and OriGene). The qualities of these total RNA were tested using the Agilent Bioanalyzer 2100 Eukaryote Total RNA Nano Series II. Only total RNAs with a RIN score of more than 7 were used for RNA-Seq library constructionRibosomal RNA (rRNA) was removed from total RNA using the RiboMinus™ Eukaryote Kit for RNA-Seq from Ambion. The ribosomal RNA depleted RNA fraction is termed the RiboMinus™ RNA fraction and is enriched in polyadenylated (polyA) mRNA, non-polyadenylated RNA, pre-processed RNA, tRNA, and may also contain regulatory RNA molecules such as microRNA (miRNA) and short interfering RNA (siRNA), snRNA, and other RNA transcripts of yet unknown function. Ambion RiboMinus rRNA depletion was performed as described in the manufacturer’s protocol (Pub. Part no.: 100004590, Rev. date 2 December 2011) following the standard protocol.TruSeq RNA Sample Preparation was performed on the RiboMinus™ RNA fraction as described in the manufacturer’s protocol (Pub. Part no.: 15026495 Rev. F March 2014) following the low sample protocol.The libraries were sequenced on Illumina’s HiSeq 2000 instrument following standard protocol.      "
      },
      "Data-Processing" : "Data quality check using fastQC version 0.11.2.Alignment of unpaired unstranded reads using STAR version 2.4.0.Quantification of transcripts and isoforms using RSEM version 1.2.21 using rsem-calculate-expression, both alignment and quantification was done using the STAR_RSEM.sh pipeline (https://github.com/ENCODE-DCC/long-rna-seq-pipeline/blob/master/DAC/STAR_RSEM.sh)The programe featurecounts version 1.4.6-p2 from the SourceForge Subread package was used to produce a summary file of counts from all the alignement .bam files.The summary file of counts (RNAseq.counts) was used to plot the multidimensional scaling plot using edgeR version 3.1.3.The *.osc.gz files were loaded into the genome browser ZENBU and was used visualize the transcripts. Screen shots were captured.Genome_build: hg19 with Gencode V19 annotationSupplementary_files_format_and_content: .osc files are simple tab delimited files. They were generated by combining the isoform.results files outputed by RSEM with the gencode v19 .gtf file. It contains abundance measurements and transcript isoforms. It also contains metadata that is inputed into ZENBU.Supplementary_files_format_and_content: RNAseq.counts is a simple tab delimited file containing the counts for all the RNA-seq libraries for each gene (summary file of counts).    ",
      "ref" : "contrib1",
      "Library-Strategy" : "RNA-Seq",
      "Library-Source" : "transcriptomic",
      "Library-Selection" : "cDNA",
      "Instrument-Model" : {
        "Predefined" : "Illumina HiSeq 2000"
      },
      "Supplementary-Data" : "ftp://ftp.ncbi.nlm.nih.gov/geo/samples/GSM1698nnn/GSM1698570/suppl/GSM1698570_Biochain_Adult_Kidney_latest.osc.txt.gz    ",
      "target" : "https://www.ncbi.nlm.nih.gov/sra?term=SRX1020497",
      "type" : "SRA"
    }
  }

 */