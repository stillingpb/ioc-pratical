package ioc;

import ioc.util.BeanLoaderException;

public interface BeanLoader {
	public Object getBean(String identifier) throws BeanLoaderException;
}
