package entry

import entry.processor.StreamProcessor
import org.apache.log4j.Logger

object ClusterMain {
  val logger = Logger.getLogger(ClusterMain.getClass)
  def main(args: Array[String]): Unit = {
    logger.error("Spark Job started")
    /*if(args.isEmpty){
      logger.error("JSON source file file not found")
      System.exit(-1)
    }*/

    val spark = StreamProcessor.defaultSpark()
    val baseDf = StreamProcessor.loadData(spark,"/spark/conf/Teste/")

    StreamProcessor.columnMap.values.foreach(columnName => {
      StreamProcessor.sessionCountBy(columnName,baseDf)
    })
    logger.error("Spark Job finished")
  }

}
