package ioc;

import ioc.annotation.Component;
import ioc.data.BeanData;
import ioc.data.BindingMap;
import ioc.data.ComponentBean;
import ioc.data.ConstructorInjectPoint;
import ioc.data.FieldInjectPoint;
import ioc.data.InjectPoint;
import ioc.data.MethodInjectPoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

public class AnnotationBeanDataLoader implements BeanDataLoader {

	private BindingMap bindingMap;
	private Set<ComponentBean> componentBeans;

	public AnnotationBeanDataLoader(ClassScanner scanner) {
		bindingMap = new BindingMap();
		componentBeans = new HashSet<ComponentBean>();
		Set<Class<?>> classes = scanner.loadClasses();
		for (Class<?> clazz : classes) {
			if (clazz.isAnnotationPresent(Component.class)) {
				String qualifier = clazz.getSimpleName();
				if (clazz.isAnnotationPresent(Named.class)) {
					qualifier = clazz.getAnnotation(Named.class).value();
				}
				componentBeans.add(new ComponentBean(clazz, qualifier));
			}
		}
	}

	public BeanData getBeanData(Class<?> clazz, String qualifier) {
		Class<?> bindingClazz = null;
		try {
			bindingClazz = getBindingClass(clazz, qualifier);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ConstructorInjectPoint constInjectPoint = findConstructInjectPoint(bindingClazz);
		List<InjectPoint> injectPoints = new ArrayList<InjectPoint>();
		injectPoints.addAll(findFieldInjectPoint(bindingClazz));
		injectPoints.addAll(findMethodInjectPoint(bindingClazz));
		ComponentBean component = new ComponentBean(bindingClazz, qualifier);
		return new BeanData(component, constInjectPoint, injectPoints);
	}

	private ConstructorInjectPoint findConstructInjectPoint(Class<?> clazz) {
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		for (Constructor<?> constructor : constructors) {
			if (!constructor.isAnnotationPresent(Inject.class))
				continue;
			Class<?> paramTypes[] = constructor.getParameterTypes();
			Annotation[][] paramAnnotations = constructor.getParameterAnnotations();
			List<BeanData> dependencies = new ArrayList<BeanData>();
			for (int i = 0; i < paramTypes.length; i++) {
				String methodQualifier = paramTypes[i].getSimpleName();
				for (Annotation paramAnnotation : paramAnnotations[i]) {
					if (paramAnnotation.annotationType().isAssignableFrom(Named.class)) {
						methodQualifier = ((Named) paramAnnotation).value();
						break;
					}
				}
				BeanData methodData = getBeanData(paramTypes[i], methodQualifier);
				dependencies.add(methodData);
			}
			return new ConstructorInjectPoint(constructor, dependencies);
		}
		try {
			return new ConstructorInjectPoint(clazz.getConstructor(), new ArrayList<BeanData>());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<InjectPoint> findMethodInjectPoint(Class<?> clazz) {
		List<InjectPoint> injectPoints = new ArrayList<InjectPoint>();
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (!method.isAnnotationPresent(Inject.class))
				continue;
			Class<?> paramTypes[] = method.getParameterTypes();
			Annotation[][] paramAnnotations = method.getParameterAnnotations();
			List<BeanData> dependencies = new ArrayList<BeanData>();
			for (int i = 0; i < paramTypes.length; i++) {
				String methodQualifier = paramTypes[i].getSimpleName();
				for (Annotation paramAnnotation : paramAnnotations[i]) {
					if (paramAnnotation.annotationType().isAssignableFrom(Named.class)) {
						methodQualifier = ((Named) paramAnnotation).value();
						break;
					}
				}
				BeanData methodData = getBeanData(paramTypes[i], methodQualifier);
				dependencies.add(methodData);
			}
			injectPoints.add(new MethodInjectPoint(method, dependencies));
		}
		return injectPoints;
	}

	private Collection<? extends InjectPoint> findFieldInjectPoint(Class<?> clazz) {
		List<InjectPoint> injectPoints = new ArrayList<InjectPoint>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Inject.class)) {
				Class<?> fieldClass = field.getType();
				String fieldQualifier = fieldClass.getSimpleName();
				if (field.isAnnotationPresent(Named.class)) {
					fieldQualifier = field.getAnnotation(Named.class).value();
				}
				BeanData fieldData = getBeanData(fieldClass, fieldQualifier);
				injectPoints.add(new FieldInjectPoint(field, fieldData));
			}
		}
		return injectPoints;
	}

	private Class<?> getBindingClass(Class<?> clazz, String qualifier) throws Exception {
		List<ComponentBean> matched = new ArrayList<ComponentBean>();
		for (ComponentBean bean : componentBeans) {
			if (bean.isMatched(clazz, qualifier))
				matched.add(bean);
		}
		if (matched.size() == 0)
			return null;
		else if (matched.size() == 1)
			return matched.get(0).getComponentType();
		else
			throw new Exception("there is too much bean for (" + clazz.getName() + " : "
					+ qualifier + ")  :" + matched);
	}

}
