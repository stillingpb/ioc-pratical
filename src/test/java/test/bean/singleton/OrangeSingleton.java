package test.bean.singleton;

import ioc.annotation.Component;

import javax.inject.Singleton;

@Singleton
@Component
public class OrangeSingleton {
	public String get(){
		return "orange singleton";
	}
}
