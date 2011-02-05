
package com.markatta.stackdetective.render.links;

import com.markatta.stackdetective.model.Entry;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class Jdk6JavadocResolverTest {

   
    @Test
    public void testGetLinkFor() {
        ClassLinkResolver instance = JdkResolverFactory.create(JdkResolverFactory.JDK6_BASE_URL);
        String result = instance.getURLFor(new Entry("getApplet()", "java.applet.AppletContext", "AppletContext.java", 1));
        assertEquals("http://download.oracle.com/javase/6/docs/api/java/applet/AppletContext.html", result);
    }

}