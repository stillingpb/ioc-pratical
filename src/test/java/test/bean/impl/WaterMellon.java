package test.bean.impl;

import ioc.annotation.Component;
import test.bean.Fruit;
import test.bean.WaterMellonAnnotation;

@Component
@WaterMellonAnnotation
public class WaterMellon implements Fruit {

	public String get() {
		return "water mellon";
	}

}
