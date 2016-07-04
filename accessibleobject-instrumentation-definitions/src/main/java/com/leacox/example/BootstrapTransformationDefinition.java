package com.leacox.example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.pool.TypePool;

/**
 * @author John Leacox
 */
public interface BootstrapTransformationDefinition {
  AgentBuilder.RawMatcher getRawMatcher(BootstrapTransformationContext context);

  AgentBuilder.Transformer getTransformer(BootstrapTransformationContext context);
}
