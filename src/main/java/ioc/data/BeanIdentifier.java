package ioc.data;

public class BeanIdentifier {
	private Class<?> beanType;
	private String qualifier;
	private boolean isProvider;

	public BeanIdentifier(Class<?> beanType, String qualifier, boolean isProvider) {
		this.beanType = beanType;
		this.qualifier = qualifier;
		this.isProvider = isProvider;
	}

	public Class<?> getBeanType() {
		return beanType;
	}

	public String getQualifier() {
		return qualifier;
	}

	public boolean isProvider() {
		return isProvider;
	}

	public String toString() {
		return "(" + beanType + " : " + qualifier + " : " + isProvider + ")";
	}
}
