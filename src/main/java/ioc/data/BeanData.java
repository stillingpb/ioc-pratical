package ioc.data;

import java.util.List;

public class BeanData {
	private ComponentBean component;
	private ConstructorInjectPoint constructInjectPoint;
	private List<InjectPoint> dependencis;

	public BeanData(ComponentBean component, ConstructorInjectPoint constInjectPoint,
			List<InjectPoint> dependencis) {
		this.component = component;
		this.constructInjectPoint = constInjectPoint;
		this.dependencis = dependencis;
	}

	public ComponentBean getComponent() {
		return component;
	}

	public List<InjectPoint> getDependencis() {
		return dependencis;
	}

	public ConstructorInjectPoint getConstructInjectPoint() {
		return constructInjectPoint;
	}

}
