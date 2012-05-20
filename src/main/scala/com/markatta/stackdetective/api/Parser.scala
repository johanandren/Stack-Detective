package com.markatta.stackdetective.api

import util.parsing.combinator._
import com.markatta.stackdetective.api._
import java.util.regex.Pattern
import util.matching.Regex
import util.parsing.input.Reader

trait StackTraceParser {
  /** @param text A trimmed stack trace error print, with unix style linefeeds
    * @return A stack trace object
    */
  def parse(text: String): StackTrace
}

object StackTraceParser {

  /** factory function for creating a stack trace parser */
  def apply(): StackTraceParser = new RegexParser
}

final class UnparseableTrace(message: String) extends RuntimeException(message)

/** Stack trace text parser based on the scala SDK parser combinator api
  */
final private[api] class RegexParser extends RegexParsers with StackTraceParser {

  override def skipWhitespace = false

  object patterns {
    // just to avoid recompiling the regexps on each parse
    val javaLetter = """[a-zA-Z$_]""".r
    val javaDigit = """[0-9]""".r
    val lf = """\r?\n""".r
    val ws = """[ \t]*""".r
    val number = "[0-9]+".r
    val filename = """[^:]+""".r
    val messageToken = """(\s*)\S+?(\s+)""".r
    val messageTerminal = """(\r?\n\s*at)$""".r
  }

  /** match the input with rep but stop att the first occurence of terminal */
  def messageParser: Parser[String] = Parser { in =>
    if (in.atEnd) {
      Failure("End of input", in)
    } else {
      val buffer = StringBuilder.newBuilder
      buffer ++= in.first.toString
      // keep track of last 4 so that we can go back before at when it is found
      var rests = List[(String,Reader[RegexParser.this.type#Elem])]()
      var rest = in.rest

      while (!rest.atEnd && patterns.messageTerminal.findFirstIn(buffer).isEmpty){
        val char = rest.first.toString
        // keep track of the input at each character
        rests ::= (char -> rest)
        buffer ++= char
        rest = rest.rest
      }
      // whoa whoa, "at" found, now we need to back up until before it
      // the \n\r?\sat part and flag a success from there
      val terminalMatch = patterns.messageTerminal.findFirstIn(buffer).get
      val charsInTerminal = terminalMatch.length

      val inBeforeAtPart = rests(charsInTerminal - 1)._2
      buffer.length -= charsInTerminal
      Success(buffer.toString, inBeforeAtPart)

    }


  }




  def javaLetter = regex(patterns.javaLetter)

  def javaDigit = regex(patterns.javaDigit)

  def lf = regex(patterns.lf)

  def ws = regex(patterns.ws)

  def javaLetterOrDigit = javaLetter | javaDigit ^^ {
    case either => either
  }

  def identifier = javaLetter ~ rep(javaLetterOrDigit) ^^ {
    case letter~more => letter + more.mkString
  }

  def number: Parser[Int] = regex(patterns.number) ^^ { _.toInt }

  def fileName: Parser[String] = regex(patterns.filename) ^^ { _.toString }

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

  def stackTraceMessage: Parser[String] = messageParser

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


