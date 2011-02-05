package com.markatta.stackdetective.util;

import com.markatta.stackdetective.model.StackTrace;
import com.markatta.stackdetective.render.HtmlTraceRenderer;
import com.markatta.stackdetective.render.links.ClassLinkResolver;
import com.markatta.stackdetective.render.links.JdkResolverFactory;
import com.markatta.stackdetective.render.links.ResolverStack;
import java.util.List;

/**
 * Small demo utility that reads stacktraces from files and prints them re-formatted
 * to standard out.
 * 
 * @author johan
 */
public final class RenderAsHTML {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: RenderAsText testFile1 [testFile2 ...]");
            System.out.println(" where testfile contains stacktraces separated by empty lines");
            return;
        }

        List<StackTrace> stackTraces = FileParser.parseOnePerFile(args);

        System.out.println("<html><body>");

        ClassLinkResolver linkResolver = new ResolverStack(JdkResolverFactory.create(JdkResolverFactory.JDK6_BASE_URL));
        RenderUtil renderer = new RenderUtil();
        renderer.render(
                new HtmlTraceRenderer(linkResolver),
                stackTraces,
                System.out);

        System.out.println("</body></html>");
    }
}
