package geo.fetch

import better.files.File
import geo.cli.CommandsBioProject
import geo.models.RunInfo
import kantan.csv._
/*
object Test extends scala.App {

  import ammonite.ops._
  import ammonite.ops.ImplicitWd._
  import better.files._
  import kantan.csv._
  import kantan.csv.ops._
  import kantan.csv.generic._
  import io.circe.optics.JsonPath.root
  import io.circe._
  import io.circe.parser._
  import geo.fetch._

  import scala.util._
  import better.files.File

  //def getPathIf(dir: File)(fun: File => Boolean) = dir.children.collectFirst{ case f if fun(f) => f.pathAsString }.getOrElse("")

  implicit val config: CsvConfiguration = rfc.withCellSeparator('\t').withHeader(true)
  val root: Path = Path("/data/samples/clocks/")
  val index: Path = Path("/data/samples/merge.tsv")
  val fl = index.toIO.toScala
  val rt = root.toIO.toScala
  

  val ignore: Seq[Path] = Vector.empty
  val ignoreFolders = ignore.map(_.toIO.toScala).toSet


  for(series <- rt.children.filter(_.isDirectory)) {
      println("============")
      println(s"SERIES = " +series.name)
      val samples = series.children.filter(s => s.isDirectory && s.nonEmpty && !ignoreFolders.contains(s)).toList
      val genes =   for{s <- samples if s.name.contains("genes_abundance")} yield s
      val transcripts =   for{s <- samples if s.name.contains("transcripts_abundance")} yield s

  }
  /*

  def merge_samples(index: Path = Path("/data/samples/merge.tsv"),
           root: Path = Path("/data/samples/clocks/"),
           rewrite: Boolean = false,
          ) = {
    implicit val f = FetchGEO(key)
    val fl = index.toIO.toScala
    val rt = root.toIO.toScala
    val runs: List[AnnotatedRun] = rt.children.filter(_.isDirectory).flatMap {
      series =>
        println("============")
        println(s"SERIES = " +series.name)
        //(series.name, series.children.filter(_.isDirectory))
        val samples = series.children.filter(s => s.isDirectory && s.nonEmpty && !ignoreFolders.contains(s)).toList
        samples.flatMap {
          case experiment if experiment.isDirectory && experiment.children.exists(f=> f.isDirectory &&
            f.children.exists(child=>child.name.contains("_transcripts_abundance.tsv"))
          ) =>
            println("Experiment = " + experiment.name)
            if(rewrite || !(experiment.children.exists(_.name == experiment.name + ".json") && experiment.children.exists(_.name == experiment.name + "_runs.tsv"))  )
            {
              println("cannot find " + experiment.name + ".json, creating it from scratch!")
              Try {
                val uname = experiment.name.toUpperCase
                if(uname.startsWith("PRJN") || uname.startsWith("SRX") || uname.startsWith("ERX"))
                  geo.cli.MainCommand.fetchBioProject(experiment.name, key, experiment.pathAsString + "/" + experiment.name + ".json", experiment.pathAsString + "/" + experiment.name + "_runs.tsv", true)
                else geo.cli.MainCommand.fetchGSM(experiment.name, key, experiment.pathAsString + "/" + experiment.name + ".json", experiment.pathAsString + "/" + experiment.name + "_runs.tsv", true)

              } match {
                case Failure(th) => println("could not create Experiment because of: " + th.toString)
                case _ => println(experiment.name + ".json" + " successfully created!")
              }
            }
            //val sample = extractGSM(series, experiment)

            experiment.children.collect {
              case run if run.isDirectory && run.children.exists(_.name.contains("_transcripts_abundance.tsv")) =>
                println(series.name + "/" + experiment.name + "/" + run.name)

                val runMeta = run / (run.name + "_run.tsv")
                if(runMeta.exists && runMeta.nonEmpty) {
                  println(s"FOUND metadata file for ${run.name}")
                  runMeta.toJava.asCsvReader[AnnotatedRun](rfc).toList.headOption match {
                    case Some(Left(err)) =>
                      println(s"could not read ${runMeta.pathAsString} because of ${err}")
                      runMeta.delete().createIfNotExists()
                      writeRunInfo(runMeta, run, series)(f)
                    case Some(Right(value)) =>
                      value.copy(
                        genes = getPathIf(run)(_.name.contains("genes_abundance.tsv")),
                        transcripts = getPathIf(run)(_.name.contains("transcripts_abundance.tsv")),
                        quant = getPathIf(run)(_.name.contains("quant_"))
                      )
                    case None =>
                      println(s"the file ${runMeta.pathAsString} is empty, writing it from NCBI API!")
                      writeRunInfo(runMeta, run, series)(f)

                  }
                } else writeRunInfo(runMeta, run, series)(f)
            }
          case experiment =>
            println(s"Experiment ${experiment.name} does not seem to have SRR-s inside!")
            Nil
        }
    }.toList
    writeAnnotatedRuns(index, species_indexes, runs)
    println("INDEX SUCCESSFULLY CREATED at " + index.toIO.toScala.pathAsString)
  }

 */
}
*/