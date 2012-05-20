package com.markatta.stackdetective

import api.{UnparseableTrace, StackTraceParser}
import com.markatta.stackdetective.api.distance.Distance
import collection.parallel.mutable.ParArray
import collection.parallel.ParSeq

object PrintDistanceMatrix {

  def main(args: Array[String]) {
    if (args.length < 1) {
      println("Usage: DistanceMatrix testFile1 [testFile2 ...]");
      println(" where the testfile contains one stack trace each.");
      println(" Each stack trace found will be compared to ");
      println(" all other stack traces.");

    } else {

      val traces = args.par.map { path =>
        val source = scala.io.Source.fromFile(path)
        val lines = source.getLines().mkString("\n")
        source.close()
        try {
          Some(StackTraceParser().parse(lines))
        } catch {
          case e: UnparseableTrace => {
            None
          }
        }
      }.flatten


      val distances = ParSeq.tabulate[Double](traces.size, traces.size)((x: Int, y: Int) => {
        val t1 = traces(x)
        val t2 = traces(y)
        Distance.defaultDistanceAlgorithm(t1, t2)
      })




      println("Score for each compared to each of the others and itself (distance 0 is identical):\n   \t");
      for (x <- 0 until traces.size) {
        printf("%d;", x + 1)
      }
      print("\n");

      for (x <- 0 until distances.size) {
        printf("%d;", x + 1);
        for (y <- 0 until distances.size) {
          printf("%f;", distances(x)(y))
        }
        print("\n")
      }

    }
  }
}
