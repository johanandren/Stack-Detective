package com.markatta.stackdetective.distance;

import com.markatta.stackdetective.distance.cost.IntelligentSubstitutionStrategy;
import com.markatta.stackdetective.model.Entry;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class LevehnsteinDistanceCalculationTest {

    @Test
    public void backtrackWithOneIdenticalElement() {
        List<Entry> entries = new ArrayList<Entry>();
        Entry entry = new Entry("doSomething", "a.b.TheClass", "TheClass.java", 23);
        entries.add(entry);

        LevehnsteinDistanceCalculation instance = new LevehnsteinDistanceCalculation(entries, entries, new IntelligentSubstitutionStrategy());
        instance.calculateDistance();
        List<BackTrackElement> result = instance.getBackTrack();
        assertEquals(1, result.size());
        BackTrackElement backTrackElement = result.get(0);
        assertEquals(Operation.SUBSTITUTE, backTrackElement.getOperation());
        assertEquals(0, backTrackElement.getCost());
    }
    
     @Test
    public void backtrackWithTwoIdenticalElements() {
        List<Entry> entries = new ArrayList<Entry>();
        Entry entry1 = new Entry("doSomething", "a.b.TheClass", "TheClass.java", 23);
        entries.add(entry1);

        Entry entry2 = new Entry("doSomethingElse", "a.b..c.AnotherClass", "AnotherClass.java", 12);
        entries.add(entry2);

        LevehnsteinDistanceCalculation instance = new LevehnsteinDistanceCalculation(entries, entries, new IntelligentSubstitutionStrategy());
        instance.calculateDistance();
        
        List<BackTrackElement> result = instance.getBackTrack();
        assertEquals(2, result.size());
        
        BackTrackElement backTrackElement1 = result.get(0);
        assertEquals(Operation.SUBSTITUTE, backTrackElement1.getOperation());
        assertEquals(0, backTrackElement1.getCost());
        
        BackTrackElement backTrackElement2 = result.get(1);
        assertEquals(Operation.SUBSTITUTE, backTrackElement2.getOperation());
        assertEquals(0, backTrackElement2.getCost());
    }
}
