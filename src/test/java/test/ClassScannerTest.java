package test;
import ioc.ClassPathScanner;
import ioc.ClassScanner;
import ioc.util.ClassScannerException;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

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

}
