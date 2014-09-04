ioc-pratical
============

该分支使用id号来标识每个需要注入的类，不再支持jsr-350

使用示例

@Component("people")
@Singleton
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
}

public interface Fruit {
	public String get();
}

@Component("apple")
public class Apple implements Fruit {
}

@Component("orange")
public class Orange implements Fruit {
}

@Component("banana")
public class Banana implements Fruit {
}
