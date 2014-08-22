package ioc;

import ioc.util.BeanLoaderException;

public interface BeanLoader {
	public <T> T getBean(Class<T> clazz) throws BeanLoaderException;

	public <T> T getBean(Class<T> clazz, String qualifier) throws BeanLoaderException;
}
