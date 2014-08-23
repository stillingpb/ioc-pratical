package test;

import static org.junit.Assert.assertNotNull;
import ioc.AnnotationBeanDataLoader;
import ioc.BeanDataLoader;
import ioc.BeanLoader;
import ioc.ClassPathScanner;
import ioc.ClassScanner;
import ioc.DefaultBeanLoader;
import ioc.util.BeanDataLoaderException;
import ioc.util.BeanLoaderException;

import org.junit.Before;
import org.junit.Test;

import test.bean.People;
import test.bean.cycle.CycleOne;
import test.bean.cycle.CycleOneProvider;
import test.bean.cycle.CycleTwoProvider;

public class DefaultBeanLoaderTest {
	BeanLoader beanLoader;

	@Before
	public void setUp() throws BeanDataLoaderException {
		ClassScanner scanner = new ClassPathScanner("test.bean");
		BeanDataLoader beanDataLoader = new AnnotationBeanDataLoader(scanner);
		beanLoader = new DefaultBeanLoader(beanDataLoader);
	}

	@Test
	public void testPeopleBean() throws BeanLoaderException {
		People p = beanLoader.getBean(People.class);
		p.eat();
	}

	@Test(expected = BeanLoaderException.class)
	public void testCycleDepency() throws BeanLoaderException {
		beanLoader.getBean(CycleOne.class);
	}

	@Test
	public void testProvider() throws BeanLoaderException {
		CycleTwoProvider twoProvider = beanLoader.getBean(CycleTwoProvider.class);
		assertNotNull(twoProvider);
		assertNotNull(twoProvider.one2);
		assertNotNull(twoProvider.one2.get());
	}
}
