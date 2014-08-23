package ioc.util;

public class ProviderBeanLoaderException extends RuntimeException {
	public ProviderBeanLoaderException(BeanLoaderException e) {
		super("provider.get() exception", e);
	}
}
