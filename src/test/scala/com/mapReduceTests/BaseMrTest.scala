package com.mapReduceTests

import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import java.nio.file.{Files, Paths}

class BaseMrTest extends AnyFlatSpec with Matchers with BeforeAndAfter{
  val directoryPath = "/Users/tnithish/Learning/cs-401/building-a-Large-Language-Model-LLM-from-scratch/src/main/resources/output/" // Replace with your directory path
  val directory = Paths.get(directoryPath)

  before {
    // Check if the directory exists
    if (Files.exists(directory) && Files.isDirectory(directory)) {
      // List all files in the directory and delete them
      Files.list(directory).forEach { file =>
        if (Files.isRegularFile(file)) { // Check if it is a file
          Files.delete(file) // Delete the file
          println(s"Deleted: ${file.getFileName}")
        } else {
          Files.walk(file).sorted(java.util.Comparator.reverseOrder()).forEach(Files.delete)
        }
      }
    } else {
      println(s"Directory does not exist: $directoryPath")
    }
  }

  after {
    //delete the files in the output directory after the completion of the program
    if (Files.exists(directory) && Files.isDirectory(directory)) {
      Files.list(directory).forEach(file => {
        if (!Files.isRegularFile(file)) {
          Files.walk(file).sorted(java.util.Comparator.reverseOrder()).forEach(Files.delete)
        }
      })
    }
  }
}
