package test.bean.singleton;

import ioc.annotation.Component;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Component
@Resource(name = "peopleSingleton")
public class PeopleSingleton {
	@Inject
	@Resource(name = "orangeSingleton")
	public OrangeSingleton orange;

	public void eat() {
		System.out.println(orange.get());
	}
}
