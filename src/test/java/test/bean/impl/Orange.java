package test.bean.impl;

import ioc.annotation.Component;
import test.bean.Fruit;

@Component("orange")
public class Orange implements Fruit {

	public String get() {
		return "orange";
	}

}
