package com.trainingLLM

import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.EncodingType
import org.apache.hadoop.io._
import org.apache.hadoop.mapred._

import java.io.IOException
import java.util
import com.utilities.{Environment, JobConfigurationHelper}
import org.slf4j.LoggerFactory

import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.util.{Failure, Success, Try}

/**
 * Hadoop MAP-REDUCE Implementation for finding word count in a huge corpus of text
 * We are dividing the data into multiple shards, and then after ordering them, finally
 * finding the word-count at reducer.
 */
object TokenizationJob {
  private val logger = LoggerFactory.getLogger(getClass)
  private class TokenizerMapper extends MapReduceBase with Mapper[LongWritable, Text, Text, IntWritable] {
    private final val one = new IntWritable(1)
    private val word = new Text()
    private val encoding = Encodings.newDefaultEncodingRegistry().getEncoding(EncodingType.CL100K_BASE)

    /**
     * Implementation of mapper functionality,
     * Extracting words from the shard and storing the key-value pairs in collector
     */
    @throws[IOException]
    override def map(key: LongWritable, value: Text, output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit = {
      value.toString.toLowerCase().split("\\W+").filter(_.nonEmpty).foreach( token => {
        val encodedString =  encoding.encode(token.toLowerCase())
//        logger.info("Tokens Count " )
        word.set(token + "  " + encodedString.toString)
        output.collect(word, one)
      })
    }
  }

  /**
   * Implementation of Reducer functionality,
   * Here we for each reducer we are receiving a sorted list of similar words and it's an IntIterable
   * we need to count that iterable, which is the count of the word.
   */
  private class IntSumReducer extends MapReduceBase with Reducer[Text, IntWritable, Text, IntWritable] {
    override def reduce(key: Text, values: util.Iterator[IntWritable], output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit = {
      val sum = values.asScala.map(_.get()).sum
      output.collect(key, new IntWritable(sum))
    }
  }

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      logger.error("Environment not setup")
      sys.exit(-1)
    }

    val result = Try {
      val envValue = Environment.values.find(_.toString == args(0).split("=")(1)).get
      logger.debug("Environment::::" + envValue)

      if (Environment.values.contains(envValue)) {
        runJob(envValue)
      }
      else {
        logger.error("The given Env value is Invalid, please check again and retry")
      }
    }

    result match {
      case Success(_) => logger.info("Tokenization Job ran successfully.")
      case Failure(exception) => logger.error(
        s"An error occurred, please check the environment arguments: ${exception.getMessage}"
      )
    }

  }

  def runJob(env: Environment.Value): RunningJob = {
    val conf: JobConf = JobConfigurationHelper.getJobConfig("WordCount", this.getClass, env)

    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[IntWritable])
    conf.setMapperClass(classOf[TokenizerMapper])
    conf.setCombinerClass(classOf[IntSumReducer])
    conf.setReducerClass(classOf[IntSumReducer])
    conf.setInputFormat(classOf[TextInputFormat])
    conf.setOutputFormat(classOf[TextOutputFormat[Text, IntWritable]])
    JobClient.runJob(conf)
  }
}