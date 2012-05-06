package com.markatta.stackdetective.api

object Filters {

  /** a function that returns false for functions that should be omitted
    * and true for functions that should be kept in the stack trace
    */
  type FunctionFilter = (Function) => Boolean

  val NoFilter: FunctionFilter = (function: Function) => true


  def createPackageStartsWithFilter(packageNames: String*): FunctionFilter = {
    (function: Function) => !packageNames.exists(function.packageName.startsWith(_))
  }

  def createCombinedFilter(filters: Seq[FunctionFilter]): FunctionFilter = {
    (function: Function) => filters.exists(_(function))
  }

  def createJbossJee5Filter: FunctionFilter =
    createPackageStartsWithFilter(
      "java.lang.reflect",
      "sun.reflect",
      "org.jboss.aspects.tx",
      "org.jboss.ejb3",
      "org.jboss.aop",
      "$Proxy")


  def createReflectionFilter: FunctionFilter =
    createPackageStartsWithFilter("java.lang.reflect", "sun.reflect")

}
