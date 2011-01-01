package com.markatta.stackdetective.parse;

import com.markatta.stackdetective.SegmentEntry;
import com.markatta.stackdetective.StackTrace;
import com.markatta.stackdetective.TraceSegment;
import com.markatta.stackdetective.parse.StackTraceTextParser;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class StackTraceTextParserTest {

    @Test
    public void testParseSegmentEntry() {
        StackTraceTextParser instance = new StackTraceTextParser();
        SegmentEntry result = instance.parseSegmentEntry("       at javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:387)");

        assertNotNull(result);
        assertEquals(387, result.getLineNumber());
        assertEquals("fireActionPerformed", result.getMethodName());
        assertEquals("javax.swing.DefaultButtonModel", result.getClassName());
        assertEquals("DefaultButtonModel.java", result.getFileName());
    }

    @Test
    public void testParseTraceSegment() {
        StackTraceTextParser instance = new StackTraceTextParser();
        TraceSegment segment = instance.parseTraceSegment("  at org.netbeans.core.TimableEventQueue.dispatchEvent(TimableEventQueue.java:104)\n"
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
        StackTraceTextParser instance = new StackTraceTextParser();
        TraceSegment result = instance.parseTraceSegment("Exception in thread \"main\" java.lang.RuntimeException: Error running test app\n"
                + "	at com.markatta.stackdetective.TestApp.main(TestApp.java:61)\n");
        assertEquals(1, result.numberOfEntries());
    }

    @Test
    public void testParseYetAnotherSegment() {
        StackTraceTextParser instance = new StackTraceTextParser();
        TraceSegment result = instance.parseTraceSegment("Caused by: java.lang.ArrayIndexOutOfBoundsException: 130\n"
                + "	at com.markatta.stackdetective.FavourBeginDistanceCalculator.calculateDistance(FavourBeginDistanceCalculator.java:38)\n"
                + "	at com.markatta.stackdetective.TestApp.main(TestApp.java:51)\n");
        assertEquals(2, result.numberOfEntries());
        assertEquals("java.lang.ArrayIndexOutOfBoundsException", result.getExceptionType());
        assertEquals("130", result.getExceptionText());
    }

    @Test
    public void testParseStackTrace() {
        StackTraceTextParser instance = new StackTraceTextParser();
        StackTrace result = instance.parse("java.lang.NullPointerException\n"
                + "        at com.markatta.stackdetective.StackTrace.parseStackTrace(StackTrace.java:21)\n"
                + "        at com.markatta.stackdetective.StackTraceTest.testParseStackTrace(StackTraceTest.java:15)\n");
        assertEquals(1, result.numberOfSegments());
    }

    @Test
    public void testParseTwoSegmentStackTrace() {
        StackTraceTextParser instance = new StackTraceTextParser();
        StackTrace result = instance.parse("java.lang.NullPointerException\n"
                + "        at com.markatta.stackdetective.StackTrace.parseStackTrace(StackTrace.java:21)\n"
                + "        at com.markatta.stackdetective.StackTraceTest.testParseStackTrace(StackTraceTest.java:15)\n"
                + "Caused by: some.other.Exception: fake message"
                + "        at some.package.ClassA(ClassA:23)");
        assertEquals(2, result.numberOfSegments());
    }

    @Test
    public void testParseStackTraceFromLog4J() {
        StackTraceTextParser instance = new StackTraceTextParser();
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
        StackTraceTextParser instance = new StackTraceTextParser();
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
        StackTraceTextParser instance = new StackTraceTextParser();
        StackTrace result = instance.parse("Exception in thread \"main\" java.lang.RuntimeException: Error running test app\n"
                + "	at com.markatta.stackdetective.TestApp.main(TestApp.java:61)\n"
                + "[catch] at java.awt.EventQueue.dispatchEvent(EventQueue.java:635)\n"
                + "     at org.netbeans.core.TimableEventQueue.dispatchEvent(TimableEventQueue.java:104)"
                + "     at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:296)");

        assertEquals(1, result.numberOfSegments());
        assertEquals(1, result.flatten().size());
    }
}