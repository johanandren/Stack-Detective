package com.markatta.stackdetective.util;

import com.markatta.stackdetective.distance.BackTrackElement;
import com.markatta.stackdetective.distance.DefaultDistanceCalculator;
import com.markatta.stackdetective.distance.cost.IntelligentSubstitutionStrategy;
import com.markatta.stackdetective.model.Entry;
import com.markatta.stackdetective.model.StackTrace;
import java.util.List;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author johan
 */
public class PrintDistanceBackTrack {

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        Logger.getLogger("com.markatta.stackdetective.distance.LevehnsteinDistanceCalculation").setLevel(Level.TRACE);
        
        if (args.length != 2) {
            System.out.println("Usage: RenderAsText testFile1 testFile2");
            System.out.println(" where the files contains one stacktrace each");
            return;
        }

        List<StackTrace> stackTraces = FileParser.parseOnePerFile(args);
        StackTrace stackTraceA = stackTraces.get(0);
        StackTrace stackTraceB = stackTraces.get(1);

        DefaultDistanceCalculator calculator = new DefaultDistanceCalculator(new IntelligentSubstitutionStrategy());
        List<BackTrackElement> backtrack = calculator.getDistanceBacktrack(stackTraceA, stackTraceB);

        List<Entry> entriesFromA = stackTraceA.getCauseSegment().getEntries();
        List<Entry> entriesFromB = stackTraceB.getCauseSegment().getEntries();
        
        for (BackTrackElement backTrackElement : backtrack) {
            switch (backTrackElement.getOperation()) {
                case DELETE:
                    System.out.println("DEL: " + entriesFromA.get(backTrackElement.getI()));
                    break;

                case INSERT:
                    System.out.println("INS: " + entriesFromB.get(backTrackElement.getJ()));
                    break;

                case SUBSTITUTE:
                    System.out.println("SUB: " + entriesFromA.get(backTrackElement.getI()) + " => " + entriesFromB.get(backTrackElement.getJ()));
                    break;

                case NONE:
                    break;
            }
        }

    }
}
