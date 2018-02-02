package de.htw_berlin.tpro.test.utils.jpa;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Michael Baumert
 */
public class ReflectionHelper {
  public static <T extends Annotation> T getMethodAnnotation(
      Class<?> type, String methodName, Class<T> annotation) {
    try {
      Method m = type.getDeclaredMethod(methodName);
      return (T)m.getAnnotation(annotation);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
  public static <T extends Annotation> T getFieldAnnotation(
      Class<?> type, String fieldName, Class<T> annotation) {
    try {
      Field f = type.getDeclaredField(fieldName);
      return (T)f.getAnnotation(annotation);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
  public static <T extends Annotation> T getClassAnnotation(
      Class<?> type, Class<T> annotation) {
    return (T) type.getAnnotation(annotation);
  }
}