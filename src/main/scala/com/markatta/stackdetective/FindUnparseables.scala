package com.markatta.stackdetective

import api.{UnparseableTrace, StackTraceParser}


object FindUnparseables {

  def main(args: Array[String]) {

    if (args.length < 1) {
      println("Usage: FindUnparseables testFile1 [testFile2 ...]");
      println(" where the testfile contains one stack trace each.");
      println(" Each stack trace found will be compared to ");
      println(" all other stack traces.");

    } else {

      val traces = args.par.foreach { path =>
        val source = scala.io.Source.fromFile(path)
        val lines = source.getLines().mkString("\n")
        source.close()
        try {
          StackTraceParser().parse(lines)
        } catch {
          case e: UnparseableTrace => {
            println("======")
            println(path)
            println("======")
            println(e)
          }
        }
      }
    }
  }
}
