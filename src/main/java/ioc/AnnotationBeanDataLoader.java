package ioc;

import ioc.annotation.Component;
import ioc.data.BeanData;
import ioc.data.ComponentBean;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

public class AnnotationBeanDataLoader implements BeanDataLoader {

	private ClassScanner scanner;
	private Set<ComponentBean> componentBeans;
	/**
	 * 缓存已经加载过的beanData
	 */
	private Map<Class<?>, BeanData> beanDataMap;

	public AnnotationBeanDataLoader(ClassScanner scanner) {
		this.scanner = scanner;
	}

	private Set<ComponentBean> loadClasses() throws BeanDataLoaderException {
		Set<ComponentBean> componentBeans = new HashSet<ComponentBean>();
		Set<Class<?>> classes = null;
		try {
			classes = scanner.loadClasses();
		} catch (ClassScannerException e) {
			throw new BeanDataLoaderException("load class exception", e);
		}
		for (Class<?> clazz : classes) {
			if (clazz.isAnnotationPresent(Component.class)) {
				String qualifier = ReflectUtil.getClassQualifier(clazz);
				componentBeans.add(new ComponentBean(clazz, qualifier));
			}
		}
		return componentBeans;
	}

	public void loadAllBeanData() throws BeanDataLoaderException {
		if (componentBeans == null)
			componentBeans = loadClasses();
		if (beanDataMap == null)
			beanDataMap = new HashMap<Class<?>, BeanData>();
	}

	public BeanData getBeanData(Class<?> clazz, String qualifier) throws BeanDataLoaderException {
		if (componentBeans == null)
			componentBeans = loadClasses();
		if (beanDataMap == null)
			beanDataMap = new HashMap<Class<?>, BeanData>();
		Class<?> bindingClazz = getBindingClass(clazz, qualifier);
		if (beanDataMap.containsKey(bindingClazz))
			return beanDataMap.get(bindingClazz);
		ConstructorInjectPoint constInjectPoint = findConstructInjectPoint(bindingClazz);
		List<InjectPoint> injectPoints = new ArrayList<InjectPoint>();
		injectPoints.addAll(findFieldInjectPoint(bindingClazz));
		injectPoints.addAll(findMethodInjectPoint(bindingClazz));
		BeanData beanData = new BeanData(bindingClazz, qualifier, constInjectPoint, injectPoints);
		beanDataMap.put(bindingClazz, beanData);
		return beanData;
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
		List<BeanData> dependencies = new ArrayList<BeanData>();
		Constructor constructorNeedInject = null;
		int count = 0; // 计数需要注入的构造器个数
		for (Constructor<?> constructor : constructors) {
			boolean isAccessable = constructor.isAccessible();
			constructor.setAccessible(true);
			if (!constructor.isAnnotationPresent(Inject.class)) {
				constructor.setAccessible(isAccessable);
				continue;
			}
			if (count++ > 1) // 如果计数需要注入的构造器个数超过1个，抛出异常
				throw new BeanDataLoaderException(clazz.toString()
						+ " has too much constructor need to inject");
			Class<?> paramTypes[] = constructor.getParameterTypes();
			Annotation[][] paramAnnotations = constructor.getParameterAnnotations();
			for (int i = 0; i < paramTypes.length; i++) {
				String paramQualifier = ReflectUtil.getParameterQualifier(paramTypes[i],
						paramAnnotations[i]);
				BeanData paramData = getBeanData(paramTypes[i], paramQualifier);
				dependencies.add(paramData);
			}
			constructor.setAccessible(isAccessable);
			constructorNeedInject = constructor;
		}
		if (constructorNeedInject != null)
			return new ConstructorInjectPoint(constructorNeedInject, dependencies);
		try {
			return new ConstructorInjectPoint(clazz.getConstructor(), new ArrayList<BeanData>());
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
			List<BeanData> dependencies = new ArrayList<BeanData>();
			for (int i = 0; i < paramTypes.length; i++) {
				String paramQualifier = ReflectUtil.getParameterQualifier(paramTypes[i],
						paramAnnotations[i]);
				BeanData paramData = getBeanData(paramTypes[i], paramQualifier);
				dependencies.add(paramData);
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
				Class<?> fieldClass = field.getType();
				String fieldQualifier = ReflectUtil.getParameterQualifier(fieldClass,
						field.getAnnotations());
				BeanData fieldData = getBeanData(fieldClass, fieldQualifier);
				injectPoints.add(new FieldInjectPoint(field, fieldData));
			}
			field.setAccessible(isAccessible);
		}
		return injectPoints;
	}

	private Class<?> getBindingClass(Class<?> clazz, String qualifier)
			throws BeanDataLoaderException {
		List<ComponentBean> matched = new ArrayList<ComponentBean>();
		for (ComponentBean bean : componentBeans) {
			if (bean.isMatched(clazz, qualifier))
				matched.add(bean);
		}
		if (matched.size() == 0)
			throw new BeanDataLoaderException("there is no bean match with (" + clazz.toString()
					+ " : " + qualifier + ")  :" + matched);
		else if (matched.size() == 1)
			return matched.get(0).getComponentType();
		else
			throw new BeanDataLoaderException("there is too much bean match with ("
					+ clazz.toString() + " : " + qualifier + ")  :" + matched);
	}

}
