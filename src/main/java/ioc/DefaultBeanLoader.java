package ioc;

import ioc.data.BeanData;
import ioc.data.ComponentBean;
import ioc.data.ConstructorInjectPoint;
import ioc.data.InjectPoint;

import java.util.List;

public class DefaultBeanLoader implements BeanLoader {
	private BeanDataLoader beanDataLoader;

	public DefaultBeanLoader(BeanDataLoader beanDataLoader) {
		this.beanDataLoader = beanDataLoader;
	}

	public <T> T getBean(Class<T> clazz) {
		return getBean(clazz, clazz.getSimpleName());
	}

	private Object getBean(ComponentBean comptBean) {
		return getBean(comptBean.getComponentType(), comptBean.getQualifier());
	}

	public <T> T getBean(Class<T> clazz, String qualifier) {
		BeanData beanData = beanDataLoader.getBeanData(clazz, qualifier);
		Object instance = constructBeanInstance(beanData);
		autowiredBean(instance, beanData);
		return (T) instance;
	}

	private void autowiredBean(Object instance, BeanData beanData) {
		for (InjectPoint injectPoint : beanData.getDependencis()) {
			List<BeanData> dependencies = injectPoint.getDependencies();
			Object[] params = new Object[dependencies.size()];
			for (int i = 0; i < dependencies.size(); i++) {
				BeanData paramBeanData = dependencies.get(i);
				ComponentBean comptBean = paramBeanData.getComponent();
				params[i] = getBean(comptBean);
			}
			injectPoint.inject(instance, params);
		}
	}

	private Object constructBeanInstance(BeanData beanData) {
		ConstructorInjectPoint constInjectPoint = beanData.getConstructInjectPoint();
		List<BeanData> dependencies = constInjectPoint.getDependencies();
		Object[] params = new Object[dependencies.size()];
		for (int i = 0; i < dependencies.size(); i++) {
			BeanData paramBeanData = dependencies.get(i);
			ComponentBean comptBean = paramBeanData.getComponent();
			params[i] = getBean(comptBean);
		}
		return constInjectPoint.newInstance(params);
	}
}
