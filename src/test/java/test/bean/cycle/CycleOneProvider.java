package test.bean.cycle;

import ioc.annotation.Component;

import javax.inject.Inject;

@Component
public class CycleOneProvider {
	@Inject
	public CycleTwoProvider two;

	public String toString() {
		return "cycle one provider instance";
	}
}
