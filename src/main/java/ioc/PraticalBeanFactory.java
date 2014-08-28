package ioc;

import ioc.util.BeanLoaderException;

public class PraticalBeanFactory {
	private BeanLoader beanLoader;

	public PraticalBeanFactory(String... pck) {
		ClassScanner cscanner = new ClassPathScanner(pck);
		BeanDataLoader bdloader = new AnnotationBeanDataLoader(cscanner);
		beanLoader = new DefaultBeanLoader(bdloader);
	}

	public <T> T getBean(Class<T> clazz) throws BeanLoaderException {
		return beanLoader.getBean(clazz);
	}

	public <T> T getBean(Class<T> clazz, String qualifier) throws BeanLoaderException {
		return beanLoader.getBean(clazz, qualifier);
	}
}
