package ioc;

import ioc.data.BeanData;
import ioc.util.BeanDataLoaderException;

public interface BeanDataLoader {
	/**
	 * 获取单个java bean的 beanData
	 * 
	 * @param identifier id号
	 * @return
	 * @throws BeanDataLoaderException
	 */
	public BeanData getBeanData(String identifier) throws BeanDataLoaderException;

	/**
	 * 一次加载所有的bean数据，这样每次加载单个beanData时只需要查找出来即可
	 * 
	 * @throws BeanDataLoaderException
	 */
	public void loadAllBeanData() throws BeanDataLoaderException;
}
