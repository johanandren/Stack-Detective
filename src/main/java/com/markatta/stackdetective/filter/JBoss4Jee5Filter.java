package com.markatta.stackdetective.filter;

/**
 * Prefilled with packages that can be ignored for JBoss 4 JEE5 applications.
 *
 * @author johan
 */
public final class JBoss4Jee5Filter extends PackagePrefixFilter {

    public JBoss4Jee5Filter() {
        super(new String[]{
                    "java.lang.reflect",
                    "sun.reflect",
                    "org.jboss.aspects.tx",
                    "org.jboss.ejb3",
                    "org.jboss.aop",
                    "$Proxy"
                });
    }
}
