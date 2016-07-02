package com.leacox.example.bytebuddy;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.pool.TypePool;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.jar.JarFile;

/**
 * @author John Leacox
 */
public class Main {
  public static void main(String[] args) throws Exception {
    instrument();

    Foo foo = new Foo();


    String methodName = "inAccessibleMethod";

    Method method = foo.getClass().getDeclaredMethod(methodName);
    method.setAccessible(true);
    method.invoke(foo);
  }

  private static void instrument() throws Exception {
    Instrumentation instrumentation = ByteBuddyAgent.install();

    File file = new File("../accessibleobject-instrumentation/target/accessibleobject-instrumentation-1.0-SNAPSHOT.jar");
    String path = file.getAbsolutePath();
    System.out.println("jar path: " + path);
    Boolean exists = file.exists();

    JarFile jarFile;
    try {
      jarFile = new JarFile(file);
      instrumentation.appendToBootstrapClassLoaderSearch(jarFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    TypePool typePool = TypePool.Default.of(new ClassFileLocator.Compound(
        new ClassFileLocator.ForJarFile(jarFile),
        ClassFileLocator.ForClassLoader.of(Main.class.getClassLoader())));

    String setAccessible0MethodName = "setAccessible0";
    Class[] paramTypes = new Class[2];
    paramTypes[0] = AccessibleObject.class;
    paramTypes[1] = boolean.class;
    Method setAccessible0Method = AccessibleObject.class.getDeclaredMethod(setAccessible0MethodName, paramTypes);

    new AgentBuilder.Default()
        .disableClassFormatChanges()
        .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
        .with(new SysOutListener())
        .ignore(none())
        .type(named("java.lang.reflect.AccessibleObject"))
        .transform(new AgentBuilder.Transformer() {
          @Override
          public DynamicType.Builder<?> transform(
              DynamicType.Builder<?> builder, TypeDescription typeDescription,
              ClassLoader classLoader) {
            return builder.method(named("setAccessible").and(takesArguments(boolean.class)))
                .intercept(MethodDelegation.to(
                    typePool.describe("com.leacox.example.bytebuddy.instrument.java.lang.reflect.SetAccessibleImplementation").resolve()
                ));
          }
        })
        .type(named("com.leacox.example.bytebuddy.instrument.java.lang.reflect.SetAccessibleImplementation"))
        .transform(new AgentBuilder.Transformer() {
          @Override
          public DynamicType.Builder<?> transform(
              DynamicType.Builder<?> builder, TypeDescription typeDescription,
              ClassLoader classLoader) {
            return builder.method(named("setAccessible1"))
                .intercept(
                    MethodCall.invoke(new MethodDescription.ForLoadedMethod(setAccessible0Method)).withAllArguments());
          }
        })
        .installOnByteBuddyAgent();
  }
}
