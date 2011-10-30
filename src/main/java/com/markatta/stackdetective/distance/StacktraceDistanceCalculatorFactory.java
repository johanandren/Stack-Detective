package com.markatta.stackdetective.distance;

import com.markatta.stackdetective.distance.levehnstein.WeightedLevehnsteinDistance;
import com.markatta.stackdetective.distance.misc.NumberOfSegmentsAlgorithm;
import com.markatta.stackdetective.distance.misc.RootTextComparisonAlgorithm;
import com.markatta.stackdetective.distance.misc.SameCauseAlgorithm;
import com.markatta.stackdetective.distance.misc.StackTraceTypeAlgorithm;
import com.markatta.stackdetective.model.StackTrace;

public class StacktraceDistanceCalculatorFactory {

	/**
	 * @return A sensible default algorithm to use
	 */
	public DistanceAlgorithm<StackTrace> createDefaultCalculator() {
		WeightedAlgorithmCombination<StackTrace> combination = new WeightedAlgorithmCombination<StackTrace>();
		combination.addAlgorithm(0.80, new WeightedLevehnsteinDistance());
		combination.addAlgorithm(0.05, new StackTraceTypeAlgorithm());
		combination.addAlgorithm(0.05, new NumberOfSegmentsAlgorithm());
		combination.addAlgorithm(0.05, new RootTextComparisonAlgorithm());
		combination.addAlgorithm(0.05, new SameCauseAlgorithm());
		
		return combination;
	}
	
}
