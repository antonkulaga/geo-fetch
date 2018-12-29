
import geo.fetch._
val gsm_id = "GSM1698570"

val f = FetchGEO("0a1d74f32382b8a154acacc3a024bdce3709")
/*
f.get_gsm_json("GSM1698568").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
f.get_gsm_json("GSM1698568").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
f.get_gsm_json("GSM1698570").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
f.get_gsm_json("GSM2927683").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
f.get_gsm_json("GSM2927750").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
f.get_gsm_json("GSM2042593").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
f.get_gsm_json("GSM2042596").unsafeRunSync().as[MINiML.Container].map(_.content.Sample.Channel.Characteristics)
 */
val gsm = f.getGSM(gsm_id)
val srx = gsm.relations.srx.get
f.fetch_sra_json(srx).unsafeRunSync()