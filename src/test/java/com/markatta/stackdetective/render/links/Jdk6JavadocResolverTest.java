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