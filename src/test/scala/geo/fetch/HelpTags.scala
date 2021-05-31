package geo.fetch

import geo.models.BioProject.ExperimentSet

object HelpTags extends scala.App {

  val key = "0a1d74f32382b8a154acacc3a024bdce3709"
  val f = FetchGEO(key)
  val bioproject = f.getBioProject("PRJNA373885")
  val experiments = bioproject.experiments.map(exp=>exp.experiment)
  val samples = bioproject.experiments.map(exp=>exp.sample)
  //samples.partition(s=>s.sample_attributes.attributes.collectFirst(_._1=="bmi at term") > 30)
  def bmi(sample: ExperimentSet.Sample) = sample.sample_attributes.attributes("bmi at term").toDouble


  val (obese, normal) =  bioproject.experiments.partition(exp=> bmi(exp.sample) > 30)
  val obese_runs = obese.map(exp=>exp.run_set.run.identifiers.primary_id)
  println(obese_runs.map("\"" + _ + "\"").mkString("[",",", "]"))
  println("====================")
  val normal_runs = normal.map(exp=>exp.run_set.run.identifiers.primary_id)
  println(normal_runs.map("\"" + _ + "\"").mkString("[",",", "]"))
}
