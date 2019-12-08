package geo.cli
//import ammonite.ops._
import better.files.{File, _}
import File._
import java.io.{File => JFile}
import java.nio.file.Paths

import com.monovore.decline._
import geo.extras.{SampleSummarizer, SampleSummarizerLike}
import geo.fetch.{FetchGEO, _}
import kantan.csv.ops._
import cats.implicits._
import com.monovore.decline._

import scala.util._
import geo.extras._

trait CommandsIndex extends FetchCommand with SampleSummarizerLike {

  //protected lazy val essential: Opts[Boolean] = Opts.flag("essential", help = "Include only the most important information about the run", "e").orFalse
  /*
  protected lazy val runs: Opts[String] = Opts.option[String](long = "runs",
    short = "r",
    help = "Where to put SRA output")
    .withDefault("")
  */

  import java.nio.file.Path

  val index: Opts[String] = Opts.option[String]("index", "where to generate index file", "i").withDefault( "index.tsv")
  val base: Opts[Path] = Opts.option[Path]("base", "base folder to take samples from", "b").withDefault(Paths.get("/data/samples"))
  val species_indexes: Opts[Option[String]] = Opts.option[String]("species_indexes", "folder to write the indexes of species").orNone.withDefault(None)
  val rewrite: Opts[Boolean] = Opts.flag("rewrite", short = "r", help = "Should we rewrite already existing files?").orFalse
  val stable: Opts[Boolean] = Opts.flag("stable", short = "s", help = "Get's rid of unstable Ensemble id's").orTrue
  val verbose: Opts[Boolean] = Opts.flag("verbose", short = "v", help = "Prints more to the logs").orFalse
  val ignore: Opts[List[Path]] = Opts.options[Path]("ignore", help = "ignore folders when building index").orEmpty



  //lazy val key ="0a1d74f32382b8a154acacc3a024bdce3709"
  //implicit val ss = SampleSummarizer(FetchGEO(key))

  /**
    * Write indexes with runs to the disc
    * @param index file to write
    * @param species_indexes species_indexes folder
    * @param runs
    */
  def writeAnnotatedRuns(index: File,
                         species_indexes: Option[File],
                         runs: Seq[AnnotatedRun],
                         mergeTPMs: Boolean,
                         stable: Boolean,
                         verbose: Boolean
                        ) = {
    println(s"WRITING RUNS TO ${index.pathAsString}")
    index.createFileIfNotExists().toJava.asCsvWriter[AnnotatedRun](config.withHeader).write(runs).close()
    species_indexes match {
      case Some(species_indexes_path) if species_indexes_path.isDirectory =>
        val species_ind: File = species_indexes_path.createDirectoryIfNotExists()
        val by_species = runs.groupBy{a=>
          val str: String = if(File(a.quantAnnotation.index).exists)
            a.runAnnotation.organism
            else  a.runAnnotation.organism + "_" +File(a.quantAnnotation.index).name+  "_salmon_"+ a.quantAnnotation.salmon_version
          str
        }
        for ((species_name, rs) <- by_species) {
          val sp =  species_name.replace(" ", "_")
          Try {
              val p = (species_ind / (sp + ".tsv")).createFileIfNotExists()
              p.toJava.asCsvWriter[AnnotatedRun](config.withHeader).write(rs).close()
              println(s"created per-species file for ${sp} at" + p.pathAsString)
            } match {
              case Failure(th) =>
                println(s"SPECIES FAILURE for ${species_name}:")
                println(th)
              case _ =>
            }
          if(mergeTPMs){
            mergeTranscriptFiles(runs, stable, verbose, species_ind, sp)
            mergeGenesFiles(runs, stable, verbose, species_ind, sp)
          }
        }

      case None =>
        println("As species index")
    }
  }
  protected def mergeTranscriptFiles(runs: Seq[AnnotatedRun], stable: Boolean, verbose: Boolean, species_ind: File, sp: String): Unit = {
    val transcriptFiles = runs.map(r => r.runAnnotation.run -> File(r.quantAnnotation.transcripts))
      .filter { case (_, f) =>
        val isFolder = f.isDirectory
        if (isFolder) println(s"${f} is a directory but should be Folder")
        f.exists && !isFolder
      }
    val groupedTranscripts = transcriptFiles.groupBy(_._2.lines.size)
    for {
      (i, values) <- groupedTranscripts
    } {
      val trs = if (groupedTranscripts.size == 1) species_ind / (sp + "_transcripts.tsv") else species_ind / (sp + s"_transcripts_${i}.tsv")
      println(s"writing merged transcripts to ${trs.pathAsString}")
      mergeExpressions(trs, values, "transcripts", stable, verbose)
    }
  }

  protected def mergeGenesFiles(runs: Seq[AnnotatedRun], stable: Boolean, verbose: Boolean, species_ind: File, sp: String): Unit = {
    val geneFiles = runs.map(r => r.runAnnotation.run -> File(r.quantAnnotation.genes))
      .filter { case (_, f) =>
        val isFolder = f.isDirectory
        if (isFolder) println(s"${f} is a directory but should be Folder")
        f.exists && !isFolder
      }
    val groupedGenes = geneFiles.groupBy(_._2.lines.size)
    for {
      (i, values) <- groupedGenes
    } {
      val gs = if (groupedGenes.size == 1) species_ind / (sp + "_genes.tsv") else species_ind / (sp + s"_genes_${i}.tsv")
      println(s"writing merged genes to ${gs.pathAsString}")
      mergeExpressions(gs, values, "genes", stable, verbose)
    }
  }

  def samples_table(base: Path,
                    index: String,
                    species_indexes: Option[String],
                    key: String,
                    rewrite: Boolean,
                    stable: Boolean,
                    verbose: Boolean,
                    ignore: Seq[Path] = Vector.empty
          ) = {
    implicit val f = FetchGEO(key)
    val rt = base.toFile.toScala
    val indexFile: File = index match {
      case i if i.startsWith("/") => File(i)
      case relative => rt / relative
    }
    val speciesFile = species_indexes.map{
      case str if str.startsWith("/") => File(str)
      case str => rt / str
    }.map(_.createDirectoryIfNotExists())
    val ignoreFolders = ignore.map(_.toFile.toScala).toSet
    val runs: Vector[AnnotatedRun] = rt.children.filter(_.isDirectory).toVector.sortBy(_.name).flatMap {
      series =>
        println("============")
        println(s"SERIES = " + series.name)
        //(series.name, series.children.filter(_.isDirectory))
        val samples = series.children.filter(s => s.isDirectory && s.nonEmpty && !ignoreFolders.contains(s)).toVector.sortBy(_.name)
        samples.flatMap {
          case experiment if experiment.isDirectory && experiment.children.exists(f => f.isDirectory &&
            f.children.exists(child => child.name.contains("_transcripts_abundance.tsv"))
          ) =>
            processExperiment(series, experiment, rewrite)

          case experiment =>
            println(s"Experiment ${experiment.name} does not seem to have SRR-s inside!")
            Nil
        }
    }


    writeAnnotatedRuns(indexFile, speciesFile, runs, mergeTPMs = speciesFile.isDefined, stable, verbose)
    println("INDEX SUCCESSFULLY CREATED at " + indexFile.pathAsString)
  }

  protected lazy val samples_index: Command[Unit] = Command(
    name = "samples_index",
    header = "Summarizes gene expressions"){
    (base, index, species_indexes, key, rewrite, stable, verbose, ignore).mapN(samples_table)
  }


}
