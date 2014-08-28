package ioc;

import ioc.annotation.Component;
import ioc.data.BeanData;
import ioc.data.BeanIdentifier;
import ioc.data.ConstructorInjectPoint;
import ioc.data.FieldInjectPoint;
import ioc.data.InjectPoint;
import ioc.data.MethodInjectPoint;
import ioc.util.BeanDataLoaderException;
import ioc.util.ClassScannerException;
import ioc.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.sun.net.ssl.internal.ssl.Provider;

public class AnnotationBeanDataLoader implements BeanDataLoader {

	private ClassScanner scanner;
	/**
	 * 缓存已加载的组件bean
	 */
	private ComponentBeanMap componentBeanMap;
	/**
	 * 缓存已经加载过的beanData
	 */
	private Map<Class<?>, BeanData> beanDataMap;

	public AnnotationBeanDataLoader(ClassScanner scanner) {
		this.scanner = scanner;
		beanDataMap = new HashMap<Class<?>, BeanData>();
	}

	public void loadAllBeanData() throws BeanDataLoaderException {
		loadComponentBeanFromClassesIfNeeded();
		Map<String, List<Class<?>>> componentBeans = componentBeanMap.getComponentBeans();
		for (Entry<String, List<Class<?>>> entry : componentBeans.entrySet()) {
			String qualifier = entry.getKey();
			for (Class<?> bindingClazz : entry.getValue())
				loadBeanData(bindingClazz, qualifier);
		}
	}

	public BeanData getBeanData(Class<?> clazz, String qualifier) throws BeanDataLoaderException {
		loadComponentBeanFromClassesIfNeeded();
		Class<?> bindingClazz = getBindingClass(clazz, qualifier);
		if (beanDataMap.containsKey(bindingClazz))
			return beanDataMap.get(bindingClazz);
		return loadBeanData(bindingClazz, qualifier);
	}

	private BeanData loadBeanData(Class<?> clazz, String qualifier) throws BeanDataLoaderException {
		ConstructorInjectPoint constInjectPoint = findConstructInjectPoint(clazz);
		List<InjectPoint> injectPoints = new ArrayList<InjectPoint>();
		injectPoints.addAll(findFieldInjectPoint(clazz));
		injectPoints.addAll(findMethodInjectPoint(clazz));
		boolean isSingleton = findSingleInfo(clazz);
		BeanData beanData = new BeanData(clazz, qualifier, constInjectPoint, injectPoints,
				isSingleton);
		beanDataMap.put(clazz, beanData);
		return beanData;
	}

