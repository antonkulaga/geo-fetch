package geo.fetch

import scala.collection.immutable.ListMap

object SoftParser {
  import fastparse._, NoWhitespace._

  def digit[_: P]: P[Unit] = CharIn("0-9")
  def letter[_: P]: P[Unit] = CharIn("A-Za-z")
  def name[_: P]: P[Unit] =  CharsWhile(c => !(c==' ' || c == '=')) //digit | letter | "_" | "-"
  def equals[_:P]: P[Unit] =  " ".rep ~ "=" ~ " ".rep

  def entity[_: P]: P[(String, String)] = "^" ~ name.rep.! ~ equals ~ CharsWhile(_!='\n').rep.! ~ "\n"

  def property[_: P]: P[(String, String)] =  "!" ~ name.rep.! ~ equals ~ CharsWhile(_!='\n').rep.! ~ "\n"

  def properties[_: P]: P[Map[String, Seq[String]]] = property.rep.map(ps=>ps.groupBy(_._1).mapValues(_.map(_._2)))

  def softMap[_ :P]: P[Seq[(String, String, Map[String, Seq[String]])]] = (entity ~ properties).rep


  def parseSOFT(text: String): Parsed[Seq[(String, String, Map[String, Seq[String]])]] = {
    parse(text, softMap(_))
  }
}
