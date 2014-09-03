package ioc;

import ioc.annotation.Component;
import ioc.data.BeanData;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

public class AnnotationBeanDataLoader implements BeanDataLoader {

	private ClassScanner scanner;

	/**
	 * id号和Class的绑定关系 <identify, clazz>
	 */
	private Map<String, Class<?>> idToClassBinding;

	/**
	 * 缓存已经加载过的beanData
	 */
	private Map<Class<?>, BeanData> beanDataMap;

	public AnnotationBeanDataLoader(ClassScanner scanner) {
		this.scanner = scanner;
		idToClassBinding = new HashMap<String, Class<?>>();
		beanDataMap = new HashMap<Class<?>, BeanData>();
	}

	public void loadAllBeanData() throws BeanDataLoaderException {
		loadComponentBeanFromClassesIfNeeded();
		for (Entry<String, Class<?>> entry : idToClassBinding.entrySet()) {
			String identifier = entry.getKey();
			Class<?> clazz = entry.getValue();
			BeanData beanData = loadBeanData(clazz, identifier);
			beanDataMap.put(clazz, beanData);
		}
	}

	public BeanData getBeanData(String identifier) throws BeanDataLoaderException {
		loadComponentBeanFromClassesIfNeeded();
		Class<?> bindingClazz = idToClassBinding.get(identifier);
		if (beanDataMap.containsKey(bindingClazz))
			return beanDataMap.get(bindingClazz);
		return loadBeanData(bindingClazz, identifier);
	}

	private BeanData loadBeanData(Class<?> clazz, String identifier) throws BeanDataLoaderException {
		ConstructorInjectPoint constInjectPoint = findConstructInjectPoint(clazz);
		List<InjectPoint> injectPoints = new ArrayList<InjectPoint>();
		injectPoints.addAll(findFieldInjectPoint(clazz));
		injectPoints.addAll(findMethodInjectPoint(clazz));
		boolean isSingleton = findSingleInfo(clazz);
		BeanData beanData = new BeanData(clazz, identifier, constInjectPoint, injectPoints,
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
		Set<Class<?>> classes = null;
		try {
			classes = scanner.loadClasses();
		} catch (ClassScannerException e) {
			throw new BeanDataLoaderException("load class exception", e);
		}
		for (Class<?> clazz : classes) {
			if (clazz.isAnnotationPresent(Component.class)) {
				String identifier = ReflectUtil.getClassIdentifier(clazz);
				idToClassBinding.put(identifier, clazz);
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
		List<String> dependencies = new ArrayList<String>();
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
			Annotation[][] paramAnnotations = constructor.getParameterAnnotations();
			for (int i = 0; i < paramTypes.length; i++) {
				String identifier = ReflectUtil.getBeanIdentifier(paramTypes[i],
						paramAnnotations[i]);
				dependencies.add(identifier);
			}
			constructor.setAccessible(isAccessable);
			constructorNeedInject = constructor;
		}
		if (constructorNeedInject != null)
			return new ConstructorInjectPoint(constructorNeedInject, dependencies);
		try {
			return new ConstructorInjectPoint(clazz.getConstructor(), new ArrayList<String>());
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
			Annotation[][] paramAnnotations = method.getParameterAnnotations();
			List<String> dependencies = new ArrayList<String>();
			for (int i = 0; i < paramTypes.length; i++) {
				String identifier = ReflectUtil.getBeanIdentifier(paramTypes[i],
						paramAnnotations[i]);
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
				Annotation[] paramAnnotation = field.getAnnotations();
				String identifier = ReflectUtil.getBeanIdentifier(paramType, paramAnnotation);
				injectPoints.add(new FieldInjectPoint(field, identifier));
			}
			field.setAccessible(isAccessible);
		}
		return injectPoints;
	}
}
