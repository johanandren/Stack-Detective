package com.markatta.stackdetective.distance.misc;

import org.w3c.dom.Text;

import com.markatta.stackdetective.distance.DistanceAlgorithm;
import com.markatta.stackdetective.model.StackTrace;

public class RootTextComparisonAlgorithm implements DistanceAlgorithm<StackTrace> {

	@Override
	public double calculateDistance(StackTrace a, StackTrace b) {
		final String textA = a.getCauseSegment().getExceptionText();
		final String textB = b.getCauseSegment().getExceptionText();

		if (textA.equals(textB)) {
			return 1.0;
		} else {
			
			int longestCommon = longestCommonSubstring(textA, textB).length();
			if (longestCommon == 0)
				return 0.0;
			
			// more than two thirds? call it a match
			if (textA.length() / longestCommon  > 0.66) {
				return 1.0;
			} else {
				return 0.0;
			}
		}
	}

	/**
	 * Shamelessly stolen from rosetta code
	 * (http://rosettacode.org/wiki/Longest_common_subsequence#Java)
	 */
	public static String longestCommonSubstring(String a, String b) {
		int[][] lengths = new int[a.length() + 1][b.length() + 1];

		// row 0 and column 0 are initialized to 0 already

		for (int i = 0; i < a.length(); i++)
			for (int j = 0; j < b.length(); j++)
				if (a.charAt(i) == b.charAt(j))
					lengths[i + 1][j + 1] = lengths[i][j] + 1;
				else
					lengths[i + 1][j + 1] = Math.max(lengths[i + 1][j], lengths[i][j + 1]);

		// read the substring out from the matrix
		StringBuffer sb = new StringBuffer();
		for (int x = a.length(), y = b.length(); x != 0 && y != 0;) {
			if (lengths[x][y] == lengths[x - 1][y])
				x--;
			else if (lengths[x][y] == lengths[x][y - 1])
				y--;
			else {
				assert a.charAt(x - 1) == b.charAt(y - 1);
				sb.append(a.charAt(x - 1));
				x--;
				y--;
			}
		}

		return sb.reverse().toString();
	}

}
