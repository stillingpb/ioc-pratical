package ioc;

import ioc.data.BeanData;

public interface BeanDataLoader {
	public BeanData getBeanData(Class<?> clazz, String qualifier);
}
