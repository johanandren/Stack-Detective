package com.markatta.stackdetective.distance;

import static org.junit.Assert.*;

import org.junit.Test;

public class WeightedAlgorithmCombinationTest {

	@Test
	public void worksWithOnlyOneAlgorithm() {
		WeightedAlgorithmCombination<Object> instance = new WeightedAlgorithmCombination<Object>();
		instance.addAlgorithm(1, oneDistanceCalculator);
		
		assertEquals(1, instance.calculateDistance(new Object(), new Object()), 0.000000001);
	}
	
	@Test
	public void worksWithMultipleAlgorithmsAndWeights() {
		WeightedAlgorithmCombination<Object> instance = new WeightedAlgorithmCombination<Object>();
		instance.addAlgorithm(0.5, oneDistanceCalculator);
		instance.addAlgorithm(0.5, oneDistanceCalculator);
		
		assertEquals(1, instance.calculateDistance(new Object(), new Object()), 0.000000001);
	}
	
	private final DistanceAlgorithm<Object> oneDistanceCalculator = new DistanceAlgorithm<Object>() {
		
		@Override
		public double calculateDistance(Object a, Object b) {
			return 1;
		}
	};
}
