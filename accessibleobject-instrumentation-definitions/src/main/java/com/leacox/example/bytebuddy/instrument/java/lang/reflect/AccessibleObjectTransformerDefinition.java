package com.leacox.example.bytebuddy.instrument.java.lang.reflect;

import com.leacox.example.BootstrapTransformationContext;
import com.leacox.example.BootstrapTransformationDefinition;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.JavaModule;

import java.lang.reflect.Method;
import java.security.ProtectionDomain;

/**
 * @author John Leacox
 */
public class AccessibleObjectTransformerDefinition implements BootstrapTransformationDefinition {
  @Override
  public AgentBuilder.RawMatcher getRawMatcher(BootstrapTransformationContext context) {
    return new AgentBuilder.RawMatcher() {
      @Override
      public boolean matches(
          TypeDescription typeDescription, ClassLoader classLoader, JavaModule module,
          Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
        return ElementMatchers.named("java.lang.reflect.AccessibleObject").matches(typeDescription);
      }
    };
  }

  @Override
  public AgentBuilder.Transformer getTransformer(BootstrapTransformationContext context) {
    Method setAccessible0Method;
    try {
      String setAccessible0MethodName = "setAccessible0";
      Class[] paramTypes = new Class[2];
      paramTypes[0] = java.lang.reflect.AccessibleObject.class;
      paramTypes[1] = boolean.class;
      setAccessible0Method = java.lang.reflect.AccessibleObject.class
          .getDeclaredMethod(setAccessible0MethodName, paramTypes);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }

    TypePool typePool = context.getBootstrapPool();

    return new AgentBuilder.Transformer() {
      @Override
      public DynamicType.Builder<?> transform(
          DynamicType.Builder<?> builder, TypeDescription typeDescription,
          ClassLoader classLoader) {
        return builder.method(
            ElementMatchers.named("setAccessible").and(ElementMatchers.takesArguments(boolean.class)))
            .intercept(MethodDelegation.to(
                typePool.describe("com.leacox.example.bytebuddy.instrument.java.lang.reflect.AccessibleObjectInterceptor").resolve()
            ).andThen(MethodCall.invoke(setAccessible0Method).withThis().withAllArguments()));
      }
    };
  }
}
