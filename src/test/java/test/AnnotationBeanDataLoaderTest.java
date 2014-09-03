package test;

import static org.junit.Assert.assertNotNull;
import ioc.AnnotationBeanDataLoader;
import ioc.BeanDataLoader;
import ioc.ClassPathScanner;
import ioc.ClassScanner;
import ioc.data.BeanData;
import ioc.data.InjectPoint;
import ioc.util.BeanDataLoaderException;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class AnnotationBeanDataLoaderTest {
	BeanDataLoader beanDataLoader;

	@Before
	public void setUp() throws BeanDataLoaderException {
		ClassScanner scanner = new ClassPathScanner("test.bean");
		beanDataLoader = new AnnotationBeanDataLoader(scanner);
	}

	@Test
	public void testFruitBean() throws BeanDataLoaderException {
		BeanData appleBean = beanDataLoader.getBeanData("apple");
		assertNotNull(appleBean);
		BeanData orangeBean = beanDataLoader.getBeanData("orange");
		assertNotNull(orangeBean);
	}

	@Test
	public void testPeopleBean() throws BeanDataLoaderException {
		BeanData peopleBean = beanDataLoader.getBeanData("people");
		assertNotNull(peopleBean);
		List<InjectPoint> list = peopleBean.getDependencis();
		for (InjectPoint i : list) {
			System.out.println(i.getDependencies());
		}
	}
}
