package com.markatta.stackdetective.api.distance

import com.markatta.stackdetective.api.Function

/** A weighted cost calculator that makes differences in the beginning and end of
  * the stack trace to cost much more than differences mid-trace. Substitution
  * calculation is done with a stack trace specific algorithm.
  */
class IntelligentSubsitutionCosts extends Costs {

  val nonSubstitutionCost = 0.02
  val differentPackageCost = 0.01
  val differentClassCost = 0.005
  val differentMethodNameCost = 0.0010
  val differentLineNumberCost = 0.0002
  val identicalCost = 0.0

  def delete(entries: Seq[Function], index: Int) =
    nonSubstitutionCost * positionMultiplier(entries.size, index)

  def add(entries: Seq[Function], index: Int) = delete(entries, index)

  def substitute(a: Seq[Function], indexA: Int, b: Seq[Function], indexB: Int) = {
    val f1 = a(indexA - 1)
    val f2 = b(indexB - 1)

    val entryDifferenceCost = compareFunctions(f1, f2)

    // this makes the cost become dependent on the position of the line
    // in the respective lists, see nonSubstitionCost for a more detailed
    // explanation
    val multiplier = (positionMultiplier(a.size, indexA) * positionMultiplier(b.size, indexB)) / 2

    entryDifferenceCost * multiplier
  }

  private def compareFunctions(f1: Function, f2: Function): Double = {

    if (f1.packageName != f2.packageName) {
      differentPackageCost
    } else if (f1.className != f2.className) {
      differentClassCost
    } else if (f1.methodName != f2.methodName) {
      differentMethodNameCost
    } else if (f1.location != f2.location) {
      differentLineNumberCost
    } else {
      identicalCost
    }
  }


  private def positionMultiplier(listSize: Int, position: Int): Double =  {
    // cost increases from the center of the list to ~1 * the multiplier
    // on the first and last line of the list
    // but never less than 0.1 of the cost
    val halfSize = listSize / 2.0;
    math.max(math.abs(position - halfSize + 0.5), 0.1);
  }
}
