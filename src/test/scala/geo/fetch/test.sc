//import $ivy.`"com.nrinaudo" %% "kantan.csv-generic" % kantanVersion,`
import geo.cli.CommandSra
import geo.fetch._
import geo.models.{EssentialInfo, RunInfo}
import kantan.csv.rfc
import pprint.PPrinter.BlackWhite
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
import io.circe._
import io.circe.generic._
import io.circe.syntax._
import io.circe.optics.JsonPath._
import geo.models.BioProject._
import geo.models.BioProject

val key = "0a1d74f32382b8a154acacc3a024bdce3709"
val f = FetchGEO(key)
//val sra = "ERR2075073" //"SRR3200449"
//val g = f.getGSM("PRJNA223213")
//println(g)
val id = "PRJNA223213"
val p = f.getBioProject(id)
/*
val s = {
  val json = f.fetch_bioproject_json(id).unsafeRunSync()
  exp.getOption(json).map(v=> v.map(_.as[BioProject.ExperimentPackage])).get
}
*/
println("===============")
println(p.experiments.head.study)

//val r = f.getSRA(sra).head

//BlackWhite.pprintln(r.toFlatJSON.spaces2)
//CommandSra.fetchSRA(sra, key, "flatjson")
/*
val run_str = f.fetch_sra_runinfo(sra).replace("\r\n", "\n")
val lst: List[List[String]] = run_str.asCsvReader[List[String]](rfc.withHeader.withCellSeparator(',')).map{
  case Right(r)=> r
  case Left(e) => throw new Exception(s"Cannot parse runinfo with error ${e}!")
}.toList
BlackWhite.pprintln(lst)
println("===")
val j = lst.head.zip(lst.tail.head).toMap.asJson
println(j)
*/
/*
 */