package test.bean.impl;

import javax.annotation.Resource;

import test.bean.Fruit;
import ioc.annotation.Component;

@Component
@Resource(name = "orange")
public class Orange implements Fruit {

	public String get() {
		return "orange";
	}

}
