package ioc.data;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class FieldInjectPoint implements InjectPoint {
	private Field field;
	private BeanIdentifier dependency;
	private List<BeanIdentifier> dependencies;

	public FieldInjectPoint(Field field, BeanIdentifier dependency) {
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

	public BeanIdentifier getDependency() {
		return dependency;
	}

	public List<BeanIdentifier> getDependencies() {
		return dependencies;
	}

}
