package geo.fetch

import geo.cli.CommandsBioProject
import zio.URIO

object TestBioProject extends scala.App {

  val key = "0a1d74f32382b8a154acacc3a024bdce3709"
  val f = FetchGEO(key)

  val project = f.getBioProject("PRJNA561156")
  val arr = project.experiments.map(e=>e.run_set.run.accession).map(v=> "\""+ v + "\"").mkString("[", ",", "]")
  println(arr)
}