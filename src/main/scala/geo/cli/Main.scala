package geo.cli

import com.monovore.decline._

object Main extends CommandApp(
  name = "GEO-Fetch",
  header = "GEO fetch application",
  main = {
    MainCommand.mainCommand.map{ _=>
      //just to run it
      System.exit(0)
    }
  }

)


object MainCommand extends CommandsBioProject with CommandsIndex {

  lazy val mainCommand: Opts[Unit] = Opts.subcommand(fetch_sra) orElse
    Opts.subcommand(fetch_gsm) orElse
    Opts.subcommand(fetch_bioproject) orElse
    Opts.subcommand(samples_index)

}