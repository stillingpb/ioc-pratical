package ioc.util;

/**
 * ClassScanner对象扫描class path下的类，可能抛出此异常
 * 
 * @author pb
 * 
 */
public class ClassScannerException extends Exception {
	public ClassScannerException(String msg) {
		super(msg);
	}
}
