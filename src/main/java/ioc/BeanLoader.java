package ioc;

public interface BeanLoader {
	public <T> T getBean(Class<T> clazz);
	public <T> T getBean(Class<T> clazz, String qualifier);
}
