package geo.cli

import java.nio.file.{Path, Paths}

import wvlet.log.LogSupport
import java.nio.file.Path

import better.files._
import cats.implicits._
import com.monovore.decline._
import geo.fetch.FetchGEO
import geo.models.RunInfo
import wvlet.log.LogSupport
import pprint.PPrinter.BlackWhite

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

/*
object MainCommands extends SraCommands

trait SraCommands extends LogSupport{


  protected lazy val path: Opts[Path] = Opts.argument[Path]("file or folder to read from")
  protected lazy val delimiter: Opts[String] = Opts.option[String](long = "delimiter", short = "d", help = "delimiter to be used when parsing PWMs").withDefault("auto")
  protected lazy val verbose: Opts[Boolean] = Opts.flag("verbose", short="v", help="show values of the found PWMs").map(_=>true).withDefault(false)

  lazy val mainCommand =Command(
    name = "list",
    header = "Lists known files"
  )
}
*/
