package geo.fetch

import geo.models.BioProject
trait MedipTwins_PRJNA236349 {
  val key = "0a1d74f32382b8a154acacc3a024bdce3709"
  val f = FetchGEO(key)
  val bioproject = f.getBioProject( 	"PRJNA236349") //GSE54370
  val (control, treatment) = bioproject.experiments.partition(e=>e.experiment.title.toLowerCase.contains("control"))
  println(s"${control.length} controls and ${treatment.length} treatments")
  println(control.map(e=>"\"" + e.run_set.run.identifiers.primary_id + "\"").mkString("[",",", "]"))
  println("================")
  println(treatment.map(e=>"\"" + e.run_set.run.identifiers.primary_id + "\"").mkString("[",",", "]"))
}

trait MedipTwins50 {
  val key = "0a1d74f32382b8a154acacc3a024bdce3709"
  val f = FetchGEO(key)
  val bioproject = f.getBioProject( 	"PRJNA235960") //GSE54222
  //val bioproject = f.getBioProject( 	"PRJNA236349") //GSE54370
  //pprint.pprintln(bioproject.experiments)
  val (control, treatment) = bioproject.experiments.partition(e=>e.sample.sample_attributes.attributes("subject status") == "Control")
  println(s"${control.length} controls and ${treatment.length} treatments")
  println(control.map(e=>"\"" + e.run_set.run.identifiers.primary_id + "\"").mkString("[",",", "]"))
  println("================")
  println(treatment.map(e=>"\"" + e.run_set.run.identifiers.primary_id + "\"").mkString("[",",", "]"))

}

object MedipTwin extends scala.App with MedipTwins50 {

}
