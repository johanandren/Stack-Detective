package com.markatta.stackdetective.distance;

import com.markatta.stackdetective.distance.cost.FavourBeginStrategy;
import com.markatta.stackdetective.StackTrace;
import com.markatta.stackdetective.parse.StackTraceTextParser;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class DefaultDistanceCalculatorTest {

    @Test
    public void equalTracesHave0Difference() {
        StackTraceTextParser parser = new StackTraceTextParser();
        StackTrace result = parser.parse("java.lang.NullPointerException\n"
                + "        at com.markatta.stackdetective.StackTrace.parseStackTrace(StackTrace.java:21)\n"
                + "        at com.markatta.stackdetective.StackTraceTest.testParseStackTrace(StackTraceTest.java:15)\n");

        DefaultDistanceCalculator instance = new DefaultDistanceCalculator(new FavourBeginStrategy());
        assertEquals(0, instance.calculateDistance(result, result));
    }

    @Test
    public void nonEqualHaveDifference() {
        StackTraceTextParser parser = new StackTraceTextParser();
        StackTrace trace1 = parser.parse("Exception in thread \"main\" java.lang.RuntimeException: Error running test app\n"
                + "	at com.markatta.stackdetective.TestApp.main(TestApp.java:61)\n"
                + "Caused by: java.lang.ArrayIndexOutOfBoundsException: 130\n"
                + "	at com.markatta.stackdetective.FavourBeginDistanceCalculator.calculateDistance(FavourBeginDistanceCalculator.java:38)\n"
                + "	at com.markatta.stackdetective.TestApp.main(TestApp.java:51)\n");
        StackTrace trace2 = parser.parse("java.lang.NullPointerException\n"
                + "        at com.markatta.stackdetective.StackTrace.parseStackTrace(StackTrace.java:21)\n"
                + "        at com.markatta.stackdetective.StackTraceTest.testParseStackTrace(StackTraceTest.java:15)\n");

        DefaultDistanceCalculator instance = new DefaultDistanceCalculator(new FavourBeginStrategy());
        int result = instance.calculateDistance(trace1, trace2);
        assertFalse("Expected result to be non-0 as the traces are different", 0 == result);

    }
}