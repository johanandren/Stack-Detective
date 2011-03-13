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
package com.markatta.stackdetective.model;

/**
 * Contains the same data as a java.lang.StackTraceElement
 * 
 * @author johan
 */
public final class Entry {

    private final int lineNumber;

    private final String className;

    private final String methodName;

    private final String fileName;

    private final String packageName;

    /**
     * @param methodName the name of the method, for example "theMethod". Without parameter list.
     * @param fullyQualifiedClassName The package and classname of the class
     * @param fileName The name of the java file where the class comes from
     * @param lineNumber The line the error occured on
     */
    public Entry(String methodName, String fullyQualifiedClassName, String fileName, int lineNumber) {
        this.lineNumber = lineNumber;
        this.methodName = methodName;
        this.fileName = fileName;

        int lastDot = fullyQualifiedClassName.lastIndexOf('.');
        if (lastDot == -1) {
            // default package
            this.packageName = "";
            this.className = fullyQualifiedClassName;
        } else {
            // split class and package name
            this.packageName = fullyQualifiedClassName.substring(0, lastDot);
            this.className = fullyQualifiedClassName.substring(lastDot + 1);
        }
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

    /**
     * @return The classname without package
     */
    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    /**
     * @return fully qualified class name containing both package and class name
     */
    public String getFqClassName() {
        if (packageName.length() == 0) {
            return className;
        } else {
            return packageName + "." + className;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entry other = (Entry) obj;
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
