package test.bean.impl;

import javax.annotation.Resource;

import test.bean.Fruit;
import ioc.annotation.Component;

@Component
@Resource(name = "apple")
public class Apple implements Fruit {

	public String get() {
		return "apple";
	}

}
