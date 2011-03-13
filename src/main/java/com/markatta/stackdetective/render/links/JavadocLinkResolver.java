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
package com.markatta.stackdetective.render.links;

import com.markatta.stackdetective.model.Entry;

/**
 * Creates links to a javadoc site for all packages matching a set of possible
 * @author johan
 */
public class JavadocLinkResolver implements ClassLinkResolver {

    private final String javadocUrl;

    private final String[] forPackagesStartingWith;

    /**
     * @param javadocUrl Should contain the entire url to the api dir where the javadoc is located and end with '/'
     * @param forPackagesStartingWith 
     */
    public JavadocLinkResolver(String javadocUrl, String... forPackagesStartingWith) {
        this.javadocUrl = javadocUrl;
        this.forPackagesStartingWith = forPackagesStartingWith;
    }

    public String getURLFor(Entry entry) {

        boolean matchesPackages = false;
        for (int i = 0; i < forPackagesStartingWith.length; i++) {
            String prefix = forPackagesStartingWith[i];
            if (entry.getPackageName().startsWith(prefix)) {
                matchesPackages = true;
                break;
            }
        }
        if (matchesPackages) {

            StringBuilder builder = new StringBuilder();
            builder.append(javadocUrl);
            builder.append(entry.getPackageName().replace('.', '/'));
            builder.append('/');
            builder.append(entry.getClassName());
            builder.append(".html");
            
            return builder.toString();

        } else {
            return null;
        }
    }
}
