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
package com.markatta.stackdetective.parse;

import org.junit.Before;
import com.markatta.stackdetective.model.Entry;
import com.markatta.stackdetective.model.StackTrace;
import com.markatta.stackdetective.model.Segment;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class NaiveTextParserTest {

    @Test
    public void testParseSegmentEntry() {
        NaiveTextParser instance = new NaiveTextParser();
        Entry result = instance.parseSegmentEntry("       at javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:387)");

        assertNotNull(result);
        assertEquals(387, result.getLineNumber());
        assertEquals("fireActionPerformed", result.getMethodName());
        assertEquals("javax.swing.DefaultButtonModel", result.getFqClassName());
        assertEquals("DefaultButtonModel.java", result.getFileName());
    }

    @Test
    public void parseSegmentEntryWithUnknownSources() {
        NaiveTextParser instance = new NaiveTextParser();
        Entry result = instance.parseSegmentEntry("       at javax.swing.DefaultButtonModel.fireActionPerformed(Unknown Source)");

        assertNotNull(result);
        assertEquals(-1, result.getLineNumber());
        assertEquals("fireActionPerformed", result.getMethodName());
        assertEquals("javax.swing.DefaultButtonModel", result.getFqClassName());
        assertEquals("Unknown Source", result.getFileName());
    }

    @Test
    public void parseSegmentEntryWithRandomFileEntry() {
        NaiveTextParser instance = new NaiveTextParser();
        Entry result = instance.parseSegmentEntry("       at javax.swing.DefaultButtonModel.fireActionPerformed(Random)");

        assertNotNull(result);
        assertEquals(-1, result.getLineNumber());
        assertEquals("fireActionPerformed", result.getMethodName());
        assertEquals("javax.swing.DefaultButtonModel", result.getFqClassName());
        assertEquals("Random", result.getFileName());
    }

    @Test
    public void parseSegmentEntryWithNativeMethod() {
        NaiveTextParser instance = new NaiveTextParser();
        Entry result = instance.parseSegmentEntry("       at javax.swing.DefaultButtonModel.fireActionPerformed(Native Method)");

        assertNotNull(result);
        assertEquals(-1, result.getLineNumber());
        assertEquals("fireActionPerformed", result.getMethodName());
        assertEquals("javax.swing.DefaultButtonModel", result.getFqClassName());
        assertEquals("Native Method", result.getFileName());
    }

    @Test
    public void testParseTraceSegment() {
        NaiveTextParser instance = new NaiveTextParser();
        Segment segment = instance.parseTraceSegment("  at org.netbeans.core.TimableEventQueue.dispatchEvent(TimableEventQueue.java:104)\n"
                + "       at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:296)\n"
                + "       at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:211)\n"
                + "       at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:201)\n"
                + "       at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:196)\n"
                + "       at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:188)\n"
                + "       at java.awt.EventDispatchThread.run(EventDispatchThread.java:122)\n");
        assertEquals(7, segment.numberOfEntries());
    }

    
    
    @Test
    public void testParseAnotherSegment() {
        NaiveTextParser instance = new NaiveTextParser();
        Segment result = instance.parseTraceSegment("Exception in thread \"main\" java.lang.RuntimeException: Error running test app\n"
                + "	at com.markatta.stackdetective.TestApp.main(TestApp.java:61)\n");
        assertEquals(1, result.numberOfEntries());
    }

    @Test
    public void testParseYetAnotherSegment() {
        NaiveTextParser instance = new NaiveTextParser();
        Segment result = instance.parseTraceSegment("Caused by: java.lang.ArrayIndexOutOfBoundsException: 130\n"
                + "	at com.markatta.stackdetective.FavourBeginDistanceCalculator.calculateDistance(FavourBeginDistanceCalculator.java:38)\n"
                + "	at com.markatta.stackdetective.TestApp.main(TestApp.java:51)\n");
        assertEquals(2, result.numberOfEntries());
        assertEquals("java.lang.ArrayIndexOutOfBoundsException", result.getExceptionType());
        assertEquals("130", result.getExceptionText());
    }

    @Test
    public void testParseStackTrace() {
        NaiveTextParser instance = new NaiveTextParser();
        StackTrace result = instance.parse("java.lang.NullPointerException\n"
                + "        at com.markatta.stackdetective.StackTrace.parseStackTrace(StackTrace.java:21)\n"
                + "        at com.markatta.stackdetective.StackTraceTest.testParseStackTrace(StackTraceTest.java:15)\n");
        assertEquals(1, result.numberOfSegments());
    }

    @Test
    public void testParseTwoSegmentStackTrace() {
        NaiveTextParser instance = new NaiveTextParser();
        StackTrace result = instance.parse("java.lang.NullPointerException\n"
                + "        at com.markatta.stackdetective.StackTrace.parseStackTrace(StackTrace.java:21)\n"
                + "        at com.markatta.stackdetective.StackTraceTest.testParseStackTrace(StackTraceTest.java:15)\n"
                + "Caused by: some.other.Exception: fake message"
                + "        at some.package.ClassA(ClassA:23)");
        assertEquals(2, result.numberOfSegments());
    }

    @Test
    public void testParseStackTraceFromLog4J() {
        NaiveTextParser instance = new NaiveTextParser();
        StackTrace result = instance.parse("2010-12-16 00:00:00,327 ERROR [a.b.exceptions.ServerException] Failed to lookup interface a.b.ServiceRemote\n"
                + "javax.naming.CommunicationException [Root exception is java.lang.ClassNotFoundException: No ClassLoaders found for: a.b.ServiceRemote (no security manager: RMI class loader disabled)]\n"
                + "       at org.jnp.interfaces.NamingContext.lookup(NamingContext.java:786)\n"
                + "       at org.jnp.interfaces.NamingContext.lookup(NamingContext.java:627)\n"
                + "       at javax.naming.InitialContext.lookup(InitialContext.java:392)\n"
                + "       at se.databyran.prosang.ejb.scheduled.AuthenticatedEJBLocator.lookup(AuthenticatedEJBLocator.java:80)\n"
                + "       at se.databyran.prosang.ejb.scheduled.UpdateDonorQuarantineStatusJob.execute(UpdateDonorQuarantineStatusJob.java:21)\n"
                + "       at org.quartz.core.JobRunShell.run(JobRunShell.java:203)\n"
                + "       at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:520)\n"
                + "Caused by: java.lang.ClassNotFoundException: No ClassLoaders found for: se.databyran.prosang.ejb.donor.DonorAdministrationRemote (no security manager: RMI class loader disabled)\n"
                + "       at sun.rmi.server.LoaderHandler.loadProxyClass(LoaderHandler.java:535)\n"
                + "       at java.rmi.server.RMIClassLoader$2.loadProxyClass(RMIClassLoader.java:628)\n"
                + "       at org.jboss.system.JBossRMIClassLoader.loadProxyClass(JBossRMIClassLoader.java:82)\n"
                + "       at java.rmi.server.RMIClassLoader.loadProxyClass(RMIClassLoader.java:294)\n"
                + "       at sun.rmi.server.MarshalInputStream.resolveProxyClass(MarshalInputStream.java:238)\n"
                + "       at java.io.ObjectInputStream.readProxyDesc(ObjectInputStream.java:1531)\n"
                + "       at java.io.ObjectInputStream.readClassDesc(ObjectInputStream.java:1493)\n"
                + "       at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:1732)\n"
                + "       at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1329)\n"
                + "       at java.io.ObjectInputStream.readObject(ObjectInputStream.java:351)\n"
                + "       at java.rmi.MarshalledObject.get(MarshalledObject.java:142)\n"
                + "       at org.jnp.interfaces.MarshalledValuePair.get(MarshalledValuePair.java:72)\n"
                + "       at org.jnp.interfaces.NamingContext.lookup(NamingContext.java:710)\n"
                + "       ... 6 more");


        assertEquals(2, result.numberOfSegments());

    }

    @Test
    public void testParseAnotherStackTrace() {
        NaiveTextParser instance = new NaiveTextParser();
        StackTrace result = instance.parse("Exception in thread \"main\" java.lang.RuntimeException: Error running test app\n"
                + "	at com.markatta.stackdetective.TestApp.main(TestApp.java:61)\n"
                + "Caused by: java.lang.ArrayIndexOutOfBoundsException: 130\n"
                + "	at com.markatta.stackdetective.FavourBeginDistanceCalculator.calculateDistance(FavourBeginDistanceCalculator.java:38)\n"
                + "	at com.markatta.stackdetective.TestApp.main(TestApp.java:51)\n");
        assertEquals(2, result.numberOfSegments());
        assertEquals(3, result.flatten().size());
    }

    @Test
    public void catchMakesNextPartIgnored() {
        NaiveTextParser instance = new NaiveTextParser();
        StackTrace result = instance.parse("Exception in thread \"main\" java.lang.RuntimeException: Error running test app\n"
                + "	at com.markatta.stackdetective.TestApp.main(TestApp.java:61)\n"
                + "[catch] at java.awt.EventQueue.dispatchEvent(EventQueue.java:635)\n"
                + "     at org.netbeans.core.TimableEventQueue.dispatchEvent(TimableEventQueue.java:104)\n"
                + "     at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:296)");

        assertEquals(1, result.numberOfSegments());
        assertEquals(4, result.flatten().size());
    }

    @Test
    public void yetAnotherStackTraceIsParseable() {
        NaiveTextParser instance = new NaiveTextParser();
        StackTrace result = instance.parse("se.daby.SomeOtherException: Does not work at all\n  at c.d.e.YadaYada(YadaYada.java:23)\n   at f.k.YipYip(YipYip.java:45)");

        assertEquals(1, result.numberOfSegments());
        assertEquals(2, result.flatten().size());

    }

    @Test
    public void andYetAnotherStackTrace() {
        NaiveTextParser instance = new NaiveTextParser();
        StackTrace result = instance.parse("java.lang.NullPointerException\n"
                + "	at se.databyran.prosang.client.core.dynamicsearch.shell.SearchShellPresentationModel.setCurrentSearchProfile(SearchShellPresentationModel.java:349)\n"
                + "	at se.databyran.prosang.client.core.dynamicsearch.shell.SearchShellTopComponent.loadSearchProfile(SearchShellTopComponent.java:490)\n"
                + "	at se.databyran.prosang.client.core.dynamicsearch.shell.SearchShellTopComponent.loadButtonActionPerformed(SearchShellTopComponent.java:692)\n"
                + "	at se.databyran.prosang.client.core.dynamicsearch.shell.SearchShellTopComponent.access$1100(SearchShellTopComponent.java:49)\n"
                + "	at se.databyran.prosang.client.core.dynamicsearch.shell.SearchShellTopComponent$8.actionPerformed(SearchShellTopComponent.java:598)\n"
                + "	at javax.swing.AbstractButton.fireActionPerformed(Unknown Source)\n"
                + "	at javax.swing.AbstractButton$Handler.actionPerformed(Unknown Source)\n"
                + "	at javax.swing.DefaultButtonModel.fireActionPerformed(Unknown Source)\n"
                + "	at javax.swing.DefaultButtonModel.setPressed(Unknown Source)\n"
                + "	at javax.swing.plaf.basic.BasicButtonListener.mouseReleased(Unknown Source)\n"
                + "	at java.awt.AWTEventMulticaster.mouseReleased(Unknown Source)\n"
                + "	at java.awt.Component.processMouseEvent(Unknown Source)\n"
                + "	at javax.swing.JComponent.processMouseEvent(Unknown Source)\n"
                + "	at java.awt.Component.processEvent(Unknown Source)\n"
                + "	at java.awt.Container.processEvent(Unknown Source)\n"
                + "	at java.awt.Component.dispatchEventImpl(Unknown Source)\n"
                + "	at java.awt.Container.dispatchEventImpl(Unknown Source)\n"
                + "	at java.awt.Component.dispatchEvent(Unknown Source)\n"
                + "	at java.awt.LightweightDispatcher.retargetMouseEvent(Unknown Source)\n"
                + "	at java.awt.LightweightDispatcher.processMouseEvent(Unknown Source)\n"
                + "	at java.awt.LightweightDispatcher.dispatchEvent(Unknown Source)\n"
                + "	at java.awt.Container.dispatchEventImpl(Unknown Source)\n"
                + "	at java.awt.Window.dispatchEventImpl(Unknown Source)\n"
                + "	at java.awt.Component.dispatchEvent(Unknown Source)\n"
                + "	at java.awt.EventQueue.dispatchEvent(Unknown Source)\n"
                + "	at org.netbeans.core.TimableEventQueue.dispatchEvent(TimableEventQueue.java:137)\n"
                + "	at java.awt.EventDispatchThread.pumpOneEventForFilters(Unknown Source)\n"
                + "	at java.awt.EventDispatchThread.pumpEventsForFilter(Unknown Source)\n"
                + "	at java.awt.EventDispatchThread.pumpEventsForHierarchy(Unknown Source)\n"
                + "	at java.awt.EventDispatchThread.pumpEvents(Unknown Source)\n"
                + "	at java.awt.EventDispatchThread.pumpEvents(Unknown Source)\n"
                + "	at java.awt.EventDispatchThread.run(Unknown Source)\n");
        assertEquals(1, result.getSegments().size());
        assertEquals(32, result.flatten().size());
        assertNotNull(result.getCauseSegment());
        assertEquals("java.lang.NullPointerException", result.getRootExceptionType());

    }

    @Test
    public void handlesUnknownSource() {
        NaiveTextParser instance = new NaiveTextParser();
        StackTrace result = instance.parse("se.daby.SomeOtherException: Does not work at all\n  at c.d.e.YadaYada(Unknown Source)\n   at f.k.YipYip(YipYip.java:45)");

        assertEquals(1, result.numberOfSegments());
        assertEquals(2, result.flatten().size());
    }

    @Test
    public void handlesNativeSource() {
        NaiveTextParser instance = new NaiveTextParser();
        StackTrace result = instance.parse("se.daby.SomeOtherException: Does not work at all\n  at c.d.e.YadaYada(Native Method)\n   at f.k.YipYip(YipYip.java:45)");

        assertEquals(1, result.numberOfSegments());
        assertEquals(2, result.flatten().size());
    }
}
