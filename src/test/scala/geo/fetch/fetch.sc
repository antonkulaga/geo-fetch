
/*
import geo.fetch._
import io.circe.Xml._
val gsm_id = "GSM1698570"
println("---")

val f = FetchGEO("0a1d74f32382b8a154acacc3a024bdce3709")
val gsm  = f.get_gsm_json(gsm_id).unsafeRunSync()
println("============================")

/*
f.get_gsm_json("GSM1698568").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
f.get_gsm_json("GSM1698568").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
f.get_gsm_json("GSM1698570").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
f.get_gsm_json("GSM2927683").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
f.get_gsm_json("GSM2927750").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
f.get_gsm_json("GSM2042593").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
f.get_gsm_json("GSM2042596").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
 */
val t = f.get_gsm_text("GSM1698568").unsafeRunSync()
t.count(_=='\n')
*/