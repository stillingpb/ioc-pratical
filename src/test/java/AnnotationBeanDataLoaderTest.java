import static org.junit.Assert.*;
import ioc.AnnotationBeanDataLoader;
import ioc.BeanDataLoader;
import ioc.ClassPathScanner;
import ioc.ClassScanner;
import ioc.data.BeanData;
import ioc.data.ComponentBean;
import ioc.data.FieldInjectPoint;
import ioc.data.MethodInjectPoint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.bean.Fruit;
import test.bean.People;
import test.bean.impl.Apple;
import test.bean.impl.Orange;

public class AnnotationBeanDataLoaderTest {
	BeanDataLoader beanDataLoader;

	@Before
	public void setUp() {
		ClassScanner scanner = new ClassPathScanner("test.bean");
		beanDataLoader = new AnnotationBeanDataLoader(scanner);
	}

	@Test
	public void testFruitBean() {
		BeanData appleBean = beanDataLoader.getBeanData(Fruit.class, "apple");
		assertNotNull(appleBean);
		BeanData orangeBean = beanDataLoader.getBeanData(Fruit.class, "orange");
		assertNotNull(orangeBean);
	}

	@Test
	public void testPeopleBean() {
		BeanData peopleBean = beanDataLoader.getBeanData(People.class, "people");
		assertNotNull(peopleBean);
		FieldInjectPoint fieldInjectPoint = (FieldInjectPoint) peopleBean.getDependencis().get(0);
		ComponentBean orangeComponent = fieldInjectPoint.getDependency().getComponent();
		assertEquals(orangeComponent.getComponentType(), Orange.class);

		MethodInjectPoint methodInjectPoint = (MethodInjectPoint) peopleBean.getDependencis()
				.get(1);
		ComponentBean appleComponent = methodInjectPoint.getDependencies().get(0).getComponent();
		assertEquals(appleComponent.getComponentType(), Apple.class);
	}
}
