package geo.extras

import better.files.File
import geo.fetch.{AnnotatedRun, FetchGEO}

import scala.util.{Failure, Try}


case class SampleSummarizer(geo: FetchGEO) extends SampleSummarizerLike
/**
  * Extra function to summarize already proccessed samples
  */
trait SampleSummarizerLike {

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


  implicit val config: CsvConfiguration = rfc.withCellSeparator('\t').withHeader(true)
  def getPathIf(dir: File)(fun: File => Boolean) = dir.children.collectFirst{ case f if fun(f) => f.pathAsString }.getOrElse("")

  def mergeExpressions(where: File, expressions: Seq[(String, File)], column: String) = if(expressions.nonEmpty){
    val toc = expressions.head._2.lineIterator.map(l=>l.substring(0, l.indexOf("\t"))).zipWithIndex.toVector
    val ee = expressions.map{ case (run, e)=>
      run -> e.lineIterator.map(l=>l.substring(l.indexOf("\t")+1)).toVector
    }
    val (taken, discarded) = ee.partition{ case (_, v)  => v.length == toc.length}
    for((f, d)<-discarded) {
      println(s"length of ${f} suggests that something is wrong as it is ${d.length} while ${toc.length} is expected!")
    }
    where.createFileIfNotExists()
    where.append(column + "\t")
    for((run, _) <-taken) where.append(run + "\t")
    where.append("\n")
    val exps = taken.map(_._2)
    for((t, i)<-toc){
      where.append(t + "\t")
      for(e <-exps) where.append(e(i) + "\t")
      where.append("\n")
    }
  }

  /**
    * Processes GSM or SRX
    * @param seriesFolder GSE or PRJM
    * @param experimentFolder GSM or SRX name
    * @param rewrite
    * @return
    */
  def processExperiment(seriesFolder: File, experimentFolder: File, rewrite: Boolean)(implicit f: FetchGEO): Vector[AnnotatedRun] = {
    println("Experiment = " + experimentFolder.name)
    if (rewrite || !(experimentFolder.children.exists(_.name == experimentFolder.name + ".json") && experimentFolder.children.exists(_.name == experimentFolder.name + "_runs.tsv"))) {
      println("cannot find " + experimentFolder.name + ".json, creating it from scratch!")
      Try {
        val uname = experimentFolder.name.toUpperCase
        if (uname.startsWith("PRJN") || uname.startsWith("SRX") || uname.startsWith("ERX"))
          geo.cli.MainCommand.fetchBioProject(experimentFolder.name, f.apiKey, experimentFolder.pathAsString + "/" + experimentFolder.name + ".json", experimentFolder.pathAsString + "/" + experimentFolder.name + "_runs.tsv", true)
        else
          geo.cli.MainCommand.fetchGSM(experimentFolder.name, f.apiKey, experimentFolder.pathAsString + "/" + experimentFolder.name + ".json", experimentFolder.pathAsString + "/" + experimentFolder.name + "_runs.tsv", true)

      } match {
        case Failure(th) => println("could not create Experiment because of: " + th.toString)
        case _ => println(experimentFolder.name + ".json" + " successfully created!")
      }
    }
    //val sample = extractGSM(series, experiment)

    experimentFolder.children.collect {
      case run if run.isDirectory && run.children.exists(_.name.contains("_transcripts_abundance.tsv")) =>
        processRun(seriesFolder, experimentFolder, run)(f)
    }.toVector
  }


  protected def processRun(seriesFolder: File, experimentFolder: File, runFolder: File)(implicit f: FetchGEO): AnnotatedRun = {
    println(seriesFolder.name + "/" + experimentFolder.name + "/" + runFolder.name)

    val runMeta = runFolder / (runFolder.name + "_run.tsv")
    if (runMeta.exists && runMeta.nonEmpty) {
      println(s"FOUND metadata file for ${runFolder.name}")
      val metas =runMeta.toJava.asCsvReader[AnnotatedRun](config).toList
      metas.headOption match {
        case Some(Left(err)) =>
          println(s"could not read ${runMeta.pathAsString} because of ${err}")
          runMeta.delete().createIfNotExists()
          prepareRunInfo(runMeta, seriesFolder.name, runFolder)(f)
        case Some(Right(value)) =>
          value.copy(
            genes = getPathIf(runFolder)(_.name.contains("genes_abundance.tsv")),
            transcripts = getPathIf(runFolder)(_.name.contains("transcripts_abundance.tsv")),
            quant = getPathIf(runFolder)(_.name.contains("quant_"))
          )
        case None =>
          println(s"the file ${runMeta.pathAsString} is empty, writing it from NCBI API!")
          prepareRunInfo(runMeta, seriesFolder.name, runFolder)(f)

      }
    } else prepareRunInfo(runMeta, seriesFolder.name, runFolder)(f)
  }

  /**
    * gets annotated run from NCBI, writes it to the file and returns as result
    * @param runFolder
    * @param f implicit FetchGEO
    * @return
    */
  def prepareRunInfo(runMeta: File, series: String, runFolder: File)(implicit f: FetchGEO): AnnotatedRun = {
    val runInfo = f.getAnnotatedRun(runFolder.name, series,
      getPathIf(runFolder)(_.name.contains("genes_abundance.tsv")),
      getPathIf(runFolder)(_.name.contains("transcripts_abundance.tsv")),
      getPathIf(runFolder)(_.name.contains("quant_")),
    )

    //println(s"*******************************RUN INFO FOR ${}")
    //pprint.pprintln(runInfo)
    println(s"did not found metadata file for ${runFolder.name}, getting info from NCBI and writing to ${runMeta.pathAsString}")
    runMeta.createIfNotExists().toJava.asCsvWriter[AnnotatedRun](config.withHeader).write(runInfo).close()
    runInfo
  }

}