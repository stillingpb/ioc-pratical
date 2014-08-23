package ioc.util;

import ioc.data.BeanIdentifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.inject.Named;
import javax.inject.Provider;
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

	/**
	 * 通过参数类型，泛化类型，注解获得beanIdentifier
	 * 
	 * @param paramType
	 *            参数类型
	 * @param genericType
	 *            泛化类型
	 * @param paramAnnotations
	 *            注解
	 * @return beanIdentifier
	 */
	public static BeanIdentifier getBeanIdentifier(Class<?> paramType, Type genericType,
			Annotation[] paramAnnotations) {
		boolean isProvider = false;
		if (paramType == Provider.class) {
			ParameterizedType pType = (ParameterizedType) genericType;
			paramType = (Class<?>) pType.getActualTypeArguments()[0];
			isProvider = true;
		}
		String paramQualifier = getParameterQualifier(paramType, paramAnnotations);
		return new BeanIdentifier(paramType, paramQualifier, isProvider);
	}

	private static String getParameterQualifier(Class<?> paramType, Annotation[] annotations) {
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
