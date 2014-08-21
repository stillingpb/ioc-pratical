package test.bean;

import ioc.annotation.Component;

import javax.inject.Inject;
import javax.inject.Named;

@Component
public class People {

	@Inject
	@Named("orange")
	Fruit orange;

	Fruit apple;

	Fruit banana;

	@Inject
	public People(@Named("banana") Fruit banana) {
		this.banana = banana;
	}

	@Inject
	public void setApple(@Named("apple") Fruit apple) {
		this.apple = apple;
	}

	public void eat() {
		System.out.println(banana.get());
		System.out.println(orange.get());
		System.out.println(apple.get());
	}
}
