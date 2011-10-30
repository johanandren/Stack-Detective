package com.markatta.stackdetective.distance.misc;

import com.markatta.stackdetective.distance.DistanceAlgorithm;
import com.markatta.stackdetective.model.StackTrace;

public class NumberOfSegmentsAlgorithm implements DistanceAlgorithm<StackTrace> {
	
	@Override
	public double calculateDistance(StackTrace a, StackTrace b) {
		final int sizeA = a.getSegments().size();
		final int sizeB = b.getSegments().size();
		
		double largest = Math.max(sizeA, sizeB);
		double smallest = Math.min(sizeA, sizeB);
		
		return smallest/largest;
	}
}
