package com.leacox.example.bytebuddy;

import static net.bytebuddy.matcher.ElementMatchers.none;

import com.leacox.example.BootstrapTransformationContext;
import com.leacox.example.BootstrapTransformationDefinition;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.pool.TypePool;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.ServiceLoader;
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

    try {
      method.setAccessible(false);
      throw new RuntimeException("Expected a security exception");
    } catch (SecurityException e) {
      System.out.println(e.getMessage());
    }
  }

  private static void instrument() throws Exception {
    Instrumentation instrumentation = ByteBuddyAgent.install();

    File file = new File(
        "../accessibleobject-instrumentation/target/accessibleobject-instrumentation-1.0-SNAPSHOT.jar");
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

    AgentBuilder agentBuilder = new AgentBuilder.Default()
        .disableClassFormatChanges()
        .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
        .with(new SysOutListener())
        .ignore(none());

    TypePool typePool = TypePool.Default.of(new ClassFileLocator.Compound(
        new ClassFileLocator.ForJarFile(jarFile),
        ClassFileLocator.ForClassLoader.of(null)));
    agentBuilder = installTransformers(agentBuilder, typePool);

    agentBuilder.installOnByteBuddyAgent();
  }

  private static AgentBuilder installTransformers(AgentBuilder agentBuilder, TypePool typePool) {
    BootstrapTransformationContext context = new BootstrapTransformationContext(typePool);
    for (BootstrapTransformationDefinition definition : ServiceLoader
        .load(BootstrapTransformationDefinition.class)) {
      agentBuilder = agentBuilder
          .type(definition.getRawMatcher(context))
          .transform(definition.getTransformer(context));
    }

    return agentBuilder;
  }
}
