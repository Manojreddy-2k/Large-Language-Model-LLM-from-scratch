package com.utilities

object Environment extends Enumeration {
  type Environment = Value
  val local: Value = Value("local")
  val test: Value = Value("test")
  val cloud: Value = Value("cloud")
}
