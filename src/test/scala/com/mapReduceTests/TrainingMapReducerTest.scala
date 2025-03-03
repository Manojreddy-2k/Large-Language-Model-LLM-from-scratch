package com.mapReduceTests

import com.trainingLLM.LLMEncoder
import com.utilities.Environment
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import java.nio.file.Files

class TrainingMapReducerTest extends BaseMrTest {

  "LLMEncoder MR" should " generate vector embeddings for the vocab it train" in {
    LLMEncoder.main(Array("env=test"))

    Files.list(directory).forEach { file =>
      Files.list(file).count() shouldBe (4)
    }
  }

  "LLMEncoder MR with invalid Environment" should "not run at all" in {
    LLMEncoder.main(Array(null))
//    thrown.getMessage should include ("Invalid environment value")

    if (Files.exists(directory) && Files.isDirectory(directory)) {
      Files.list(directory).forEach { file =>
        Files.list(file).count() shouldBe (0)
      }
    }
  }
}