	private boolean findSingleInfo(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Singleton.class))
			return true;
		return false;
	}

	private void loadComponentBeanFromClassesIfNeeded() throws BeanDataLoaderException {
		if (componentBeanMap != null)
			return;
		componentBeanMap = new ComponentBeanMap();
		Set<Class<?>> classes = null;
		try {
			classes = scanner.loadClasses();
		} catch (ClassScannerException e) {
			throw new BeanDataLoaderException("load class exception", e);
		}
		for (Class<?> clazz : classes) {
			if (clazz.isAnnotationPresent(Component.class)) {
				String qualifier = ReflectUtil.getClassQualifier(clazz);
				componentBeanMap.add(qualifier, clazz);
			}
		}
	}

	/**
	 * 查找需要注入构造器，如果没有构造器被标示为需要注入，选择默认构造器
	 * 
	 * @param clazz
	 * @return 需要注入的构造器
	 * @throws BeanDataLoaderException
	 *             如果有多个构造器，或者无默认构造器
	 */
	private ConstructorInjectPoint findConstructInjectPoint(Class<?> clazz)
			throws BeanDataLoaderException {
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		List<BeanIdentifier> dependencies = new ArrayList<BeanIdentifier>();
		Constructor<?> constructorNeedInject = null;
		int count = 0; // 计数需要注入的构造器个数
		for (Constructor<?> constructor : constructors) {
			boolean isAccessable = constructor.isAccessible();
			constructor.setAccessible(true);
			if (!constructor.isAnnotationPresent(Inject.class)) {
				constructor.setAccessible(isAccessable);
				continue;
			}
			if (++count > 1) // 如果计数需要注入的构造器个数超过1个，抛出异常
				throw new BeanDataLoaderException(clazz.toString()
						+ " has too much constructor need to inject");
			Class<?> paramTypes[] = constructor.getParameterTypes();
			Type genericTypes[] = constructor.getGenericParameterTypes();
			Annotation[][] paramAnnotations = constructor.getParameterAnnotations();
			for (int i = 0; i < paramTypes.length; i++) {
				BeanIdentifier identifier = ReflectUtil.getBeanIdentifier(paramTypes[i],
						genericTypes[i], paramAnnotations[i]);
				dependencies.add(identifier);
			}
			constructor.setAccessible(isAccessable);
			constructorNeedInject = constructor;
		}
		if (constructorNeedInject != null)
			return new ConstructorInjectPoint(constructorNeedInject, dependencies);
		try {
			return new ConstructorInjectPoint(clazz.getConstructor(),
					new ArrayList<BeanIdentifier>());
		} catch (Exception e) {
			throw new BeanDataLoaderException("please reserve a default public constructor for "
					+ clazz.toString(), e);
		}
	}

	private List<InjectPoint> findMethodInjectPoint(Class<?> clazz) throws BeanDataLoaderException {
		List<InjectPoint> injectPoints = new ArrayList<InjectPoint>();
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			boolean isAccessible = method.isAccessible();
			method.setAccessible(true);
			if (!method.isAnnotationPresent(Inject.class)) {
				method.setAccessible(isAccessible);
				continue;
			}
			Class<?> paramTypes[] = method.getParameterTypes();
			Type genericTypes[] = method.getGenericParameterTypes();
			Annotation[][] paramAnnotations = method.getParameterAnnotations();
			List<BeanIdentifier> dependencies = new ArrayList<BeanIdentifier>();
			for (int i = 0; i < paramTypes.length; i++) {
				BeanIdentifier identifier = ReflectUtil.getBeanIdentifier(paramTypes[i],
						genericTypes[i], paramAnnotations[i]);
				dependencies.add(identifier);
			}
			injectPoints.add(new MethodInjectPoint(method, dependencies));
			method.setAccessible(isAccessible);
		}
		return injectPoints;
	}

	private Collection<? extends InjectPoint> findFieldInjectPoint(Class<?> clazz)
			throws BeanDataLoaderException {
		List<InjectPoint> injectPoints = new ArrayList<InjectPoint>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			boolean isAccessible = field.isAccessible();
			field.setAccessible(true);
			if (field.isAnnotationPresent(Inject.class)) {
				Class<?> paramType = field.getType();
				Type genericType = field.getGenericType();
				Annotation[] paramAnnotation = field.getAnnotations();
				BeanIdentifier identifier = ReflectUtil.getBeanIdentifier(paramType, genericType,
						paramAnnotation);
				injectPoints.add(new FieldInjectPoint(field, identifier));
			}
			field.setAccessible(isAccessible);
		}
		return injectPoints;
	}

	private Class<?> getBindingClass(Class<?> clazz, String qualifier)
			throws BeanDataLoaderException {
		List<Class<?>> matched = componentBeanMap.getBindingClass(clazz, qualifier);
		if (matched == null || matched.size() == 0)
			throw new BeanDataLoaderException("there is no bean match with (" + clazz.toString()
					+ " : " + qualifier + ")  :" + matched);
		else if (matched.size() == 1)
			return matched.get(0);
		else
			throw new BeanDataLoaderException("there is too much bean match with ("
					+ clazz.toString() + " : " + qualifier + ")  :" + matched);
	}

	/**
	 * 以<qualifier, List<Class<?>>>的形式保存componentBean,
	 * 供快速定位匹配<class,qualifier>的类
	 * 
	 * @author pb
	 * 
	 */
	private static class ComponentBeanMap {
		/**
		 * <key, value> == <qualifier, List<Class<?>>>
		 */
		private Map<String, List<Class<?>>> componentBeans;

		public ComponentBeanMap() {
			componentBeans = new HashMap<String, List<Class<?>>>();
		}

		public Map<String, List<Class<?>>> getComponentBeans() {
			return componentBeans;
		}

		public void add(String qualifier, Class<?> clazz) {
			qualifier = qualifier.toLowerCase();
			List<Class<?>> classes = componentBeans.get(qualifier);
			if (classes == null) {
				classes = new ArrayList<Class<?>>();
				componentBeans.put(qualifier, classes);
			}
			classes.add(clazz);
		}

		public List<Class<?>> getBindingClass(Class<?> clazz, String qualifier) {
			qualifier = qualifier.toLowerCase();
			List<Class<?>> matched = new ArrayList<Class<?>>();
			List<Class<?>> classes = componentBeans.get(qualifier);
			if (classes == null)
				return matched;
			for (Class<?> c : classes)
				if (clazz.isAssignableFrom(c))
					matched.add(c);
			return matched;
		}
	}

}
