package com.markatta.stackdetective.distance.misc;

import com.markatta.stackdetective.distance.DistanceAlgorithm;
import com.markatta.stackdetective.model.Entry;
import com.markatta.stackdetective.model.StackTrace;

public class SameCauseAlgorithm implements DistanceAlgorithm<StackTrace> {

	@Override
	public double calculateDistance(StackTrace a, StackTrace b) {
		// not sure why there is data like this, guard for now
		if (a.getCauseSegment().getEntries().isEmpty() || b.getCauseSegment().getEntries().isEmpty()) {
			return 0;
		}
		
		Entry firstEntryOfA = a.getCauseSegment().getEntries().get(0);
		Entry firstEntryOfB = b.getCauseSegment().getEntries().get(0);

		if (firstEntryOfA.getClassName().equals(firstEntryOfB.getClassName())
				&& firstEntryOfA.getMethodName().equals(firstEntryOfB.getMethodName())) {
			return 1;
		} else {
			return 0;
		}
	}

}
