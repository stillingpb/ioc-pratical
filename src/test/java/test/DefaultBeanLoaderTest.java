package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
import test.bean.singleton.PeopleSingleton;

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
		People p = (People) beanLoader.getBean("people");
		p.eat();
	}

	// @Test(expected = BeanLoaderException.class)
	// public void testCycleDepency() throws BeanLoaderException {
	// beanLoader.getBean(CycleOne.class);
	// }

	// @Test
	// public void testProvider() throws BeanLoaderException {
	// CycleTwoProvider twoProvider =
	// beanLoader.getBean(CycleTwoProvider.class);
	// assertNotNull(twoProvider);
	// assertNotNull(twoProvider.one2);
	// assertNotNull(twoProvider.one2.get());
	// }

	@Test
	public void testSingleton() throws BeanLoaderException {
		People p1 = (People) beanLoader.getBean("people");
		People p2 = (People) beanLoader.getBean("people");
		assertNotEquals(p1, p2);
		PeopleSingleton people1 = (PeopleSingleton) beanLoader.getBean("peopleSingleton");
		PeopleSingleton people2 = (PeopleSingleton) beanLoader.getBean("peopleSingleton");
		assertEquals(people1, people2);
		assertEquals(people1.orange, people2.orange);
	}
}
