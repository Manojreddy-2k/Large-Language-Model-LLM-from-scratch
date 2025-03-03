package com.utilities

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper


class TrainingDataTest extends AnyFlatSpec with Matchers{

  "createInputTokensAndOutputLabels method" should "correctly generate input and output labels" in {
    val bufferedData: Array[Integer] = Array(1, 2, 3, 4, 5)
    val (features, labels) = TrainingDataGen.createInputTokensAndOutputLabels(bufferedData)
    println(features.toList.map(ele => ele.mkString(" ")))
    println(labels.toList.map(ele => ele.mkString(" ")))
    features shouldEqual Array(Array(1), Array(2), Array(3), Array(4))
    labels shouldEqual Array(Array(2), Array(3), Array(4), Array(5))
  }

}
