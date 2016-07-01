package com.leacox.example.bytebuddy.instrument.java.lang.reflect;

/**
 * @author John Leacox
 */
public class SetAccessibleImplementation {
  public void setAccessible(boolean flag) throws SecurityException {
    System.out.println("***** We got here *****");
  }
}
