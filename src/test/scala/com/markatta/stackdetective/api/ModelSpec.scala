package com.markatta.stackdetective.api

import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers

class ModelSpec extends Spec with ShouldMatchers {

  describe("A Function") {

    it("knows its name parts") {
      val func = Function("java.rmi.NoSuchObjectException.toString", UnknownLocation)

      func.packageName should equal ("java.rmi")
      func.className should equal ("NoSuchObjectException")
      func.methodName should equal ("toString")
      func.fqClassName should equal ("java.rmi.NoSuchObjectException")
      func.fqMethodName should equal ("java.rmi.NoSuchObjectException.toString")
    }
  }
}
