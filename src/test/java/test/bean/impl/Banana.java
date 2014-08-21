package test.bean.impl;

import ioc.annotation.Component;
import test.bean.Fruit;

@Component
public class Banana implements Fruit{

	public String get() {
		return "banana";
	}

}
