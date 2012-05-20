package com.markatta.stackdetective.api

import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers

class ParserSpec extends Spec with ShouldMatchers {

  describe("A Parser") {

    it("should parse a simple stack trace") {
      val text = """java.rmi.NoSuchObjectException: no such object in table
  at org.jboss.tm.usertx.client.ClientUserTransaction.begin(ClientUserTransaction.java:124)
	at com.example.client.core.util.form.EJBPresentationModel.performFinal(EJBPresentationModel.java:160)"""

      val parser = StackTraceParser()
      val trace = parser.parse(text)

      trace.segments.size should be (1)
      val segment = trace.segments(0)
      segment.exception.exceptionType should be ("java.rmi.NoSuchObjectException")
      segment.exception.message should be ("no such object in table")
      segment.entries.size should be (2)
      val entry = segment.entries(0)
      entry.fqMethodName should be ("org.jboss.tm.usertx.client.ClientUserTransaction.begin")
      entry.location.file should be ("ClientUserTransaction.java")
      entry.location.line should be (124)
    }

    it("should parse a multipart stack trace with a caused part") {
      val text = """javax.ejb.EJBTransactionRolledbackException: $Proxy453 cannot be cast to com.example.product.ejb.donor.CustomerAdministrationRemote
	at org.jboss.ejb3.tx.Ejb3TxPolicy.handleInCallerTx(Ejb3TxPolicy.java:87)
	at org.jboss.aspects.tx.TxPolicy.invokeInCallerTx(TxPolicy.java:130)
	at org.jboss.aspects.tx.TxInterceptor$Required.invoke(TxInterceptor.java:195)
	at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)
	at org.jboss.ejb3.stateless.StatelessInstanceInterceptor.invoke(StatelessInstanceInterceptor.java:62)
	at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)
	at org.jboss.ejb3.mdb.MessagingContainer.localInvoke(MessagingContainer.java:249)
	at org.jboss.ejb3.mdb.inflow.MessageInflowLocalProxy.delivery(MessageInflowLocalProxy.java:268)
	at org.jboss.ejb3.mdb.inflow.MessageInflowLocalProxy.invoke(MessageInflowLocalProxy.java:138)
	at $Proxy558.execute(Unknown Source)
	at org.jboss.resource.adapter.quartz.inflow.QuartzJob.execute(QuartzJob.java:57)
	at org.quartz.core.JobRunShell.run(JobRunShell.java:203)
	at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:520)
Caused by: java.lang.ClassCastException: $Proxy453 cannot be cast to com.example.product.ejb.customer.CustomerAdministrationRemote
	at com.example.product.ejb.scheduled.UpdateCustomerBillStatusJob.execute(UpdateCustomerBillStatusJob.java:32)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
	at java.lang.reflect.Method.invoke(Method.java:597)
	at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:112)
	at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:166)
	at org.jboss.ejb3.interceptor.EJB3InterceptorsInterceptor.invoke(EJB3InterceptorsInterceptor.java:63)
	at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)
	at org.jboss.ejb3.entity.TransactionScopedEntityManagerInterceptor.invoke(TransactionScopedEntityManagerInterceptor.java:54)
	at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)
	at org.jboss.ejb3.AllowedOperationsInterceptor.invoke(AllowedOperationsInterceptor.java:47)
	at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)
	at org.jboss.aspects.tx.TxPolicy.invokeInCallerTx(TxPolicy.java:126)
	... 11 more"""

      val parser = StackTraceParser()
      val result = parser.parse(text)

      result.segments.size should be (2)
      val seg1 = result.segments(0)
      seg1.exception.exceptionType should be ("javax.ejb.EJBTransactionRolledbackException")
      seg1.entries(9).location should be (UnknownLocation)

      val seg2 = result.segments(1)
      seg2.exception.exceptionType should be ("java.lang.ClassCastException")
      seg2.exception.message should be ("$Proxy453 cannot be cast to com.example.product.ejb.customer.CustomerAdministrationRemote")
      seg2.truncatedLines should be (11)
    }

    it("should handle native source locations") {
      val text = """java.net.ConnectException: Connection refused: connect
      at java.net.PlainSocketImpl.socketConnect(Native Method)
      at java.net.PlainSocketImpl.doConnect(Unknown Source)
      at java.net.PlainSocketImpl.connectToAddress(Unknown Source)
      at java.net.PlainSocketImpl.connect(Unknown Source)
      at java.net.SocksSocketImpl.connect(Unknown Source)
      at java.net.Socket.connect(Unknown Source)
      at java.net.Socket.connect(Unknown Source)
      at java.net.Socket.<init>(Unknown Source)
        at java.net.Socket.<init>(Unknown Source)
          at client1.create_client(client1.java:34)
          at client1.main(client1.java:17)"""

      val parser = StackTraceParser()
      val result = parser.parse(text)

      result.segments.size should be (1)
      val seg1 = result.segments(0)
      seg1.entries.size should be (11)
      seg1.entries(0).location should be (NativeMethod)
      seg1.entries(7).methodName should be ("<init>")

    }

    it("should parse exception without message") {
      val text = """java.util.ConcurrentModificationException
	at java.util.TreeMap$PrivateEntryIterator.nextEntry(Unknown Source)
	at java.util.TreeMap$KeyIterator.next(Unknown Source)
	at java.util.AbstractCollection.toArray(Unknown Source)
	at java.util.ArrayList.<init>(Unknown Source)
	at com.example.client.core.program.tree.TreeFolder.getChildren(TreeFolder.java:53)
	at com.example.client.core.program.menu.tree.ui.FilteredTreeModel.getChildCount(FilteredTreeModel.java:87)
	at javax.swing.tree.VariableHeightLayoutCache$TreeStateNode.expand(Unknown Source)
	at javax.swing.tree.VariableHeightLayoutCache$TreeStateNode.expand(Unknown Source)
	at javax.swing.tree.VariableHeightLayoutCache.rebuild(Unknown Source)
	at javax.swing.tree.VariableHeightLayoutCache.setModel(Unknown Source)
	at javax.swing.plaf.basic.BasicTreeUI.setModel(Unknown Source)
	at javax.swing.plaf.basic.BasicTreeUI$Handler.propertyChange(Unknown Source)
	at java.beans.PropertyChangeSupport.firePropertyChange(Unknown Source)
	at java.beans.PropertyChangeSupport.firePropertyChange(Unknown Source)
	at java.awt.Component.firePropertyChange(Unknown Source)
	at javax.swing.JTree.setModel(Unknown Source)
	at com.example.client.core.program.menu.tree.ui.TreeTopComponent$ProgramTreeListener.treeWasReloaded(TreeTopComponent.java:709)
	at com.example.client.core.program.tree.SwingTreeListenerDecorator$2.run(SwingTreeListenerDecorator.java:55)
	at java.awt.event.InvocationEvent.dispatch(Unknown Source)
	at java.awt.EventQueue.dispatchEvent(Unknown Source)
	at org.netbeans.core.TimableEventQueue.dispatchEvent(TimableEventQueue.java:137)
	at java.awt.EventDispatchThread.pumpOneEventForFilters(Unknown Source)
	at java.awt.EventDispatchThread.pumpEventsForFilter(Unknown Source)
	at java.awt.EventDispatchThread.pumpEventsForHierarchy(Unknown Source)
	at java.awt.EventDispatchThread.pumpEvents(Unknown Source)
	at java.awt.EventDispatchThread.pumpEvents(Unknown Source)
	at java.awt.EventDispatchThread.run(Unknown Source)"""
      val parser = StackTraceParser()
      val result = parser.parse(text)

      result.segments.size should be (1)

    }

  }

