package com.markatta.stackdetective.distance;

import java.io.Serializable;

/**
 * Represents one operation in the distane calculation
 * 
 * @author johan
 */
public final class BackTrackElement implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Operation operation;

    private final int cost;

    private final int i;

    private final int j;

    /**
     * 
     * @param operation The operation that was performed
     * @param cost The cost of the operation
     * @param i The position in the source entry list
     * @param j The position in the target entry list
     */
    public BackTrackElement(Operation operation, int cost, int i, int j) {
        this.operation = operation;
        this.cost = cost;
        this.i = i;
        this.j = j;
    }

    public int getCost() {
        return cost;
    }

    public Operation getOperation() {
        return operation;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    @Override
    public String toString() {
        return "BackTrackElement{" + "operation=" + operation + ", cost=" + cost + '}';
    }
}
