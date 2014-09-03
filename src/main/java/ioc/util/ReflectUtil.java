package ioc.util;

import java.lang.annotation.Annotation;

import javax.annotation.Resource;

public class ReflectUtil {
	/**
	 * 获取一个被标示为java bean 的类的 identifier
	 * 
	 * @param clazz
	 * @return identifier
	 */
	public static String getClassIdentifier(Class<?> clazz) {
		String identifier = clazz.getSimpleName();
		if (clazz.isAnnotationPresent(Resource.class))
			identifier = clazz.getAnnotation(Resource.class).name();
		return identifier;
	}

	public static String getBeanIdentifier(Class<?> paramType, Annotation[] annotations) {
		String identifier = paramType.getSimpleName();
		for (Annotation annotation : annotations) {
			Class<?> annotationType = annotation.annotationType();
			if (Resource.class.isAssignableFrom(annotationType)) {
				identifier = ((Resource) annotation).name();
				break;
			}
		}
		return identifier;
	}
}
