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
