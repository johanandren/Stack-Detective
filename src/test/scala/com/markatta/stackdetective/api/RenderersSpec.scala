package com.markatta.stackdetective.api

import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers

class RenderersSpec extends Spec with ShouldMatchers {

  describe("A TextRenderer") {

    it("renders stacktraces the same way they looked before parsing") {
      val text = """java.rmi.NoSuchObjectException: no such object in table
  at org.jboss.tm.usertx.client.ClientUserTransaction.begin(ClientUserTransaction.java:124)
  at com.example.client.core.util.form.EJBPresentationModel.performFinal(EJBPresentationModel.java:160)"""
      val trace = StackTraceParser().parse(text)

      val result = Renderers.textRenderer(Filters.NoFilter)(trace)

      result should equal (text)
    }

    it("filters stacktraces with the given filters") {
      val text = """java.rmi.NoSuchObjectException: no such object in table
  at org.jboss.tm.usertx.client.ClientUserTransaction.begin(ClientUserTransaction.java:124)
  at com.example.client.core.util.form.EJBPresentationModel.performFinal(EJBPresentationModel.java:160)"""
      val trace = StackTraceParser().parse(text)

      val result = Renderers.textRenderer(Filters.createPackageStartsWithFilter("org.jboss"))(trace)

      result.contains("org.jboss.tm.usertx.client.ClientUserTransaction.begin") should be (false)
    }

  }

  describe("A HTMLRenderer") {
    it("should create links to the jdk javadoc") {
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

      val trace = StackTraceParser().parse(text)
      val result = Renderers.createHtmlRenderer()(Filters.NoFilter)(trace)


      result.contains("http://docs.oracle.com/javase/6/docs/api/java/net/Socket.html") should be (true)
    }

  }
}
