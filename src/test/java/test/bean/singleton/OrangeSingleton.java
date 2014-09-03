package test.bean.singleton;

import ioc.annotation.Component;

import javax.annotation.Resource;
import javax.inject.Singleton;

@Singleton
@Component
@Resource(name = "orangeSingleton")
public class OrangeSingleton {
	public String get(){
		return "orange singleton";
	}
}
