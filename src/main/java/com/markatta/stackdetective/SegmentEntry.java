package com.markatta.stackdetective;

/**
 * Contains the same data as a java.lang.StackTraceElement
 * 
 * @author johan
 */
public class SegmentEntry {

    private final int lineNumber;

    private final String className;

    private final String methodName;

    private final String fileName;

    public SegmentEntry(String methodName, String className, String fileName, int lineNumber) {
        this.lineNumber = lineNumber;
        this.methodName = methodName;
        this.className = className;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SegmentEntry other = (SegmentEntry) obj;
        if (this.lineNumber != other.lineNumber) {
            return false;
        }
        if ((this.className == null) ? (other.className != null) : !this.className.equals(other.className)) {
            return false;
        }
        if ((this.methodName == null) ? (other.methodName != null) : !this.methodName.equals(other.methodName)) {
            return false;
        }
        if ((this.fileName == null) ? (other.fileName != null) : !this.fileName.equals(other.fileName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.lineNumber;
        hash = 79 * hash + (this.className != null ? this.className.hashCode() : 0);
        hash = 79 * hash + (this.methodName != null ? this.methodName.hashCode() : 0);
        hash = 79 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("       ");
        builder.append(className);
        builder.append(".");
        builder.append(methodName);
        builder.append("(");
        builder.append(fileName);
        builder.append(":");
        builder.append(lineNumber);
        builder.append(")");

        return builder.toString();
    }
}
