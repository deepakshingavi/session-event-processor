import entry.processor.StreamProcessor._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.json4s._
import org.json4s.native.Serialization
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite


class StreamProcessorTest extends AnyFunSuite
  with BeforeAndAfterAll {
  implicit val formats = Serialization.formats(NoTypeHints)

  var spark: SparkSession = _
  var baseDf: DataFrame = _

  override protected def beforeAll(): Unit = {
    spark = SparkSession.builder()
      .master("local[1]")
      .getOrCreate()
    val inputPath = "src/test/resources/test_data/"
    baseDf = loadData(spark, inputPath)
  }

  override protected def afterAll(): Unit = {
    spark.close()
  }

  test("test spark data frame load") {
    val inputPath = "src/test/resources/test_data/"
    val baseDf = loadData(spark, inputPath)
    assertResult(115)(baseDf.count())
  }

  test("Count unique anonymous_id by Device") {
    val resultMap : collection.Map[String, Long] = sessionCountBy("device_family",baseDf)
    val expected = collection.Map[String,Long]("Other" -> 42, "iPad" -> 2, "Samsung SM-G530BT" -> 1, "Samsung GT-I9300I" -> 1, "XT1069" -> 1, "iPhone" -> 1)

    assertResult(expected)(resultMap)
  }

  test("Count unique anonymous_id by OS") {
    val resultMap : collection.Map[String, Long] = sessionCountBy("os_family",baseDf)
    val expected = collection.Map[String,Long]("iOS" -> 3, "Linux" -> 1, "Other" -> 41, "Android" -> 3)

    assertResult(expected)(resultMap)
  }

  test("Count unique anonymous_id by browser_family") {
    val resultMap : collection.Map[String, Long] =  sessionCountBy("browser_family",baseDf)
    val expected = collection.Map[String,Long]("Other" -> 41, "Facebook" -> 1, "Chrome Mobile" -> 2, "Firefox" -> 1, "Mobile Safari" -> 3)

    assertResult(expected)(resultMap)
  }



}

