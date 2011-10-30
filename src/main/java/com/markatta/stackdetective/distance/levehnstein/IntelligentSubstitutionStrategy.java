/**
 * Copyright (C) 2011 Johan Andren <johan@markatta.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.markatta.stackdetective.distance.levehnstein;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.markatta.stackdetective.model.Entry;

/**
 * A weighted cost calculator that makes differences in the beginning and end of
 * the stack trace to cost much more than differences mid-trace. Substitution
 * calculation is done with a stack trace specific algorithm.
 * 
 * @author johan
 */
final class IntelligentSubstitutionStrategy implements DistanceCostStrategy {

	private static final Logger LOGGER = Logger.getLogger(IntelligentSubstitutionStrategy.class.getName());

	private static final double NON_SUBSTITUTION_COST = 0.02;

	private static final double DIFFERING_PACKAGES_COST = 0.01;

	private static final double DIFFERING_CLASS_NAME_COST = 0.005;

	private static final double DIFFERING_METHOD_NAME_COST = 0.0010;

	private static final double DIFFERING_LINE_NUMBER_COST = 0.0002;

	private static final double IDENTICAL_COST = 0;

	/**
	 * The same for both add and delete
	 */
	private double nonSubstitutionCost(final List<Entry> list, final int index) {

		return NON_SUBSTITUTION_COST * positionMultiplier(list.size(), index);
	}

	@Override
	public double delete(List<Entry> entries, int index) {
		return nonSubstitutionCost(entries, index);
	}

	@Override
	public double add(List<Entry> entries, int index) {
		// identic
		return nonSubstitutionCost(entries, index);
	}

	@Override
	public double substitute(final List<Entry> entriesA, final int indexA, final List<Entry> entriesB, final int indexB) {
		final Entry entryA = entriesA.get(indexA - 1);
		final Entry entryB = entriesB.get(indexB - 1);

		final double entryDifferenceCost = compareFrames(entryA, entryB);

		// this makes the cost become dependent on the position of the line
		// in the respective lists, see nonSubstitionCost for a more detailed
		// explanation
		final double positionAMultiplier = positionMultiplier(entriesA.size(), indexA);
		final double positionBMultiplier = positionMultiplier(entriesB.size(), indexB);

		// average of the two
		final double positionMultiplier = (positionAMultiplier + positionBMultiplier) / 2;

		return positionMultiplier * entryDifferenceCost;
	}

	private final static double positionMultiplier(int listSize, int position) {
		// cost increases from the center of the list to ~1 * the multiplier
		// on the first and last line of the list
		// but never less than 0.1 of the cost
		final double halfSize = listSize / 2;
		return Math.max(Math.abs((double) position - halfSize + 0.5), 0.1);
	}

	/**
	 * @return The cost of the difference between <code>entryA</code> and
	 *         <code>entryB</code>
	 */
	private double compareFrames(Entry entryA, Entry entryB) {

		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.log(Level.FINE, "Comparing entries:\n" + entryA + "\n" + entryB);
		}

		if (!entryA.getPackageName().equals(entryB.getPackageName())) {
			// not even the same package

			LOGGER.fine("Different package names");
			return DIFFERING_PACKAGES_COST;

		} else if (!entryA.getClassName().equals(entryB.getClassName())) {

			// same package but different class name
			LOGGER.fine("Different class names");
			return DIFFERING_CLASS_NAME_COST;

		} else if (!entryA.getMethodName().equals(entryB.getMethodName())) {
			// same class but not same method

			LOGGER.fine("Different method names");
			return DIFFERING_METHOD_NAME_COST;
			
		} else if (entryA.getLineNumber() != entryB.getLineNumber()) {
			// same class and method but different lines

			LOGGER.fine("Different line numbers");
			return DIFFERING_LINE_NUMBER_COST;
			
		} else {
			// exact match, we should never get here

			LOGGER.fine("Identical entries");
			return IDENTICAL_COST;
		}
	}
}
