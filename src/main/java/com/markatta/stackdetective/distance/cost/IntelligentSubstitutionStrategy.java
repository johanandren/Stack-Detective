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
package com.markatta.stackdetective.distance.cost;

import com.markatta.stackdetective.model.Entry;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * A weighted cost calculator that makes differences in the beginning and end of the stack
 * trace to cost much more than differences mid-trace. Substitution calculation is done
 * with a stack trace specific algorithm.
 *
 * @author johan
 */
public final class IntelligentSubstitutionStrategy implements DistanceCostStrategy {

    private static final Logger LOGGER = Logger.getLogger(IntelligentSubstitutionStrategy.class);

    private static final int DIFFERING_EXCEPTION_COST = 100000;
    
    private static final int NON_SUBSTITUTION_COST = 200;

    private static final int DIFFERING_PACKAGES_COST = 100;

    private static final int DIFFERING_CLASS_NAME_COST = 50;

    private static final int DIFFERING_METHOD_NAME_COST = 10;

    private static final int DIFFERING_LINE_NUMBER_COST = 2;

    private static final int IDENTICAL_COST = 0;

    /**
     * The same for both add and delete
     */
    private int nonSubstitutionCost(List<Entry> list, int index) {
        int halfSize = list.size() / 2;
        // at least 1, growing with the distance from the middle of the stacktrace
        // example stacktrace is 10 long, half is 5 an operation at 5 gives a
        // multiplier of 1 + abs(5 - 5) = 1
        // the first line
        // 1 + abs(0 - 5) = 6
        // end line
        // 1 + abs(10 - 5) = 6
        int positionMultiplier = 1 + Math.abs(index - halfSize);

        return NON_SUBSTITUTION_COST * positionMultiplier;
    }

    @Override
    public int delete(List<Entry> entries, int index) {
        return nonSubstitutionCost(entries, index);
    }

    @Override
    public int add(List<Entry> entries, int index) {
        // identic
        return nonSubstitutionCost(entries, index);
    }

    @Override
    public int substitute(List<Entry> entriesA, int indexA, List<Entry> entriesB, int indexB) {
        Entry entryA = entriesA.get(indexA - 1);
        Entry entryB = entriesB.get(indexB - 1);

        int entryDifferenceCost = compareFrames(entryA, entryB);
        
        // this makes the cost become dependent on the position of the line
        // in the respective lists, see nonSubstitionCost for more detailed explanaition
        int halfSizeA = entriesA.size() / 2;
        int halfSizeB = entriesB.size() / 2;
        int positionMultiplier = (halfSizeA * Math.abs(indexA - halfSizeA) + halfSizeB - Math.abs(indexB - halfSizeB));

        return  positionMultiplier * entryDifferenceCost;

    }

    /**
     * @return The cost of the difference between <code>entryA</code> and <code>entryB</code>
     */
    private int compareFrames(Entry entryA, Entry entryB) {

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Comparing entries:\n" + entryA + "\n" + entryB);
        }

        if (!entryA.getPackageName().equals(entryB.getPackageName())) {
            // not even the same package
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Different package names");
            }
            return DIFFERING_PACKAGES_COST;

        } else if (!entryA.getClassName().equals(entryB.getClassName())) {
            // same package but different class name
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Different class names");
            }
            return DIFFERING_CLASS_NAME_COST;

        } else if (!entryA.getMethodName().equals(entryB.getMethodName())) {
            // same class but not same method
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Different method names");
            }
            return DIFFERING_METHOD_NAME_COST;
        } else if (entryA.getLineNumber() != entryB.getLineNumber()) {
            // same class and method but different lines
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Different line numbers");
            }
            return DIFFERING_LINE_NUMBER_COST;
        } else {
            // exact match, we should never get here
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Identical entries");
            }
            return IDENTICAL_COST;
        }
    }

    @Override
    public int exceptionDistance(String exceptionA, String exceptionB) {
       if (exceptionA.equals(exceptionB)) {
           return IDENTICAL_COST;
       } else {
           return DIFFERING_EXCEPTION_COST;
       }
    }
}
