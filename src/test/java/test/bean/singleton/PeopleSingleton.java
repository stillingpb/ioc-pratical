package test.bean.singleton;

import ioc.annotation.BeanId;
import ioc.annotation.Component;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Component("peopleSingleton")
public class PeopleSingleton {
	@Inject
	@BeanId("orangeSingleton")
	public OrangeSingleton orange;

	public void eat() {
		System.out.println(orange.get());
	}
}
