import ioc.ClassPathScanner;
import ioc.ClassScanner;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class ClassScannerTest {
	ClassScanner scanner;

	@Before
	public void setUp() {
		scanner = new ClassPathScanner("test.bean");
	}

	@Test
	public void testClassLoad() {
		Set<Class<?>> classes = scanner.loadClasses();
		System.out.println(classes);
	}
}