  it("should parse exception with multiline message containing colon") {
    val text = """java.lang.IllegalStateException: All columns in a TableFormat:  se.databyran.prosang.client.core.tables.ColumnsTableFormat@1eefd18  must have unique identifier, that is violated by these columns:
	 column: se.databyran.prosang.client.donor.common.table.DonationColumnDecorator@17424c3 has the same identifier: .DonationColumnDecorator as: se.databyran.prosang.client.donor.common.table.DonationColumnDecorator@19999f
	 column: se.databyran.prosang.client.donor.common.table.DonationColumnDecorator@b4c4f6 has the same identifier: .DonationColumnDecorator as: se.databyran.prosang.client.donor.common.table.DonationColumnDecorator@17424c3
	at se.databyran.prosang.client.core.tables.TableModelBuilder.checkTableFormatForUniqueness(TableModelBuilder.java:122)
	at se.databyran.prosang.client.core.tables.TableModelBuilder.<init>(TableModelBuilder.java:106)
	at se.databyran.prosang.client.core.findergui.FinderDialog.updateResultTableFormat(FinderDialog.java:489)
	at se.databyran.prosang.client.core.findergui.FinderDialog.access$1400(FinderDialog.java:57)
	at se.databyran.prosang.client.core.findergui.FinderDialog$8.actionPerformed(FinderDialog.java:340)
	at javax.swing.JComboBox.fireActionEvent(Unknown Source)
	at javax.swing.JComboBox.setSelectedItem(Unknown Source)
	at se.databyran.prosang.client.core.util.gui.AbstractProSangComboBox.setSelectedItem(AbstractProSangComboBox.java:233)
	at javax.swing.JComboBox.setSelectedIndex(Unknown Source)
	at se.databyran.prosang.client.core.util.gui.AbstractProSangComboBox.setSelectedIndex(AbstractProSangComboBox.java:159)
	at javax.swing.plaf.basic.BasicComboPopup$Handler.mouseReleased(Unknown Source)
	at java.awt.AWTEventMulticaster.mouseReleased(Unknown Source)
	at java.awt.Component.processMouseEvent(Unknown Source)
	at javax.swing.JComponent.processMouseEvent(Unknown Source)"""

    val parser = StackTraceParser()
    val result = parser.parse(text)

    result.segments.size should be (1)
  }

}
