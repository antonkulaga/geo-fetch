package geo.cli

import java.nio.file.{Path, Paths}

import wvlet.log.LogSupport
import java.nio.file.Path

import better.files._
import cats.implicits._
import com.monovore.decline._
import geo.fetch.FetchGEO
import geo.models.{GSM, RunInfo}
import wvlet.log.LogSupport
import pprint.PPrinter.BlackWhite

object Test //extends scala.App
{
  import geo.fetch._
  import io.circe.Xml._

  import geo.fetch._
  //val gsm_id = "GSM1698570"
  val gsm_id = "GSM1622693" //Bs-Seq

  val f = FetchGEO("0a1d74f32382b8a154acacc3a024bdce3709")
  val g: GSM = f.getGSM(gsm_id, true)
  println("RUNS")
  println(f.get_query_text(gsm_id, "self").replace("\r\n", "\n"))

  val s = f.fetch_bioproject_xml(g.bioSample.get).unsafeRunSync()
  println("SAMPLE")
  println(s)
  println("=======")
  println(
  f.fetch_bioproject_json(g.bioSample.get).unsafeRunSync()
  )/*
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

    /*
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
  */


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