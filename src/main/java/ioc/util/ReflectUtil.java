package ioc.util;

import ioc.annotation.BeanId;
import ioc.annotation.Component;

import java.lang.annotation.Annotation;

public class ReflectUtil {
	/**
	 * 获取一个被标示为java bean 的类的 identifier
	 * 
	 * @param clazz
	 * @return identifier
	 */
	public static String getClassIdentifier(Class<?> clazz) {
		String identifier = clazz.getSimpleName();
		String deployedId = clazz.getAnnotation(Component.class).value()[0];
		if (!"".equals(deployedId))
			identifier = deployedId;
		return identifier;
	}

	public static String getBeanIdentifier(Class<?> paramType, Annotation[] annotations) {
		String identifier = paramType.getSimpleName();
		for (Annotation annotation : annotations) {
			Class<?> annotationType = annotation.annotationType();
			if (BeanId.class.isAssignableFrom(annotationType)) {
				identifier = ((BeanId) annotation).value()[0];
				break;
			}
		}
		return identifier;
	}
}
