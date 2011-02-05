package com.markatta.stackdetective.distance;

import com.markatta.stackdetective.model.Entry;
import com.markatta.stackdetective.model.StackTrace;
import com.markatta.stackdetective.distance.cost.DistanceCostStrategy;
import java.util.List;

/**
 * Gotoh gap match. Pretty useless actually.
 * 
 * @author johan
 */
public class GapCalculator implements DistanceCalculator {

    private static final long GAP_OPEN = 1;

    private static final long GAP_WIDEN = 3;

    private static final long STARSTOP = 200;

    private DistanceCostStrategy strategy;

    public GapCalculator(DistanceCostStrategy strategy) {
        this.strategy = strategy;
    }

    public int calculateDistance(StackTrace xTrace, StackTrace yTrace) {
        return (int) calculateDistance(xTrace.flatten(), yTrace.flatten());

    }

    private long calculateDistance(List<Entry> x, List<Entry> y) {
        int m = x.size();
        int n = y.size();

        long g = GAP_OPEN;
        long h = GAP_WIDEN;

        long D[][] = new long[m][n];
        long I[][] = new long[m][n];
        long T[][] = new long[m][n];

        for (int i = 0; i < m; i++) {
            D[i][0] = I[i][0] = STARSTOP;
        }

        for (int i = 0; i < n; i++) {
            D[0][i] = I[0][i] = STARSTOP;
        }

        T[0][0] = 0;
        T[1][0] = T[0][1] = g;

        for (int i = 2; i < m; i++) {
            T[i][0] = T[i - 1][0] + h;
        }

        for (int i = 2; i < n; i++) {
            T[0][i] = T[0][i - 1] + h;
        }

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                D[i][j] = Math.min(D[i - 1][j] + h, T[i - 1][j] + g);
                I[i][j] = Math.min(I[i][j - 1] + h, T[i][j - 1] + g);
                T[i][j] = Math.min(T[i - 1][j - 1] + strategy.substitute(x, i, y, j),
                        Math.min(D[i][j], I[i][j]));
            }
        }

        return T[m - 1][n - 1];
    }
}
