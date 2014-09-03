package test.bean;

import ioc.annotation.Component;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

@Component
@Resource(name = "people")
public class People {

	@Inject
	@Resource(name = "orange")
	Fruit orange;

	@Inject
	@Resource(name = "apple")
	Fruit apple;

	// @Inject
	// @Resource(name = "banana")
	// Fruit banana;
	//
	// @Inject
	// @Resource(name = "waterMellon")
	// Fruit waterMellon;

	public People() {
	}

	public void setApple(Fruit apple) {
		this.apple = apple;
	}

	public void eat() {
		System.out.println(orange.get());
		System.out.println(apple.get());
		// System.out.println(banana.get());
		// System.out.println(waterMellon.get());
	}
}
