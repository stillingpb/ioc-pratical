package test.bean;

import ioc.annotation.BeanId;
import ioc.annotation.Component;

import javax.inject.Inject;

@Component("people")
public class People {

	@Inject
	@BeanId("orange")
	Fruit orange;

	Fruit apple;

	Fruit banana;

	@Inject
	public People(@BeanId("banana") Fruit banana) {
		this.banana = banana;
	}

	@Inject
	public void setApple(@BeanId("apple") Fruit apple) {
		this.apple = apple;
	}

	public void eat() {
		System.out.println(orange.get());
		System.out.println(apple.get());
		System.out.println(banana.get());
		// System.out.println(waterMellon.get());
	}
}
