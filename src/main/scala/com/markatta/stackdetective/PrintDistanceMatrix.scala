package com.markatta.stackdetective

import api.{UnparseableTrace, StackTraceParser}
import com.markatta.stackdetective.api.distance.Distance
import collection.parallel.mutable.ParArray

object PrintDistanceMatrix {

  def main(args: Array[String]) {
    if (args.length < 1) {
      println("Usage: DistanceMatrix testFile1 [testFile2 ...]");
      println(" where the testfile contains one stack trace each.");
      println(" Each stack trace found will be compared to ");
      println(" all other stack traces.");

    } else {

      val startTime = System.currentTimeMillis

      val traces = args.map {
        path =>
          val source = scala.io.Source.fromFile(path)
          val lines = source.getLines().mkString("\n")
          source.close()
          try {
            Some(StackTraceParser().parse(lines))
          } catch {
            case e: UnparseableTrace => {
              println("Failed to parse " + path + ":\n" + e.getMessage)
              None
            }
          }
      }.flatten


      val distances = Array.tabulate[Double](traces.size, traces.size)((x: Int, y: Int) => {
        val t1 = traces(x)
        val t2 = traces(y)
        Distance.defaultDistanceAlgorithm(t1, t2)
      })

      println("Score for each compared to each of the others and itself (0 is not alike at all, 1 is identical):\n   \t");
      for (x <- 0 until traces.size) {
        printf("% 10d\t", x + 1)
      }
      print("\n");

      for (x <- 0 until distances.size) {
        printf("% 3d\t", x + 1);
        for (y <- 0 until distances.size) {
          printf("% 10f\t", distances(x)(y))
        }
        print("\n")
      }

      val executionTime = System.currentTimeMillis - startTime;
      println("Total execution time " + executionTime + " ms");

    }
  }
}
