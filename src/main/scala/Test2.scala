object Test2 extends scala.App {
  //import $ivy.`"com.nrinaudo" %% "kantan.csv-generic" % kantanVersion,`
  import geo.fetch._

  val key = "0a1d74f32382b8a154acacc3a024bdce3709"
  val f = FetchGEO(key)
  //val sra = "ERR2075073" //"SRR3200449"
  //val g = f.getGSM("PRJNA223213")
  //println(g)
  val id = "SRX365810"//"SRX5462269"
  println(f.fetch_bioproject_json(id).unsafeRunSync())
  println("===============")
  val p = f.getExperiment(id)
  /*
  val s = {
    val json = f.fetch_bioproject_json(id).unsafeRunSync()
    exp.getOption(json).map(v=> v.map(_.as[BioProject.ExperimentPackage])).get
  }
  */
  println(p)

  //val r = f.getSRA(sra).head

  //BlackWhite.pprintln(r.toFlatJSON.spaces2)
  //CommandSra.fetchSRA(sra, key, "flatjson")
  /*
  val run_str = f.fetch_sra_runinfo(sra).replace("\r\n", "\n")
  val lst: List[List[String]] = run_str.asCsvReader[List[String]](rfc.withHeader.withCellSeparator(',')).map{
    case Right(r)=> r
    case Left(e) => throw new Exception(s"Cannot parse runinfo with error ${e}!")
  }.toList
  BlackWhite.pprintln(lst)
  println("===")
  val j = lst.head.zip(lst.tail.head).toMap.asJson
  println(j)
  */
  /*
   */
}
