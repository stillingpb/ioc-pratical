package ioc;

import ioc.util.ClassScannerException;

import java.util.Set;

/**
 * 从指定java class path 中，加载所有类的类路径中，被注解或未被注解为javaBean的Class
 * 
 * @author pb
 * 
 */
public interface ClassScanner {
	/**
	 * 从指定java class path 中，加载所有类的类路径中，被注解或未被注解为javaBean的Class
	 * 
	 * @return 所有的class对象
	 * @throws ClassScannerException 解析class时出现的异常
	 */
	public Set<Class<?>> loadClasses() throws ClassScannerException;
}
