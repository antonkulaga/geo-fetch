package geo.fetch


import scala.util._
import better.files._
import File._
import java.io.{File => JFile}

import org.scalatest._
import cats.implicits._

trait FetchGeoSuite {
  self: WordSpec with Matchers =>
  "FetchGEO" should {
    "Soft" in {
      val f = FetchGEO("0a1d74f32382b8a154acacc3a024bdce3709")
      f.get_gsm_text("")
      //f.get_gse_xml()
    }
  }
}
