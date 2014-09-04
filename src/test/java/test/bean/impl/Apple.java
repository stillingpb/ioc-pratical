package test.bean.impl;

import ioc.annotation.Component;
import test.bean.Fruit;

@Component("apple")
public class Apple implements Fruit {

	public String get() {
		return "apple";
	}

}
