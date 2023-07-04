import org.apache.spark
import org.apache.spark.sql.{SparkSession, functions}
import org.apache.spark.sql.functions.{col, explode}

object Scala2 {
  def main(args: Array[String]): Unit = {

  val sparkSession =  SparkSession.builder().master("local").appName("ScalaInput").getOrCreate()
  sparkSession.sparkContext.setLogLevel("ERROR")

  val host = "127.0.0.1"
  val port = "9999"

  val initDF = sparkSession.readStream.format("socket")
    .option("host",host)
    .option("port", port)
    .load()

    println("Streaming via port" , initDF.isStreaming)

    val wordCount = (initDF
      .select(explode(functions.split(col("value"), " ")).alias("words"))
      .groupBy("words")
      .count()
      )


    wordCount
      .writeStream
      .outputMode("update") // Try "update" and "complete" mode.
      .option("truncate", false)
      .format("console")
      .start()
      .awaitTermination()

    // Print Schema of DataFrame
    println("Schema of DataFame wordCount.")
    println(wordCount.printSchema())

}
}
