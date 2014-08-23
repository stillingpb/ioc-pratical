package test.bean.cycle;

import ioc.annotation.Component;

import javax.inject.Inject;
import javax.inject.Provider;

@Component
public class CycleTwoProvider {
	@Inject
	public Provider<CycleOneProvider> one2;

	public String toString() {
		return "cycle two provider instance";
	}
}
