package test.bean.cycle;

import ioc.annotation.Component;

import javax.inject.Inject;

@Component
public class CycleOne {

	CycleTwo cycleTwo;

	@Inject
	public void setCycleTwo(CycleTwo cycleTwo) {
		this.cycleTwo = cycleTwo;
	}
}
