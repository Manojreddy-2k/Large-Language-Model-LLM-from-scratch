package com.utilities

import org.apache.hadoop.io.Text
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.jdk.CollectionConverters.IteratorHasAsJava

class VectorEmbeddingReducerUtilityTest extends AnyFlatSpec with Matchers{

  "The help deserialize the dimension array and " should "correctly averages all the vectors for a given key" in {
    val bufferedData: Array[Integer] = Array(1, 2, 3, 4, 5)
    val input = Array("[2.000, 3.000, 4.000]", "[4.0, 5.000, 6.00]")
    val expected = Array(3, 4, 5)
    val actual = VectorEmbedUtilities.calculateAverage(input.iterator.map(ele => new Text(ele)).asJava)
    print(actual.mkString(", "))
    expected should equal(actual)
  }

}
