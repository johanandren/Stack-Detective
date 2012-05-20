package com.markatta.stackdetective.api

object DocLinkResolvers {

  type Url = String
  type LinkResolver = Function => Option[Url]

  object JdkDocUrls {
    val jdk142 = "http://docs.oracle.com/javase/1.4.2/docs/api/"
    val jdk5 = "http://docs.oracle.com/javase/1.5.0/docs/api/"
    val jdk6 = "http://docs.oracle.com/javase/6/docs/api/"
    val jdk7 = "http://docs.oracle.com/javase/7/docs/api/"
  }

  def createJavadocLinkResolver(javaDocUrl: String, packageNames: String*): LinkResolver = {
    (function: Function) => {
      if (packageNames.exists(function.packageName.startsWith(_))) {
        Some(javaDocUrl + function.packageName.replace('.', '/') + '/' + function.className + ".html")

      } else {
        None
      }
    }
  }

  def createJdkDocLinkResolver(jdkDocUrl: String): LinkResolver =
    createJavadocLinkResolver(jdkDocUrl, "java.", "javax.", "sun.", "org.w3c.dom")
}
