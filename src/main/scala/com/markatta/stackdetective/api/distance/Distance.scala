package com.markatta.stackdetective.api.distance

import collection.mutable.ArrayBuffer
import com.markatta.stackdetective.api._

object Distance {

  type DistanceAlgorithm = (StackTrace, StackTrace) => Double

  abstract sealed class Operation
  final case object None extends Operation
  final case object Add extends Operation
  final case object Delete extends Operation
  final case object Substitute extends Operation

  val defaultDistanceAlgorithm: DistanceAlgorithm = levehnstein(new IntelligentSubsitutionCosts)

  private def levehnstein(costs: Costs)(trace1: StackTrace, trace2: StackTrace): Double = {
    def segmentDistance(s1: Segment, s2: Segment): Double = {
      val fs1 = s1.entries
      val fs2 = s2.entries
      val distance = Array.ofDim[Double](fs1.size + 1, fs2.size + 1)
      val operations = Array.ofDim[Operation](fs1.size + 1, fs2.size + 1)

      def setupDefaultCosts() {
        distance(0)(0) = 0.0
        operations(0)(0) = None

        // (0)(*) contains the cost to add any sub-stracktrace of fs2 to an empty trace fs1
        for (y <- 1 to fs2.size) {
          distance(0)(y) = costs.add(fs2, y)
          operations(0)(y) = Add
        }

        // (*)(0) contains the cost to add any sub-stacktrace of fs1 to an empty trace fs2
        for (x <- 1 to fs1.size) {
          distance(x)(0) = distance(x - 1)(0) + costs.add(fs1, x)
          operations(x)(0) = Add
        }
      }

      /** Flood fill the distance array so that any position (x)(y) contains
        * the minimum distance between the substring 0-x from entry list fs1 and
        * 0-y from entry list fs2
        */
      def floodFillDistances() {

        def setCostFor(x: Int, y: Int) {
          val Deletion = distance(x - 1)(y) + costs.delete(fs1, x)
          val Insertion = distance(x)(y - 1) + costs.add(fs2, y)
          val Substitution = distance(x - 1)(y - 1) + costs.substitute(fs1, x, fs2, y)
          val minimum = math.min(Deletion, math.min(Insertion, Substitution))

          distance(x)(y) = minimum
          operations(x)(y) = minimum match {
            case Deletion => Delete
            case Insertion => Add
            case Substitution => Substitute
          }

        }

        for (x <- 1 to fs1.size; y <- 1 to fs2.size) {
          setCostFor(x, y)
        }
      }

      setupDefaultCosts()
      floodFillDistances()

      // and the shortest path is in the lower right coords
      distance(fs1.size)(fs2.size)
    }

    trace1.segments.zip(trace2.segments).map { case (segment1, segment2) =>
      segmentDistance(segment1, segment2)
    }.sum
  }

}
