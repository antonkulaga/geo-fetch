package geo.extras

import better.files.File
import geo.fetch.{FetchGEO}

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
  def getPathIf(dir: File)(fun: File => Boolean): String = dir.children.collectFirst{ case f if fun(f) => f.pathAsString }.getOrElse("")

  def parseSalmon(file: File): Try[SalmonInfo] = {
    parse(file.contentAsString).flatMap(json=>json.as[SalmonInfo]) match {
      case Left(error) =>
        println(s"FAILURE IN PARSON SALMON JSON with ${error.getMessage}")
        Failure(error)
      case Right(info) =>
        Success(if(info.index.startsWith("/cromwell-executions/")) info.copy(index = File(info.index).name) else info)
    }
  }

  def undot(transcript: String): String =       transcript.indexOf(".")
  match {
    case -1 => transcript
    case i if i <= transcript.length -2 => transcript.substring(0, i) + transcript.substring(Math.max(i+2, transcript.indexOf(" ", i)))
    case _ => transcript
  }

  /**
    * Merges gene expressions in files
    * @param outputFile the output file
    * @param expressions
    * @param column
    */
  def mergeExpressions(outputFile: File, expressions: Seq[(String, File)], column: String, stable: Boolean, verbose: Boolean) = if(expressions.nonEmpty){
    val ee = expressions.map{ case (run, e)=>
      run -> e.lineIterator.map(l=>l.substring(l.indexOf("\t")+1)).toVector
    }
    val toc = expressions.head
      ._2
      .lineIterator
      .map { l =>
        val t = l.substring(0, l.indexOf("\t"))
        if (stable) undot(t) else t
      }
      .zipWithIndex.toVector

    val (taken, discarded) = ee.partition{ case (_, v)  => v.length == toc.length}
    for((f, d)<-discarded) {
      println(s"length of ${f} suggests that something is wrong as it is ${d.length} while ${toc.length} is expected!")
    }
    outputFile.createFileIfNotExists()
    outputFile.append(column + "\t")
    for((run, _) <-taken) outputFile.append(run + "\t")
    outputFile.append("\n")
    val exps = taken.map(_._2)
    for((t, i)<-toc){
      outputFile.append(t + "\t")
      for(e <-exps) outputFile.append(e(i) + "\t")
      outputFile.append("\n")
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
    if (rewrite || !(experimentFolder.children.exists(_.name == experimentFolder.name + ".json")
      && experimentFolder.children.exists(_.name == experimentFolder.name + "_runs.tsv")))
    {
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
          println(s"could not read ${runMeta.pathAsString} because of ${err} , rewriting runinfo from scratch")
          runMeta.delete().createIfNotExists()
          prepareRunInfo(runMeta, seriesFolder.name, runFolder)(f)
        case Some(Right(value)) =>
          /*
          value.copy(
            genes = getPathIf(runFolder)(_.name.contains("genes_abundance.tsv")),
            transcripts = getPathIf(runFolder)(_.name.contains("transcripts_abundance.tsv")),
            quant = getPathIf(runFolder)(_.name.contains("quant_"))
          )
           */

          val q = value.quantAnnotation
          val quant_folder = runFolder / ("quant_" + seriesFolder.name + "_" + experimentFolder.name +  "_" + runFolder.name)

          quant_folder match {
            case folder if folder.isDirectory && folder.exists && folder.nonEmpty && (folder / "cmd_info.json").exists()=>
              val cmd = folder / "cmd_info.json"
              parseSalmon(cmd).map(s =>
                 q.copy(
                  genes = getPathIf(runFolder)(_.name.contains("genes_abundance.tsv")),
                  transcripts = getPathIf(runFolder)(_.name.contains("transcripts_abundance.tsv")),
                  quant = getPathIf(runFolder)(_.name.contains("quant_"))
                ).withSalmonInfo(s)
              ) match {
                case Success(v) => value.copy(quantAnnotation = v)
                case Failure(th) => println(s"Failure to parse salmon! ${th}")
                  value
              }
            case f =>
              println(s"NO SALMON QUANTIFICATION FOUND FOR ${runFolder}, the quantification is supposed to be inside ${f}!")
              value
          }

        case None =>
          println(s"the file ${runMeta.pathAsString} is empty or broken, writing it from NCBI API!")
          //runMeta.clear()
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

    val runInfo = f.getRunAnnotation(runFolder.name, series)
    val quant_folder = runFolder / ("quant_" + series + "_" + runFolder.parent.name +  "_" + runFolder.name) //TODO FIX CODE DUPLICATION
    val run = quant_folder match {
      case folder if folder.isDirectory && folder.exists && folder.nonEmpty && (folder / "cmd_info.json").exists()=>
        val cmd = folder / "cmd_info.json"
        parseSalmon(cmd).map{ case s =>
          val quant = getPathIf(runFolder)(_.name.contains("quant_"))
          val q = File(quant)//.attributes.lastModifiedTime
          val modified = if(q.exists) { q.attributes.lastModifiedTime().toInstant.toString } else { "" }
          QuantAnnotation(
            s.salmon_version,
            s.index,
            getPathIf(runFolder)(_.name.contains("genes_abundance.tsv")),
            getPathIf(runFolder)(_.name.contains("transcripts_abundance.tsv")),
            quant,
            s.libType,
            s.numBootstraps,
            modified
          )
        } match {
          case Success(v) =>
            AnnotatedRun(runInfo, v)
          case Failure(th) => println(s"Failure to parse salmon quantification! ${th}")
            AnnotatedRun(runInfo, QuantAnnotation.empty)
        }
      case f =>
        println(s"NO SALMON QUANTIFICATION FOUND FOR ${runFolder}!")
        AnnotatedRun(runInfo, QuantAnnotation.empty)
    }

    //println(s"*******************************RUN INFO FOR ${}")
    //pprint.pprintln(runInfo)
    println(s"did not found metadata file for ${runFolder.name}, getting info from NCBI and writing to ${runMeta.pathAsString}")
    runMeta.createIfNotExists().toJava.asCsvWriter[AnnotatedRun](config.withHeader).write(run).close()
    run
  }

}