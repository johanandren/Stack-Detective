package com.markatta.stackdetective.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.markatta.stackdetective.clustering.DistanceMatrix;
import com.markatta.stackdetective.distance.DistanceAlgorithm;
import com.markatta.stackdetective.distance.StacktraceDistanceCalculatorFactory;
import com.markatta.stackdetective.model.StackTrace;

public class MeasureSuccess {

	public static void main(String[] args) throws Exception {

		final File clustersDir = new File(args[0]);

		final ExecutorService executor = Executors.newCachedThreadPool();
		
		System.out.print("Parsing files...");
		long beforeFiles = System.currentTimeMillis();

		// load and parse each trace
		Map<String, List<StackTrace>> tracesPerCluster = new HashMap<String, List<StackTrace>>();

		Collection<ClusterLoader> loaders = new ArrayList<ClusterLoader>(); 
		for (final File clusterDir : clustersDir.listFiles()) {
			loaders.add(new ClusterLoader(clusterDir));
		}
		for(Future<ClusterLoader> future: executor.invokeAll(loaders)) {
			ClusterLoader loader = future.get();
			tracesPerCluster.put(loader.clusterDir.getName(), loader.stackTraces);
		}
		System.out.println("");
		
		System.out.print("Generating distance matrix...");
		// add each trace to the distance matrix
		DistanceAlgorithm<StackTrace> calculator = new StacktraceDistanceCalculatorFactory().createDefaultCalculator();

		DistanceMatrix<StackTrace> distanceMatrix = new DistanceMatrix<StackTrace>(calculator);
		for (Map.Entry<String, List<StackTrace>> entry : tracesPerCluster.entrySet()) {
			System.out.print(".");
			for (StackTrace trace : entry.getValue()) {
				distanceMatrix.add(trace);
			}
		}
		System.out.println(".");
		
		System.out.println("Parsed and compared files in " + (System.currentTimeMillis() - beforeFiles) + " ms");

		while (true) {

			long start = System.currentTimeMillis();
			
			System.out.print("Enter limit (0.0->1.0): ");
			double limit = Double.parseDouble(System.console().readLine());
			
			// figure out how many over limit in the same cluster
			int missedSimiliar = 0;
			int matchedSimiliar = 0;
			int falsePositives = 0;

			// distances within clusters
			for (Map.Entry<String, List<StackTrace>> entry : tracesPerCluster.entrySet()) {
				for (StackTrace from : entry.getValue()) {
					// within cluster
					for (StackTrace to : entry.getValue()) {
						if (from != to) {

							if (distanceMatrix.getDistanceBetween(from, to) > limit) {
								// the limit would have excluded it from the
								// cluster
								missedSimiliar++;
							} else {
								// the limit would have included it in the
								// cluster
								matchedSimiliar++;
							}
						}
					}
				}

				// to traces in other clusters
				for (Map.Entry<String, List<StackTrace>> other : tracesPerCluster.entrySet()) {
					if (other.getKey() != entry.getKey()) {
						for (StackTrace from : entry.getValue()) {
							for (StackTrace to : entry.getValue()) {
								if (from != to) {
									if (distanceMatrix.getDistanceBetween(from, to) < limit) {
										// the limit would have excluded it from
										// the
										// cluster
										falsePositives++;
									}
								}
							}

						}
					}
				}
			}

			// figure out how many false positives
			long end = System.currentTimeMillis();
			long spent = end - start;
			System.out.println("Missed " + missedSimiliar + ", matched " + matchedSimiliar + ", false positives=" + falsePositives + ", in=" + spent + " ms");
		}
	}

	private static class ClusterLoader implements Callable<ClusterLoader> {
		private final File clusterDir;

		private List<StackTrace> stackTraces;

		public ClusterLoader(File clusterDir) {
			this.clusterDir = clusterDir;
		}

		@Override
		public ClusterLoader call() throws Exception {
			System.out.print(".");
			File[] files = clusterDir.listFiles();
			stackTraces = FileParser.parseOnePerFile(files);
			return this;
		}

	}
}
