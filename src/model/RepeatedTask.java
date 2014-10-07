package model;

import java.util.Date;

public class RepeatedTask extends Task {
	
	private Date deadline;
	private String period;

	public RepeatedTask(String description, Date time, String repeatDate) {
	    super(description);
	    deadline = time;
	    period = repeatDate;
    }

	public Date getDeadline() {
		return deadline;
	}
	
	public String getRepeatPeriod() {
		return period;
	}
	
	public void setDeadline(Date dl) {
		deadline = dl;
	}
	public void setRepeatPeriod(String repeatPeriod) {
		period = repeatPeriod;
	}

	@Override
    public Task markDone() {
	    Task taskToRepeat = new RepeatedTask(this.description, this.deadline, this.period);
	    super.markDone();
	    return taskToRepeat;
    }

}
