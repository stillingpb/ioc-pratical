package ioc.data;

import java.util.List;

public interface InjectPoint {
	public void inject(Object instance, Object... params);

	public List<BeanIdentifier> getDependencies();
}
