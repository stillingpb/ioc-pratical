package ioc.data;

import java.util.List;

public class BeanData {
	private Class<?> beanType;
	private String identifier;
	private ConstructorInjectPoint constructInjectPoint;
	private List<InjectPoint> dependencis;

	private boolean isSingleton;

	public BeanData(Class<?> beanType, String identifier, ConstructorInjectPoint constInjectPoint,
			List<InjectPoint> dependencis, boolean isSingleton) {
		this.beanType = beanType;
		this.identifier = identifier;
		this.constructInjectPoint = constInjectPoint;
		this.dependencis = dependencis;
		this.isSingleton = isSingleton;
	}

	public Class<?> getBeanType() {
		return beanType;
	}

	public String getIdentifier() {
		return identifier;
	}

	public List<InjectPoint> getDependencis() {
		return dependencis;
	}

	public ConstructorInjectPoint getConstructInjectPoint() {
		return constructInjectPoint;
	}

	public boolean isSingleton() {
		return isSingleton;
	}

	public String toString() {
		return "( " + beanType.getName() + " : " + identifier + " )";
	}
}
