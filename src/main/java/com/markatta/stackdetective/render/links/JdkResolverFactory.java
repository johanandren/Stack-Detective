package com.markatta.stackdetective.render.links;

/**
 * Can create resolvers for the various JDK versions.
 * 
 * @author johan
 */
public class JdkResolverFactory {

    public static final String JDK142_BASE_URL = "http://download.oracle.com/javase/1.4.2/docs/api/";

    public static final String JDK5_BASE_URL = "http://download.oracle.com/javase/1.5.0/docs/api/";

    public static final String JDK6_BASE_URL = "http://download.oracle.com/javase/6/docs/api/";

    public static final String JDK7_BASE_URL = "http://download.java.net/jdk7/docs/api/";

    /**
     * 
     * @param jdkJavadocUrl Base url to use for the links. Must end with "/" and point to the "api" directory
     *                      of the javadocs. Use one of the provided <code>JDKX_BASE_URL</code> constants or provide a local
     *                      url.
     */
    public static ClassLinkResolver create(String jdkJavadocUrl) {
        return new JavadocLinkResolver(jdkJavadocUrl, "java.", "javax.swing.");
    }
}
