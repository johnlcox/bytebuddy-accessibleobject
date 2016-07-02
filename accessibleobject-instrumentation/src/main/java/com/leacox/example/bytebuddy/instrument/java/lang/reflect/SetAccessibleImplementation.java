package com.leacox.example.bytebuddy.instrument.java.lang.reflect;

import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.AccessibleObject;

/**
 * @author John Leacox
 */
public class SetAccessibleImplementation {
  public static void setAccessible(@This AccessibleObject ao, boolean flag) throws SecurityException {
    System.out.println("***** We got here *****");
    setAccessible1(ao, flag);
  }

  private static void setAccessible1(AccessibleObject ao, boolean flag){

  }
}
