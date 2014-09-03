package ioc.data;

import java.lang.reflect.Constructor;
import java.util.List;

public class ConstructorInjectPoint implements InjectPoint {
	private Constructor<?> constructor;
	private List<String> dependencies;

	public ConstructorInjectPoint(Constructor<?> constructor, List<String> dependencies) {
		this.constructor = constructor;
		this.dependencies = dependencies;
	}

	public Object newInstance(Object[] params) {
		Object instance = null;
		try {
			boolean isAccessible = constructor.isAccessible();
			constructor.setAccessible(true);
			instance = constructor.newInstance(params);
			constructor.setAccessible(isAccessible);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}

	/**
	 * do nothing
	 */
	public void inject(Object instance, Object... params) {
	}

	public List<String> getDependencies() {
		return this.dependencies;
	}
}
