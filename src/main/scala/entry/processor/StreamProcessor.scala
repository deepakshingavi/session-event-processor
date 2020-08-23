package entry.processor

import org.apache.log4j.Logger
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{LongType, MapType, StringType}
import org.apache.spark.sql.{Column, DataFrame, SparkSession}

object StreamProcessor {

  val logger = Logger.getLogger(StreamProcessor.getClass)

  val columnMap = Map("BROWSER" -> "browser_family", "OPERATIONAL SYSTEM" -> "os_family", "DEVICE" -> "device_family")

  /**
   * Expression to merge list of maps
   */
  val mergeExpr: Column = expr("aggregate(result, map(), (acc, i) -> map_concat(acc, i))")

  def loadData(spark: SparkSession, inputPath: String): DataFrame = {
    spark.read.json(inputPath)
  }

  def processMobileEvents(spark: SparkSession, inputPath: String, columnName: String): collection.Map[String, Long] = {
    val df = loadData(spark, inputPath)
    sessionCountBy(columnName, df)
  }


  def sessionCountBy(columnName: String, df: DataFrame): collection.Map[String, Long] = {
    df.groupBy(col(columnName))
      .agg(countDistinct("anonymous_id").as("eventCount"))
      .select(map(col(columnName), col("eventCount")).as("mapColumn"))
      .withColumn("tempColumn", lit(1))
      .groupBy(col("tempColumn"))
      .agg(collect_list(col("mapColumn")).as("result"))
      .select(mergeExpr.cast(MapType(StringType, LongType)).as("finalResult"))
      .first()
      .getMap[String, Long](0)
  }

  def startSpark(masterUrl: String): SparkSession = {
    SparkSession
      .builder()
      .master(masterUrl)
      .getOrCreate()
  }

  def defaultSpark(): SparkSession = {
    SparkSession
      .builder()
      .getOrCreate()
  }

}
