package com.markatta.stackdetective.distance.misc;

import com.markatta.stackdetective.distance.DistanceAlgorithm;
import com.markatta.stackdetective.model.StackTrace;

public final class StackTraceTypeAlgorithm implements DistanceAlgorithm<StackTrace> {

	@Override
	public double calculateDistance(StackTrace a, StackTrace b) {
		
		String typeA = a.getCauseSegment().getExceptionType();
		String typeB = b.getCauseSegment().getExceptionType();
		
		// dead simple 0 if same type 1 if not
		return Math.abs(typeA.compareTo(typeB));
	}
	

}
