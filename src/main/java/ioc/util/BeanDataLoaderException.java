package ioc.util;

public class BeanDataLoaderException extends Exception {
	public BeanDataLoaderException(String msg) {
		super(msg);
	}

	public BeanDataLoaderException(String msg, Throwable e) {
		super(msg, e);
	}
}
