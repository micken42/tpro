package de.htw_berlin.tpro.test_utils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * @author Michael Baumert
 */
public class AssertAnnotations {

	public static void assertAnnotations(List<Class<?>> annotationTypes, List<Annotation> annotations) {
		// length
		if (annotationTypes.size() != annotations.size()) {
			throw new AssertionError(String.format("Expected %d annotations, but found %d", 
					annotationTypes.size(),
					annotations.size()));
		}
		// exists
		annotationTypes.forEach(annotationType -> {
			long amount = annotations.stream().filter(
					annotation -> annotation.annotationType().isAssignableFrom(annotationType)).count();
			if (amount == 0) {
				throw new AssertionError(String.format("No annotation of type %s found", annotationType.getName()));
			}
		});
	}

	public static void assertType(Class<?> type, Class<?>... annotationTypes) {
		assertAnnotations(Arrays.asList(annotationTypes), Arrays.asList(type.getAnnotations()));
	}

	public static void assertField(Class<?> type, String fieldName, Class<?>... annotationTypes) {
		try {
			assertAnnotations(Arrays.asList(annotationTypes),
					Arrays.asList(type.getDeclaredField(fieldName).getAnnotations()));
		} catch (NoSuchFieldException e) {
			throw new AssertionError(e);
		}
	}
	
	public static void assertMethod(Class<?> type, String methodName, Class<?>... annotationTypes) {
		try {
			assertAnnotations(Arrays.asList(annotationTypes),
					Arrays.asList(type.getDeclaredMethod(methodName).getAnnotations()));
		} catch (NoSuchMethodException e) {
			throw new AssertionError(e);
		}
	}
}