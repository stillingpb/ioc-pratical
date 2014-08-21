package ioc.data;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class FieldInjectPoint implements InjectPoint {
	private Field field;
	private BeanData dependency;
	private List<BeanData> dependencies;

	public FieldInjectPoint(Field field, BeanData dependency) {
		this.field = field;
		this.dependency = dependency;
		this.dependencies = Arrays.asList(dependency);
	}

	public void inject(Object instance, Object... params) {
		boolean isAccessiable = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(instance, params[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		field.setAccessible(isAccessiable);
	}

	public BeanData getDependency() {
		return dependency;
	}

	public List<BeanData> getDependencies() {
		return dependencies;
	}

}
