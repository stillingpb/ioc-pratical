package ioc.util;

import java.lang.annotation.Annotation;

import javax.inject.Named;
import javax.inject.Qualifier;

public class ReflectUtil {
	/**
	 * 获取一个被标示为java bean 的类的 qualifier
	 * 
	 * @param clazz
	 * @return qualifier
	 */
	public static String getClassQualifier(Class<?> clazz) {
		String qualifier = clazz.getSimpleName();
		if (clazz.isAnnotationPresent(Named.class)) {
			qualifier = clazz.getAnnotation(Named.class).value();
		} else {
			for (Annotation annotation : clazz.getAnnotations()) {
				if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
					qualifier = annotation.annotationType().getSimpleName();
					break;
				}
			}
		}
		return qualifier;
	}

	public static String getParameterQualifier(Class<?> paramType, Annotation[] annotations) {
		String paramQualifier = paramType.getSimpleName();
		for (Annotation annotation : annotations) {
			Class<?> annotationType = annotation.annotationType();
			if (Named.class.isAssignableFrom(annotationType)) {
				paramQualifier = ((Named) annotation).value();
				break;
			}
			if (annotationType.isAnnotationPresent(Qualifier.class)) {
				paramQualifier = annotationType.getSimpleName();
				break;
			}
		}
		return paramQualifier;
	}
}
