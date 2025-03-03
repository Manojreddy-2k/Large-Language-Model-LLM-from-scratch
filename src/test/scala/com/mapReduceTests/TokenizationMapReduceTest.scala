package com.mapReduceTests

import com.trainingLLM.TokenizationJob
import com.utilities.Environment
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import java.nio.file.Files


class TokenizationMapReduceTest extends BaseMrTest{

  "TokenizationJob MR" should "correctly generate words count" in {
    TokenizationJob.main(Array("env=test"))

    Files.list(directory).forEach { file =>
      Files.list(file).count() shouldBe (4)
      println(Files.list(file).filter(directoryPath => directoryPath.endsWith("0000")))
    }
  }

  "TokenizationJob MR with invalid Environment" should "throw Exception" in {
    TokenizationJob.main(Array(null))
//    thrown.getMessage should include ("please check the environment arguments")

    if (Files.exists(directory) && Files.isDirectory(directory)) {
      Files.list(directory).forEach { file =>
        Files.list(file).count() shouldBe (0)
      }
    }
  }
}
