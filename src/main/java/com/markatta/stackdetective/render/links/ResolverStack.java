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
 * Combines multiple ClassLinkResolvers into one and uses the first one that
 * returns a URL for a given stack trace entry.
 * 
 * @author johan
 */
public final class ResolverStack implements ClassLinkResolver {

    private final ClassLinkResolver[] resolvers;

    public ResolverStack(ClassLinkResolver ... resolvers) {
        this.resolvers = resolvers;
    }

    public String getURLFor(Entry entry) {
        for (int i = 0; i < resolvers.length; i++) {
            ClassLinkResolver classLinkResolver = resolvers[i];

            String url = classLinkResolver.getURLFor(entry);
            if (url != null) {
                return url;
            }
        }

        return null;
    }
}
