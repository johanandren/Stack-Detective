package com.markatta.stackdetective.api.distance

import com.markatta.stackdetective.api.Function

/** Just handles location, compares stack elements with equals. Differences
  * in the end are more expensive than differences in the beginning of the
  * stack traces.
  */
class FavorBeginCosts extends Costs {

  def delete(entries: Seq[Function], index: Int) = 1.0 + index

  def add(entries: Seq[Function], index: Int) = delete(entries, index)

  def substitute(a: Seq[Function], indexA: Int, b: Seq[Function], indexB: Int) = {
    val entryA = a(indexA - 1);
    val entryB = b(indexB - 1);

    if (entryA.equals(entryB)) {
      0.0
    } else {
      1.0 + ((indexA + indexB) / 2.0);
    }
  }
}
