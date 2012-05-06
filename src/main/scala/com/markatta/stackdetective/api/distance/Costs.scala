package com.markatta.stackdetective.api.distance

import com.markatta.stackdetective.api.Function

/** Each method returns a cost of the operation given the index and list of entries.
  * A higher cost means a longer distance because of the operation. The cost may be negative.
  */
trait Costs {
  /** @return The cost to delete the entry at '''index''' in  '''tries'''*/
  def delete(entries: Seq[Function], index: Int): Double

  /** @return The cost to add an entry at '''index''' in  '''tries'''*/
  def add(entries: Seq[Function], index: Int): Double

  /** @return The cost of replacing the entry at '''indexA''' in '''entriesA'''
    *         with the entry at '''indexB''' from '''entriesB'''
    */
  def substitute(entriesA: Seq[Function], indexA: Int, entriesB: Seq[Function], indexB: Int): Double
}
