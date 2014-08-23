/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import ioc.AnnotationBeanDataLoader;
import ioc.BeanDataLoader;
import ioc.ClassPathScanner;
import ioc.ClassScanner;
import ioc.data.BeanData;
import ioc.data.InjectPoint;
import ioc.util.BeanDataLoaderException;
import ioc.util.BeanLoaderException;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import test.bean.Fruit;
import test.bean.People;
import test.bean.cycle.CycleOne;

public class AnnotationBeanDataLoaderTest {
	BeanDataLoader beanDataLoader;

	@Before
	public void setUp() throws BeanDataLoaderException {
		ClassScanner scanner = new ClassPathScanner("test.bean");
		beanDataLoader = new AnnotationBeanDataLoader(scanner);
	}

	@Test
	public void testFruitBean() throws BeanDataLoaderException {
		BeanData appleBean = beanDataLoader.getBeanData(Fruit.class, "apple");
		assertNotNull(appleBean);
		BeanData orangeBean = beanDataLoader.getBeanData(Fruit.class, "orange");
		assertNotNull(orangeBean);
	}

	@Test
	public void testPeopleBean() throws BeanDataLoaderException {
		BeanData peopleBean = beanDataLoader.getBeanData(People.class, "people");
		assertNotNull(peopleBean);
		assertEquals(peopleBean.getConstructInjectPoint().getDependencies().get(0).getQualifier(),
				"banana");
		List<InjectPoint> list = peopleBean.getDependencis();
		for (InjectPoint i : list) {
			System.out.println(i.getDependencies().get(0).getQualifier());
		}
	}

	@Test(expected = BeanDataLoaderException.class)
	public void testCycle() throws BeanDataLoaderException {
		try {
			beanDataLoader.getBeanData(CycleOne.class, "cycleOne");
		} catch (BeanDataLoaderException e) {
			e.printStackTrace();
			throw e;
		}
	}

}
