package com.markatta.stackdetective.api.distance

import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import com.markatta.stackdetective.api.StackTraceParser

class DistanceSpec extends Spec with ShouldMatchers {

  describe("The default distance algorithm") {

    it("calculates a to-self-distance cost of 0") {
      val text = """java.rmi.NoSuchObjectException: no such object in table
  at org.jboss.tm.usertx.client.ClientUserTransaction.begin(ClientUserTransaction.java:124)
	at com.example.client.core.util.form.EJBPresentationModel.performFinal(EJBPresentationModel.java:160)"""

      val parser = StackTraceParser()
      val trace = parser.parse(text)

      val result = Distance.defaultDistanceAlgorithm(trace, trace)

      result should be (0.0)
    }

    it("calculates a distance to another trace that is higher than 0") {
      val text1 = """java.rmi.NoSuchObjectException: no such object in table
  at org.jboss.tm.usertx.client.ClientUserTransaction.begin(ClientUserTransaction.java:124)
	at com.example.client.core.util.form.EJBPresentationModel.performFinal(EJBPresentationModel.java:160)"""

      val text2 = """java.net.ConnectException: Connection refused: connect
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
      val trace1 = parser.parse(text1)
      val trace2 = parser.parse(text2)

      val result = Distance.defaultDistanceAlgorithm(trace1, trace2)

      result should be > (0.0)
    }
  }
}
