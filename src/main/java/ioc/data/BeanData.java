package ioc.data;

import java.util.List;

public class BeanData {
	private Class<?> beanType;
	private String qualifier;
	private ConstructorInjectPoint constructInjectPoint;
	private List<InjectPoint> dependencis;

	public BeanData(Class<?> beanType, String qualifier, ConstructorInjectPoint constInjectPoint,
			List<InjectPoint> dependencis) {
		this.beanType = beanType;
		this.qualifier = qualifier;
		this.constructInjectPoint = constInjectPoint;
		this.dependencis = dependencis;
	}

	public Class<?> getBeanType() {
		return beanType;
	}

	public String getQualifier() {
		return qualifier;
	}

	public List<InjectPoint> getDependencis() {
		return dependencis;
	}

	public ConstructorInjectPoint getConstructInjectPoint() {
		return constructInjectPoint;
	}

	public String toString() {
		return "( " + beanType.getName() + " : " + qualifier + " )";
	}
}
