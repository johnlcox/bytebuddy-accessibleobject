package com.leacox.example.bytebuddy.instrument.java.lang.reflect;

import net.bytebuddy.implementation.bind.annotation.This;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author John Leacox
 */
public class AccessibleObjectInterceptor {
  private static final AtomicLong count = new AtomicLong(0);

  public static void setAccessible(@This java.lang.reflect.AccessibleObject ao, boolean flag) throws SecurityException {
    System.out.println("***** We got here *****");

    // Throw an exception every other time.
    if (count.incrementAndGet() % 2 == 0) {
      throw new SecurityException("denied");
    }
  }
}
