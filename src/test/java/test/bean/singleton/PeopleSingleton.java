package test.bean.singleton;

import ioc.annotation.Component;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Component
public class PeopleSingleton {
	@Inject
	public OrangeSingleton orange;

	public void eat() {
		System.out.println(orange.get());
	}
}
