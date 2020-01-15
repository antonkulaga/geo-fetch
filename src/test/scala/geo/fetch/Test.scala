package geo.fetch

import geo.cli.CommandsBioProject

object Test extends scala.App {

  val key = "0a1d74f32382b8a154acacc3a024bdce3709"
  val f = FetchGEO(key)
  val run_id = "SRR1047652" //"SRR5291531"
  val run = f.getSRA(run_id).head
  println("RUN = " +run)
  println("==================")
  println("experiment = " + run.run.Experiment)

  val e = CommandsBioProject.fetchExperiment(run.run.Experiment, key, "", "", true)
  //pprint.pprintln(e)
  println("GOING FURTHER!")
  println("GOING FURTHER!")

  //val study = e.study

}