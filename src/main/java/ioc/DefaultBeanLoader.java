package ioc;

import ioc.data.BeanData;
import ioc.data.ConstructorInjectPoint;
import ioc.data.InjectPoint;
import ioc.util.BeanDataLoaderException;
import ioc.util.BeanLoaderException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	public Object getBean(String identifier) throws BeanLoaderException {
		BeanData beanData = null;
		try {
			beanData = beanDataLoader.getBeanData(identifier);
		} catch (BeanDataLoaderException e) {
			throw new BeanLoaderException("get bean data Exception ( id: " + identifier + " )", e);
		}
		if (singleBeanMap.containsKey(beanData))
			return singleBeanMap.get(beanData);
		if (beanInCreating.contains(beanData))
			throw new BeanLoaderException("there exists cycle depency " + beanData);
		beanInCreating.add(beanData);
		Object instance = constructBeanInstance(beanData);
		autowiredBean(instance, beanData);
		beanInCreating.remove(beanData);
		if (beanData.isSingleton())
			singleBeanMap.put(beanData, instance);
		return instance;
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
			List<String> dependencies = injectPoint.getDependencies();
			Object[] params = new Object[dependencies.size()];
			for (int i = 0; i < dependencies.size(); i++)
				params[i] = getBean(dependencies.get(i));
			injectPoint.inject(instance, params);
		}
	}

	private Object constructBeanInstance(BeanData beanData) throws BeanLoaderException {
		ConstructorInjectPoint constInjectPoint = beanData.getConstructInjectPoint();
		List<String> dependencies = constInjectPoint.getDependencies();
		Object[] params = new Object[dependencies.size()];
		for (int i = 0; i < dependencies.size(); i++)
			params[i] = getBean(dependencies.get(i));
		return constInjectPoint.newInstance(params);
	}
}
