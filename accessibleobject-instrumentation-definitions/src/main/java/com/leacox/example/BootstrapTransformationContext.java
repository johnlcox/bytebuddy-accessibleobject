package com.leacox.example;

import net.bytebuddy.pool.TypePool;

/**
 * @author John Leacox
 */
public class BootstrapTransformationContext {
  private final TypePool bootstrapPool;

  public BootstrapTransformationContext(TypePool bootstrapPool) {
    this.bootstrapPool = bootstrapPool;
  }

  public TypePool getBootstrapPool() {
    return bootstrapPool;
  }
}
