package com.trainingLLM

object Constants {
  val HDFS_URL: String = "fs.defaultFS"
  val MAP_REDUCE_JOB_REDUCERS: String = "mapreduce.job.reduces"
  val MAX_SPLIT_SIZE_PARAM: String = "mapreduce.input.fileinputformat.split.maxsize" // 64 MB in bytes

  val HADOOP_HDFS_URL: String = "hadoop." + HDFS_URL
  val HADOOP_MAP_REDUCE_JOB_REDUCERS: String = "hadoop." + MAP_REDUCE_JOB_REDUCERS
  val HADOOP_MAX_SPLIT_SIZE_PARAM: String = "hadoop." + MAX_SPLIT_SIZE_PARAM
  val EPOCHS: String = "epochs"

  // Other Application Constants
  val LOGGING_LEVEL: String = "INFO"
}