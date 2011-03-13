/**
 * Copyright (C) 2011 Johan Andren <johan@markatta.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
