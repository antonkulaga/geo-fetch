package geo.cli

import com.monovore.decline._

object Main extends CommandApp(
  name = "GEO-Fetch",
  header = "GEO fetch application",
  main = {
    MainCommand.mainCommand.map{ _=>
      //just to run it
    }
  }

)


object MainCommand extends CommandsGSM with CommandSra {

  lazy val mainCommand: Opts[Unit] = Opts.subcommand(fetch_sra) orElse Opts.subcommand(fetch_gsm) //orElse Opts.subcommand(fetch_gse)


}