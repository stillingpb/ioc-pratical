package ioc.data;

public class ComponentBean {
	private Class<?> componentType;
	private String qualifier;

	public ComponentBean(Class<?> componentType, String qualifier) {
		this.componentType = componentType;
		this.qualifier = qualifier;
	}

	public boolean isMatched(Class<?> clazz, String qualifier) {
		if (clazz.isAssignableFrom(componentType) && this.qualifier.equalsIgnoreCase(qualifier))
			return true;
		return false;
	}

	public Class<?> getComponentType() {
		return componentType;
	}

	public String toString() {
		return "(" + componentType.getName() + " : " + qualifier + ")";
	}

	public String getQualifier() {
		return this.qualifier;
	}
}
