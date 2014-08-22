package ioc;

import ioc.data.BeanData;
import ioc.data.ComponentBean;
import ioc.data.ConstructorInjectPoint;
import ioc.data.InjectPoint;
import ioc.util.BeanDataLoaderException;
import ioc.util.BeanLoaderException;

import java.util.List;

public class DefaultBeanLoader implements BeanLoader {
	private BeanDataLoader beanDataLoader;

	public DefaultBeanLoader(BeanDataLoader beanDataLoader) {
		this.beanDataLoader = beanDataLoader;
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
		Object instance = constructBeanInstance(beanData);
		autowiredBean(instance, beanData);
		return (T) instance;
	}

	private void autowiredBean(Object instance, BeanData beanData) throws BeanLoaderException {
		for (InjectPoint injectPoint : beanData.getDependencis()) {
			List<BeanData> dependencies = injectPoint.getDependencies();
			Object[] params = new Object[dependencies.size()];
			for (int i = 0; i < dependencies.size(); i++) {
				BeanData paramBeanData = dependencies.get(i);
				params[i] = getBean(paramBeanData.getBeanType(), paramBeanData.getQualifier());
			}
			injectPoint.inject(instance, params);
		}
	}

	private Object constructBeanInstance(BeanData beanData) throws BeanLoaderException {
		ConstructorInjectPoint constInjectPoint = beanData.getConstructInjectPoint();
		List<BeanData> dependencies = constInjectPoint.getDependencies();
		Object[] params = new Object[dependencies.size()];
		for (int i = 0; i < dependencies.size(); i++) {
			BeanData paramBeanData = dependencies.get(i);
			params[i] = getBean(paramBeanData.getBeanType(), paramBeanData.getQualifier());
		}
		return constInjectPoint.newInstance(params);
	}
}
