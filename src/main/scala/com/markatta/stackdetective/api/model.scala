package com.markatta.stackdetective.api


abstract sealed class Location {
  def file: String
  def line: Int
  def isKnown: Boolean
}

abstract class NotKnownLocation extends Location {
  def file = "Unknown"
  def line = -1
  val isKnown = false
}

case object UnknownLocation extends NotKnownLocation

case object NativeMethod extends NotKnownLocation

final case class FileLocation(file: String, line: Int) extends Location {
  val isKnown = true
}

final case class Function(packageName: String, className: String, methodName: String, location: Location) {
  def fqClassName = packageName + "." + className
  def fqMethodName = fqClassName + "." + methodName
}

object Function {
  def apply(fqFunctionName: String, location: Location): Function = {
    val parts = fqFunctionName.split('.')
    val packageName = parts.take(parts.length - 2).mkString(".")
    val className = parts(parts.length - 2)
    val functionName = parts(parts.length - 1)

    new Function(packageName, className, functionName, location)
  }
}

final case class Exception(exceptionType: String, message: String)

final case class Segment(exception: Exception, entries: List[Function], truncatedLines: Int = 0) {

  lazy val truncated: Boolean = truncatedLines > 0
}

final case class StackTrace(segments: List[Segment])
