package com.utilities

object TrainingDataGen {
  def createInputTokensAndOutputLabels(data: Array[Integer]): (Array[Array[Int]], Array[Array[Int]]) = {
    val features: Array[Array[Int]] = data.map(ele => Array(ele))
    val labels: Array[Array[Int]] = data.map(ele => Array(ele))
    return (features.slice(0, features.length-1), labels.slice(1, labels.length))
  }
}
