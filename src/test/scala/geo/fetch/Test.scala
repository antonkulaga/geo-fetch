package geo.fetch

import geo.cli.CommandsBioProject
import zio.URIO

object Test extends scala.App {

  val key = "0a1d74f32382b8a154acacc3a024bdce3709"
  val f = FetchGEO(key)

/*
  val run_id = "SRR1047652" //"SRR5291531"
  val run = f.getSRA(run_id).head
  println("RUN = " +run)
  println("==================")
  println("experiment = " + run.run.Experiment)

  val e = CommandsBioProject.fetchExperiment(run.run.Experiment, key, "", "", true)
  pprint.pprintln(e)
  println("GOING FURTHER!")

  //val study = e.study

 */

  //val e = CommandsBioProject.fetchExperiment("DRX081510", key, "", "", true)

  val gsm = "GSM2740707"//"GSM2740712"
  //gsm --key 0a1d74f32382b8a154acacc3a024bdce3709 -e --output GSM2740712.json --runs GSM2740712_runs.tsv  GSM2740712
  //CommandsBioProject.fetchGSM(gsm, "0a1d74f32382b8a154acacc3a024bdce3709", "GSM2740712.json", "GSM2740712_runs.tsv", false)
  //val r = f.getGSM(gsm, true)
  val query = f.get_query_soft(id = gsm)
  val res = f.runtime.unsafeRunTask(query)
  println(res)


}