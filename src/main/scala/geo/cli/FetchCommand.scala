package geo.cli

import better.files._
import com.monovore.decline._
import wvlet.log.LogSupport
class FetchCommand extends LogSupport{

  protected lazy val output = Opts.option[String](long = "output", short = "o", help = "File to save the result, prints to stdout if not set").withDefault("")
  protected lazy val key = Opts.option[String](long = "key", short = "k", help = "NCBI API KEY").withDefault("")

  def printOrSave(result: String, o: String) = {
    if(o=="") println(result) else{
      println(s"saving results to ${o}")
      File(o).write(result)
    }
  }
}
