package org.jboss.errai.jms.generator;

import java.util.Set;

import com.google.gwt.core.ext.GeneratorContext;

public interface DiscoveryStrategy {
    public Set<String> getCandidate(GeneratorContext context, DiscoveryContext veto);
}
