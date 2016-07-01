package com.leacox.example.bytebuddy;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.pool.TypePool;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
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

    Method method = foo.getClass().getMethod(methodName);
    method.setAccessible(true);
    method.invoke(foo);
  }

  private static void instrument() {
    Instrumentation instrumentation = ByteBuddyAgent.install();

    File file = new File("../accessibleobject-instrumentation/target/accessibleobject-instrumentation-1.0-SNAPSHOT.jar");
    String path = file.getAbsolutePath();
    System.out.println("jar path: " + path);
    Boolean exists = file.exists();

    try {
      instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(file));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    TypePool typePool = TypePool.Default.of(Main.class.getClassLoader());

    new AgentBuilder.Default()
        //.disableClassFormatChanges()
        .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
        .with(new SysOutListener())
        .ignore(none())
        .type(named("java.lang.reflect.AccessibleObject"))
        .transform(new AgentBuilder.Transformer() {
          @Override
          public DynamicType.Builder<?> transform(
              DynamicType.Builder<?> builder, TypeDescription typeDescription,
              ClassLoader classLoader) {
            return builder.method(named("setAccessible"))
                .intercept(MethodDelegation.to(
                    typePool.describe("com.leacox.example.bytebuddy.instrument.java.lang.reflect.SetAccessibleImplementation")
                ));
          }
        })
        .installOnByteBuddyAgent();
  }
}
