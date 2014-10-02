package model;

import java.util.concurrent.TimeUnit;

public class RepeatedTask extends Task {
	
	private TimeUnit period;

	public RepeatedTask(String description, TimeUnit repeatPeriod) {
		super(description);
		period = repeatPeriod;
	}
	
	public String getRepeatPeriod() {
		return null;
	}

	@Override
    public Task markDone() {
	    super.markDone();
	    Task taskToRepeat = new RepeatedTask(this.getDescription(), this.period);
	    return taskToRepeat;
    }

}
