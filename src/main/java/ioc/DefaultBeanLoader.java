package ioc;

import ioc.data.BeanData;
import ioc.data.BeanIdentifier;
import ioc.data.ConstructorInjectPoint;
import ioc.data.InjectPoint;
import ioc.util.BeanDataLoaderException;
import ioc.util.BeanLoaderException;
import ioc.util.ProviderBeanLoaderException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Provider;

public class DefaultBeanLoader implements BeanLoader {
	private BeanDataLoader beanDataLoader;

	/**
	 * 存储单例java bean
	 */
	private Map<BeanData, Object> singleBeanMap;

	/**
	 * 通过记录beanData，来存储正在创建的bean，从而避免循环依赖
	 */
	private Set<BeanData> beanInCreating;

	public DefaultBeanLoader(BeanDataLoader beanDataLoader) {
		this.beanDataLoader = beanDataLoader;
		singleBeanMap = new HashMap<BeanData, Object>();
		beanInCreating = new HashSet<BeanData>();
	}

	public <T> T getBean(Class<T> clazz) throws BeanLoaderException {
		return getBean(clazz, clazz.getSimpleName());
	}

	public <T> T getBean(Class<T> clazz, String qualifier) throws BeanLoaderException {
		BeanData beanData;
		try {
			beanData = beanDataLoader.getBeanData(clazz, qualifier);
		} catch (BeanDataLoaderException e) {
			throw new BeanLoaderException("get bean data Exception ( " + clazz + " : " + qualifier
					+ " )", e);
		}
		if (singleBeanMap.containsKey(beanData))
			return (T) singleBeanMap.get(beanData);
		if (beanInCreating.contains(beanData))
			throw new BeanLoaderException("there exists cycle depency " + beanData);
		beanInCreating.add(beanData);
		Object instance = constructBeanInstance(beanData);
		autowiredBean(instance, beanData);
		beanInCreating.remove(beanData);
		if (beanData.isSingleton())
			singleBeanMap.put(beanData, instance);
		return (T) instance;
	}

	/**
	 * 装配bean的field,和 method
	 * 
	 * @param instance
	 * @param beanData
	 * @throws BeanLoaderException
	 */
	private void autowiredBean(Object instance, BeanData beanData) throws BeanLoaderException {
		for (InjectPoint injectPoint : beanData.getDependencis()) {
			List<BeanIdentifier> dependencies = injectPoint.getDependencies();
			Object[] params = new Object[dependencies.size()];
			for (int i = 0; i < dependencies.size(); i++)
				params[i] = autowiredParam(dependencies.get(i));
			injectPoint.inject(instance, params);
		}
	}

	private Object constructBeanInstance(BeanData beanData) throws BeanLoaderException {
		ConstructorInjectPoint constInjectPoint = beanData.getConstructInjectPoint();
		List<BeanIdentifier> dependencies = constInjectPoint.getDependencies();
		Object[] params = new Object[dependencies.size()];
		for (int i = 0; i < dependencies.size(); i++)
			params[i] = autowiredParam(dependencies.get(i));
		return constInjectPoint.newInstance(params);
	}

	private Object autowiredParam(final BeanIdentifier identifier) throws BeanLoaderException {
		Object instance = null;
		if (identifier.isProvider()) {
			instance = new Provider() {
				public Object get() {
					try {
						return getBean(identifier.getBeanType(), identifier.getQualifier());
					} catch (BeanLoaderException e) {
						throw new ProviderBeanLoaderException(e);
					}
				}
			};
		} else
			instance = getBean(identifier.getBeanType(), identifier.getQualifier());
		return instance;
	}
}
