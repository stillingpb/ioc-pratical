package test.bean.cycle;

import ioc.annotation.Component;

import javax.inject.Inject;

@Component
public class CycleTwo {
	@Inject
	CycleOne one;
}
