GEO-fetch - library to fetch data from GEO
==========================================

GEO-fetch is a library to fetch metadata from [Gene Expression Omnibus](https://www.ncbi.nlm.nih.gov/geo/).

Adding to dependencies
----------------------

add the following to you build.sbt
```scala
resolvers += sbt.Resolver.bintrayRepo("comp-bio-aging", "main")
libraryDependencies += "group.aging-research" %% "geo-fetch" % "0.0.13"
```

Usage in code
-------------

```
val key = "" //your NCBI API key (empty by default)
val f =  FetchGEO(key)
val gsm = "GSM1698568"
val g: GSM = f.getGSM(gsm, true) //get GSM
val runs: List[RunInfo] = g.runs //read SRA runs of the GSM
```

Usage as a console tool
-----------------------

You can also use it from the docker container:
```bash
docker run https://quay.io/repository/comp-bio-aging/geo-fetch
```