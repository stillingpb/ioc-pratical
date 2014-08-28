package test;
import ioc.ClassPathScanner;
import ioc.ClassScanner;
import ioc.util.ClassScannerException;

import java.lang.reflect.Field;
import java.util.Set;

import javax.inject.Qualifier;

import org.junit.Before;
import org.junit.Test;

import test.bean.Fruit;
import test.bean.People;
import test.bean.WaterMellonAnnotation;
import test.bean.impl.Apple;
import test.bean.impl.WaterMellon;

public class ClassScannerTest {
	ClassScanner scanner;

	@Before
	public void setUp() {
		scanner = new ClassPathScanner("javax.inject");
	}

	@Test
	public void testClassLoad() throws ClassScannerException {
		Set<Class<?>> classes = scanner.loadClasses();
		System.out.println(classes);
	}

	@Test
	public void test() throws SecurityException, NoSuchFieldException {
		WaterMellonAnnotation anno = WaterMellon.class.getAnnotation(WaterMellonAnnotation.class);
	}
}
