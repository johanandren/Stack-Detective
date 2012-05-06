package com.markatta.stackdetective.api

import Filters.FunctionFilter
import DocLinkResolvers._

object Renderers {

  type Renderer = FunctionFilter => StackTrace => String

  private def locationAsText(location: Location) = (location match {
    case l: FileLocation => l.file + ":" + l.line
    case UnknownLocation => "Unknown Source"
    case NativeMethod => "Native Method"
  })

  val textRenderer: Renderer = { filter: FunctionFilter => trace: StackTrace =>
    val builder = StringBuilder.newBuilder

    trace.segments.zipWithIndex.foreach { case (segment, row) =>
      if (row > 0)
        builder ++= "Caused by: "
      builder ++= segment.exception.exceptionType ++= ": " ++= segment.exception.message ++= "\n"

      def locationAsText(location: Location) = (location match {
          case l: FileLocation => l.file + ":" + l.line
          case UnknownLocation => "Unknown source"
        })

      segment.entries.filter(filter).foreach { function =>
        builder ++= "  at " ++= function.fqMethodName ++= "(" ++= locationAsText(function.location) ++= ")\n"
      }

      if (segment.truncated) {
        builder ++= "  ... " ++= segment.truncatedLines.toString ++= " more"
      }
      builder ++= "\n"
    }

    // remove last 2 linefeeds
    builder.length = builder.length - 2
    builder.toString
  }

  def createHtmlRenderer(linkResolvers: Seq[LinkResolver] = List(createJdkDocLinkresolver(JdkDocUrls.jdk6))): Renderer = {
     filter: FunctionFilter => trace: StackTrace => {
      val builder = new StringBuilder

      builder ++= "<pre class=\"stacktrace\">"
      trace.segments.zipWithIndex.foreach { case (segment, row) =>
        if (row > 0)
          builder ++= "Caused by: "
        builder ++= segment.exception.exceptionType ++= ": " ++= segment.exception.message ++= "\n"



        segment.entries.foreach { function =>
          val url = linkResolvers.view.map(_(function)).find(_.isDefined)
          builder ++= "  at "
          url match {
            case Some(Some(url)) => {
              builder ++= "<a href\"" + url + "\">" ++=
                function.fqMethodName ++= "(" ++= locationAsText(function.location) ++=
                ")</a>\n"
            }
            case _ => {
              builder ++= function.fqMethodName ++= "(" ++= locationAsText(function.location) ++= ")\n"
            }
          }

        }

        if (segment.truncated) {
          builder ++= "  ... " ++= segment.truncatedLines.toString ++= " more"
        }
        builder ++= "\n"
      }

      // remove last 2 linefeeds
      builder.length = builder.length - 2
      builder ++= "</pre>"
      builder.toString

    }
  }

}