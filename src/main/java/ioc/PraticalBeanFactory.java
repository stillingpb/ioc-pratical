package ioc;

import ioc.util.BeanLoaderException;

public class PraticalBeanFactory {
	private BeanLoader beanLoader;

	public PraticalBeanFactory(String... pck) {
		ClassScanner cscanner = new ClassPathScanner(pck);
		BeanDataLoader bdloader = new AnnotationBeanDataLoader(cscanner);
		beanLoader = new DefaultBeanLoader(bdloader);
	}

	public Object getBean(String identifier) throws BeanLoaderException {
		return beanLoader.getBean(identifier);
	}
}
