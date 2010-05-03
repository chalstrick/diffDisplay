package com.sap.clap.test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Ignore;

@Ignore
public class TestUtil {
  
  @SuppressWarnings("unchecked")
  public static Object invokeMethod(Object object, String methodName, Object[] argObjects) throws Throwable {
    Class<? extends Object>[] argClasses = null;
    if (argObjects != null) {
      argClasses = new Class[argObjects.length];
      for (int i = 0; i < argObjects.length; i++) {
        argClasses[i] = argObjects[i].getClass();
      }
    }
    return invokeMethod(object, methodName, argClasses, argObjects);
  }
  
  public static Object invokeMethod(Object object, String methodName, Class<? extends Object>[] argClasses, Object[] argObjects) throws Throwable {
    return invokeMethod(object.getClass(), object, methodName, argClasses, argObjects);
  }

  public static Object invokeMethod(Class<? extends Object> clazz, Object object, String methodName, Class<? extends Object>[] argClasses, Object[] argObjects) throws Throwable {
    try {
      Method method =clazz.getDeclaredMethod(methodName, argClasses);
      method.setAccessible(true);
      return method.invoke(object, argObjects);      
    } catch (InvocationTargetException e) {
      throw e.getCause();
    }
  }
  
  public static void assertArrayEqualsIgnoreOrder(final Object[] expecteds, final Object[] actuals) {
    if (expecteds == null) {
      assertNull(actuals);
      return;
    }
  
    final List<Object> expectedList = Arrays.asList(expecteds);
    final List<Object> actualList = new LinkedList<Object>(Arrays.asList(actuals));
  
    if (expectedList.size() != actualList.size()) {
        fail("arrays differ; expected: " + expectedList.toString() + " but was: " + actualList.toString());
    } else {
        for (final Object o : expectedList) {
            assertTrue("arrays differ; " + o.toString() + " not found; expected: " + expectedList.toString()
                    + " but was: " + actualList.toString(), actualList.contains(o));
            actualList.remove(o);
        }
    }
  }
}
