package com.markatta.stackdetective.api

import util.parsing.combinator._
import com.markatta.stackdetective.api._

trait StackTraceParser {
  /**
    * @param text A trimmed stack trace error print, with unix style linefeeds
    * @return A stack trace object
    */
  def parse(text: String): StackTrace
}

object StackTraceParser {
  def apply(): StackTraceParser = new RegexParser
}

final class UnparseableTrace(message: String) extends RuntimeException(message)

final private[api] class RegexParser extends RegexParsers with StackTraceParser {

  override def skipWhitespace = false

  def javaLetter = regex("""[a-zA-Z$_]""".r)

  def javaDigit = regex("""[0-9]""".r)

  def lf = regex("""\r?\n""".r)

  def ws = regex("""[ \t]*""".r)

  def javaLetterOrDigit = javaLetter | javaDigit ^^ {
    case either => either
  }

  def identifier = javaLetter ~ rep(javaLetterOrDigit) ^^ {
    case letter~more => letter + more.mkString
  }

  def number: Parser[Int] = regex("[0-9]+".r) ^^ { _.toInt }

  def fileName: Parser[String] = regex("""[^:]+""".r) ^^ { _.toString }

  def location: Parser[Location] =
    "Unknown Source" ^^ { case _ => UnknownLocation } |
    "Native Method" ^^ { case _ => NativeMethod } |
    fileName ~ ":" ~ number ^^ { case n~":"~l => FileLocation(n, l) }

  def functionIdentifier: Parser[String] =
    repsep(identifier, ".") ~ ".<init>" ^^ { case path~init => path.mkString(".") + init } |
    repsep(identifier, ".") ^^ { _.mkString(".") }

  def entry: Parser[Function] =
    functionIdentifier ~ "(" ~ location ~ ")" ^^ { case id~"("~loc~")" => Function(id, loc) }

  def entryLine: Parser[Function] =
    ws ~ "at" ~ ws ~> entry

  def entries: Parser[List[Function]] =
    rep1sep(entryLine, "\n") ^^ { case entries => entries }

  def stackTraceMessage: Parser[String] =
    regex("""(?:[\S ])+""".r)

  def traceClass: Parser[String] =
    rep1sep(identifier, ".") ^^ { _.mkString(".") }

  def stackTraceHeader: Parser[Exception] =
    ( traceClass ~ regex(": ?".r) ~ stackTraceMessage <~ lf ) ^^ {  case t~_~m => Exception(t, m) } |
    ("Caused by: " ~> traceClass ~ regex(": ?".r) ~ stackTraceMessage <~lf) ^^ { case t~_~m => Exception(t, m) } |
    ( traceClass <~ lf ) ^^ { case t => Exception(t, "") }

  def segment: Parser[Segment] =
    stackTraceHeader ~ entries ~ regex("""\n\s*\.{3} """.r) ~ number ~ " more" ^^ { case ex~en~_~trunc~_ => Segment(ex, en, trunc)} |
    stackTraceHeader ~ entries <~ regex("""\n?""".r)^^ { case ex~en => Segment(ex, en) }

  def stackTrace: Parser[StackTrace] = ws ~> rep1(segment) <~ ws ^^ { StackTrace(_) }

  /** @throws com.markatta.stackdetective.api.UnparseableTrace if input is not parseable */
  def parse(text: String): StackTrace = {
    parseAll(stackTrace, text) match {
      case Success(result, _) => result
      case e: NoSuccess => throw new UnparseableTrace("Unparseable input: " + text + "\n" + e)
    }
  }


}


