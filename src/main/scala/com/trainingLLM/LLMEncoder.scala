package com.trainingLLM

import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.EncodingType
import org.apache.hadoop.io._
import org.apache.hadoop.mapred._

import java.io.IOException
import scala.collection.JavaConverters._
import com.trainingLLM.Constants._
import com.config.{ConfigLoader, MultiLayerNetworkModel}
import com.utilities.{Environment, JobConfigurationHelper, TrainingDataGen, VectorEmbedUtilities}
import org.nd4j.linalg.factory.Nd4j
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}


/**
 * MAP-REDUCE Implementation of training a huge tokenized text over a model
 * and generating it's vector embeddings of each word.
 */
object LLMEncoder {
  private val logger = LoggerFactory.getLogger(getClass)

  /**
   * This Mapper implementation is used to convert sentences into tokens, and outputs word, with its token and Vector-embedding
   *
   */
  class ModelTrainMapper extends MapReduceBase with Mapper[LongWritable, Text, Text, Text] {
    private val word = new Text()
    private val embedding = new Text()
    private val encoding = Encodings.newDefaultEncodingRegistry().getEncoding(EncodingType.CL100K_BASE)
    private val model = MultiLayerNetworkModel.getModel
    private val epochs: Int = ConfigLoader.getConfig(EPOCHS).toInt

    @throws[IOException]
    override def map(key: LongWritable, sentence: Text, output: OutputCollector[Text, Text], reporter: Reporter): Unit = {
      if (sentence.toString.trim.isEmpty) {
        logger.debug("Current sentence is Invalid")
        return
      }

      //1. Using Jtokkit we are generating the tokens for a sentence.
      val tokens = encoding.encode(sentence.toString)
      logger.info("Tokens Count " + tokens.size())

      if (tokens.size() <= 1) {
        logger.debug("Tokens not sufficient to train")
        return
      }

      // 2. Generating input and output labels,
      val (features, labels) = TrainingDataGen.createInputTokensAndOutputLabels(tokens.asScala.toArray)
      val inputFeatures = Nd4j.create(features)
      val outputLabels = Nd4j.create(labels)


      if (features.length == 0 || labels.length == 0) {
        logger.debug("Tokens not generated for this shard")
        return
      }

      // 3. Training the model using multiple epochs,
      Iterator.range(0, epochs).foreach { _ =>
        model.fit(inputFeatures, outputLabels)
      }

      val embeddings = model.getLayer(0).getParam("W")

      // 4. Storing the output in the form of Word and it's vector eg: "Hello [-1.000, 2.300, 3.60]"
      tokens.asScala.foreach(token => {
        word.set(encoding.decode(List(token).asJava) + "  " +token.toString)
        embedding.set(embeddings.getRow(token.longValue()).toStringFull)
        output.collect(word, embedding)
      })
    }
  }

  /**
   * This Reducer implementation takes, key value pair from the mapper, <Word , Embedding>
   * for similar words it is averaging the vector embeddings.
   *
   */
  class EmbeddingAverageReducer extends MapReduceBase with Reducer[Text, Text, Text, Text] {
    override def reduce(key: Text, values: java.util.Iterator[Text], output: OutputCollector[Text, Text], reporter: Reporter): Unit = {
      val average = VectorEmbedUtilities.calculateAverage(values)
      output.collect(key, new Text(average.mkString("[", ", ", "]")))
    }
  }

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      logger.error("Environment not setup")
      sys.exit(-1)
    }

    val result = Try {
      val envValue: Environment.Value = Environment.values.find(_.toString == args(0).split("=")(1)).get
      logger.info("Environment::::" + envValue)

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

  def runJob(env : Environment.Value): RunningJob = {
    val conf = JobConfigurationHelper.getJobConfig("LLMEncoder", this.getClass, env)
    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[Text])
    conf.setMapperClass(classOf[ModelTrainMapper])
    conf.setCombinerClass(classOf[EmbeddingAverageReducer])
    conf.setReducerClass(classOf[EmbeddingAverageReducer])
    conf.setInputFormat(classOf[TextInputFormat])
    conf.setOutputFormat(classOf[TextOutputFormat[Text, Text]])
    JobClient.runJob(conf)
  }
}
