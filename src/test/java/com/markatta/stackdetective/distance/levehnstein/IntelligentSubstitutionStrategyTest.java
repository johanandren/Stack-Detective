package com.markatta.stackdetective.distance.levehnstein;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.markatta.stackdetective.distance.levehnstein.IntelligentSubstitutionStrategy;
import com.markatta.stackdetective.model.Entry;

public class IntelligentSubstitutionStrategyTest {

	private final IntelligentSubstitutionStrategy instance = new IntelligentSubstitutionStrategy();

	private final List<Entry> entries = createEntries();
	
	@Test
	public void costOfAdd() {
		double costForAdd = instance.add(entries, 0);
		assertTrue(costForAdd > 0);
		
	}

	@Test
	public void costOfSubstitueTheSameRow() {
		double costForSame = instance.substitute(entries, 2, entries, 2);
		assertEquals(0, costForSame, 0.00001);
	}

	private List<Entry> createEntries() {
		Entry entry1 = new Entry("theMethod", "a.b.c.TheClass", "TheClass.java", 25);
		Entry entry2 = new Entry("anotherMethod", "a.b.c.AnotherClass", "AnotherClass.java", 16);
		Entry entry3 = new Entry("yetAnotherMethod", "a.b.c.TheClass", "TheClass.java", 125);
		return Arrays.asList(entry1, entry2, entry3);
	}

}
