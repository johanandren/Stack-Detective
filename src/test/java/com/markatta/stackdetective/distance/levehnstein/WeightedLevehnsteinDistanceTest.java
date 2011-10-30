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

import com.markatta.stackdetective.distance.levehnstein.FavourBeginStrategy;
import com.markatta.stackdetective.distance.levehnstein.WeightedLevehnsteinDistance;
import com.markatta.stackdetective.model.StackTrace;
import com.markatta.stackdetective.parse.NaiveTextParser;
import org.junit.Test;
import static org.junit.Assert.*;

public class WeightedLevehnsteinDistanceTest {

    @Test
    public void equalTracesGivesResultOf1() {
        NaiveTextParser parser = new NaiveTextParser();
        StackTrace stackTrace = parser.parse("java.lang.NullPointerException\n"
                + "        at com.markatta.stackdetective.StackTrace.parseStackTrace(StackTrace.java:21)\n"
                + "        at com.markatta.stackdetective.StackTraceTest.testParseStackTrace(StackTraceTest.java:15)\n");

        WeightedLevehnsteinDistance instance = new WeightedLevehnsteinDistance(new FavourBeginStrategy());
        assertEquals(1, instance.calculateDistance(stackTrace, stackTrace), 0.0000001);
    }

    @Test
    public void nonEqualHaveDifference() {
        NaiveTextParser parser = new NaiveTextParser();
        StackTrace trace1 = parser.parse("Exception in thread \"main\" java.lang.RuntimeException: Error running test app\n"
                + "	at com.markatta.stackdetective.TestApp.main(TestApp.java:61)\n"
                + "Caused by: java.lang.ArrayIndexOutOfBoundsException: 130\n"
                + "	at com.markatta.stackdetective.FavourBeginDistanceCalculator.calculateDistance(FavourBeginDistanceCalculator.java:38)\n"
                + "	at com.markatta.stackdetective.TestApp.main(TestApp.java:51)\n");
        StackTrace trace2 = parser.parse("java.lang.NullPointerException\n"
                + "        at com.markatta.stackdetective.StackTrace.parseStackTrace(StackTrace.java:21)\n"
                + "        at com.markatta.stackdetective.StackTraceTest.testParseStackTrace(StackTraceTest.java:15)\n");

        WeightedLevehnsteinDistance instance = new WeightedLevehnsteinDistance(new FavourBeginStrategy());
        double result = instance.calculateDistance(trace1, trace2);
        assertTrue("Expected result to be < 1 as the traces are different, was " + result, result < 1.0);

    }
}