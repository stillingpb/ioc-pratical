package test.bean.impl;
import test.bean.Fruit;
import ioc.annotation.Component;

@Component
public class Apple implements Fruit {

	public String get() {
		return "apple";
	}

}
