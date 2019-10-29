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
val run = f.getSRA("SRR5291531").head
val e = f.getExperiment(run.run.Experiment)
val study = e.study