package entry

import entry.processor.StreamProcessor.{columnMap, loadData, sessionCountBy, defaultSpark}
import org.apache.log4j.Logger
import org.json4s.native.Serialization
import org.json4s.{Formats, NoTypeHints}

import scala.util.Left
import scala.util.control.Breaks

object LocalMain {

  val logger = Logger.getLogger(LocalMain.getClass)

  def main(args: Array[String]): Unit = {
    logger.info("Spark job start ")
    implicit val formats: AnyRef with Formats = Serialization.formats(NoTypeHints)

    val spark = defaultSpark()

    val consoleReader = scala.io.StdIn
    println("Type quit for terminating the app : ")
    println()

    println("Please enter source file Path : ")
    val inputPath = consoleReader.readLine()

    val baseDf = loadData(spark,inputPath).cache()

    Breaks.breakable {
      while (true) {
        println(" You need unique session count by (Browser/Device/Operation System) or (Quit)? ")
        val columnName = consoleReader.readLine().toUpperCase
        if(columnName.equalsIgnoreCase("QUIT")){
          Breaks.break()
        }

        val output: Either[String, collection.Map[String, Long]] = columnMap.getOrElse(columnName,"NA") match {
          case "NA" => Left("Invalid column name. Try again.")
          case columnName: String => Right(sessionCountBy(columnName, baseDf))
        }

        println( output match {
          case Left(x) => x
          case Right(x) =>
            s"*** Result start *** \n ${Serialization.write(x)} *** Result finish***"
        })

      }
    }

    logger.info("Spark job terminated ")

  }
}
