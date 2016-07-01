package com.leacox.example.bytebuddy;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

/**
 * @author John Leacox
 */
public class SysOutListener implements AgentBuilder.Listener {
  @Override
  public void onTransformation(
      TypeDescription typeDescription, ClassLoader classLoader, JavaModule module,
      DynamicType dynamicType) {

  }

  @Override
  public void onIgnored(
      TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {

  }

  @Override
  public void onError(
      String typeName, ClassLoader classLoader, JavaModule module, Throwable throwable) {
    System.out.println("Error: " + typeName + "\n" + throwable);
  }

  @Override
  public void onComplete(
      String typeName, ClassLoader classLoader, JavaModule module) {

  }
}
