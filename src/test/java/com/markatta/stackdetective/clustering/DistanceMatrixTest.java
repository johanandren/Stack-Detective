/**
 * Copyright (C) 2011 Johan Andren <johan@markatta.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.markatta.stackdetective.clustering;

import static org.junit.Assert.*;

import org.junit.Test;

import com.markatta.stackdetective.distance.DistanceAlgorithm;

public class DistanceMatrixTest {

	@Test
	public void afterAddingTwoEntriesEveryPossibleCombinationHasADistance() {
		DistanceMatrix<String> instance = new DistanceMatrix<String>(new DistanceAlgorithm<String>() {

			private double distance = 1;

			@Override
			public double calculateDistance(String a, String b) {
				return distance;
			}
		});

		String a = "A";
		String b = "B";

		instance.add(a);
		instance.add(b);

		assertEquals(1, instance.getDistanceBetween(a, a), 0.0001);
		assertEquals(1, instance.getDistanceBetween(b, b), 0.0001);
		assertEquals(1, instance.getDistanceBetween(a, b), 0.0001);
		assertEquals(1, instance.getDistanceBetween(b, a), 0.0001);
	}
}