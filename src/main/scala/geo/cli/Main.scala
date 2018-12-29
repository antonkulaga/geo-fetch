package geo.cli

import java.nio.file.{Path, Paths}

import wvlet.log.LogSupport
import java.nio.file.Path

import better.files._
import cats.implicits._
import com.monovore.decline._
import geo.fetch.FetchGEO
import geo.models.RunInfo
import wvlet.log.LogSupport
import pprint.PPrinter.BlackWhite

object Main extends scala.App {
  import geo.fetch._
  import io.circe.Xml._

  import geo.fetch._
  val gsm_id = "GSM1698570"

  val f = FetchGEO("0a1d74f32382b8a154acacc3a024bdce3709")
  /*
  f.get_gsm_json("GSM1698568").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
  f.get_gsm_json("GSM1698568").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
  f.get_gsm_json("GSM1698570").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
  f.get_gsm_json("GSM2927683").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
  f.get_gsm_json("GSM2927750").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
  f.get_gsm_json("GSM2042593").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
  f.get_gsm_json("GSM2042596").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
   */

  import io.circe.Json
  import io.circe.generic.JsonCodec
  import io.circe.syntax._
  import io.circe.generic.extras._

  val gsms = Seq(
  "GSM1698568",
  "GSM1698570",
   "GSM2927683",
   "GSM2927750",
    "GSM2042593",
    "GSM2042596"
  )
  for(g <- gsms){
    val gsm = f.getGSM(g)//.unsafeRunSync()
    println(s"---------------------------$g--------------------------")
    //BlackWhite.pprintln(gsm, 1000, 1000)
    gsm.relations.sra match {
      case Some(v) if v.contains("SRA") =>
        //BlackWhite.pprintln(f.fetch_sra_runinfo("SRR2014240"), 1000, 1000)
        //f.fetch_sra_xml(v)

        BlackWhite.pprintln(f.fetch_sra_runinfo(v), 1000, 1000)

      case Some(v) if v.contains("SRX") =>
      println(s"||||||||||||||||${v}|||||||||||||||||||||||||||||||||||||")
      //val x = f.fetch_sra_xml(v).unsafeRunSync()
        val info = f.fetch_sra_runinfo(v)
        import kantan.csv._
        import kantan.csv.ops._
                BlackWhite.pprintln(info, 1000, 1000)
        println(s"+++++_${info.count(_ =='\n')}_++++++")

        for(r <- RunInfo.fromCSV(info)) {
          BlackWhite.pprintln(r, 1000, 1000)
          import io.circe.generic.auto._
          val js = r.asJson
          BlackWhite.pprintln(js, 1000, 1000)
        }
    }
  }


  /*
  f.get_gsm_json("GSM1698568").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
  f.get_gsm_json("GSM1698568").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
  f.get_gsm_json("GSM1698570").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
  f.get_gsm_json("GSM2927683").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
  f.get_gsm_json("GSM2927750").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
  f.get_gsm_json("GSM2042593").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
  f.get_gsm_json("GSM2042596").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
   */

  /*
  BlackWhite.pprintln(f.get_gsm_json("GSM1698568").unsafeRunSync().as[MINiML.Container])
  BlackWhite.pprintln(f.get_gsm_json("GSM1698568").unsafeRunSync().as[MINiML.Container])
  BlackWhite.pprintln(f.get_gsm_json("GSM1698570").unsafeRunSync().as[MINiML.Container])
  BlackWhite.pprintln(f.get_gsm_json("GSM2927683").unsafeRunSync().as[MINiML.Container])
  BlackWhite.pprintln(f.get_gsm_json("GSM2927750").unsafeRunSync().as[MINiML.Container])
  BlackWhite.pprintln(f.get_gsm_json("GSM2042593").unsafeRunSync().as[MINiML.Container])
  BlackWhite.pprintln(f.get_gsm_json("GSM2042596").unsafeRunSync().as[MINiML.Container])
  */
}
/*
object Main extends CommandApp(
  name = "GEO-Fetch",
  header = "GEO fetch application",
  main = {
    MainCommand.mainCommand.map{ _=>
      //just to run it
    }
  }

)
*/

object MainCommand {


  protected lazy val output = Opts.option[String](long = "output", short = "o", help = "File to save the result, prints to stdout if not set").withDefault("")

  protected lazy val key = Opts.option[String](long = "key", short = "k", help = "NCBI API KEY").withDefault("")
  protected lazy val format = Opts.option[String](long = "format", short = "f", help = "Either xml or json").withDefault("json")

  protected lazy val sra = Opts.argument[String]("sra")

  def printOrSave(result: String, o: String) = {
    if(o=="") println(result) else{
      println(s"saving results to ${o}")
      File(o).write(result)
    }
  }


  def getSRA(sra: String, key: String, format: String, o: String): Unit = {
    val result = (if(format == "json") FetchGEO(key).fetch_sra_json(sra) else FetchGEO(key).fetch_sra_xml(sra)).unsafeRunSync().toString
    printOrSave(result, o)
  }

  protected lazy val fetch_sra: Command[Unit] = Command(
    name = "sra",
    header = "Fetch SRA"
  ) {
    (sra, key, format, output).mapN{getSRA}
  }



  protected lazy val gsm = Opts.argument[String]("gsm")

  def getGSM(sra: String, key: String, format: String, o: String): Unit = {
    val result = (if(format == "json") FetchGEO(key).get_gsm_json(sra) else FetchGEO(key).get_gsm_xml(sra)).unsafeRunSync().toString
    printOrSave(result, o)
  }

  protected lazy val fetch_gsm: Command[Unit] = Command(
    name = "gsm",
    header = "Fetch GSM"
  ) {
    (gsm, key, format, output).mapN{getGSM}
  }

  def getGSE(sra: String, key: String, format: String, o: String): Unit = {
    val result = (if(format == "json") FetchGEO(key).get_gse_json(sra) else FetchGEO(key).get_gse_xml(sra)).unsafeRunSync().toString
    printOrSave(result, o)
  }

  protected lazy val gse = Opts.argument[String]("gse")


  protected lazy val fetch_gse: Command[Unit] = Command(
    name = "gse",
    header = "Fetch GSE"
  ) {
    (gse, key, format, output).mapN{getGSE}
  }

  lazy val mainCommand: Opts[Unit] = Opts.subcommand(fetch_sra) orElse Opts.subcommand(fetch_gsm) orElse Opts.subcommand(fetch_gse)
}

/*
object MainCommands extends SraCommands

trait SraCommands extends LogSupport{


  protected lazy val path: Opts[Path] = Opts.argument[Path]("file or folder to read from")
  protected lazy val delimiter: Opts[String] = Opts.option[String](long = "delimiter", short = "d", help = "delimiter to be used when parsing PWMs").withDefault("auto")
  protected lazy val verbose: Opts[Boolean] = Opts.flag("verbose", short="v", help="show values of the found PWMs").map(_=>true).withDefault(false)

  lazy val mainCommand =Command(
    name = "list",
    header = "Lists known files"
  )
}
*/
