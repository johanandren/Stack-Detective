package com.markatta.stackdetective.distance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Combines multiple algorithm results with a weight
 */
public class WeightedAlgorithmCombination<T> implements DistanceAlgorithm<T> {

	private final List<DistanceAlgorithm<T>> algorithms = new ArrayList<DistanceAlgorithm<T>>();

	private final List<Double> weights = new ArrayList<Double>();

	private final ExecutorService executor = Executors.newCachedThreadPool();

	/**
	 * The sum of the weights must be 1 or else an exception is thrown when the
	 * calculation is performed
	 */
	public void addAlgorithm(double weight, DistanceAlgorithm<T> algorithm) {
		algorithms.add(algorithm);
		weights.add(weight);
	}

	@Override
	public double calculateDistance(T a, T b) {

		List<AlgorithmCallable> callables = new ArrayList<AlgorithmCallable>(algorithms.size());
		for (int i = 0; i < algorithms.size(); i++) {
			DistanceAlgorithm<T> algorithm = algorithms.get(i);
			double weight = weights.get(i);
			callables.add(new AlgorithmCallable(algorithm, a, b, weight));
		}
		double distance = 0.0;
		try {
			for (Future<Double> future : executor.invokeAll(callables)) {
				distance += future.get();

			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}

		return distance;
	}

	private class AlgorithmCallable implements Callable<Double> {

		private final DistanceAlgorithm<T> algorithm;

		private final T a;

		private final T b;

		private final double weight;

		public AlgorithmCallable(DistanceAlgorithm<T> algorithm, T a, T b, double weight) {
			super();
			this.algorithm = algorithm;
			this.a = a;
			this.b = b;
			this.weight = weight;
		}

		@Override
		public Double call() throws Exception {
			return algorithm.calculateDistance(a, b) * weight;
		}

	}

}
