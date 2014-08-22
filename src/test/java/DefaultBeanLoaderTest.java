import ioc.AnnotationBeanDataLoader;
import ioc.BeanDataLoader;
import ioc.BeanLoader;
import ioc.ClassPathScanner;
import ioc.ClassScanner;
import ioc.DefaultBeanLoader;
import ioc.util.BeanLoaderException;

import org.junit.Before;
import org.junit.Test;

import test.bean.People;

public class DefaultBeanLoaderTest {
	BeanLoader beanLoader;

	@Before
	public void setUp() {
		ClassScanner scanner = new ClassPathScanner("test.bean");
		BeanDataLoader beanDataLoader = new AnnotationBeanDataLoader(scanner);
		beanLoader = new DefaultBeanLoader(beanDataLoader);
	}

	@Test
	public void testPeopleBean() throws BeanLoaderException {
		People p = beanLoader.getBean(People.class);
		p.eat();
	}

}
