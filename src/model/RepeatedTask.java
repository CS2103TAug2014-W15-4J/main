package model;

import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class RepeatedTask extends Task {
	@XStreamAlias("Deadline")
	private Date deadline;
	@XStreamAlias("Period")
	private String period;
	private Date doneDate;

	public RepeatedTask(String description, Date time, String repeatDate) {
	    super(description);
	    deadline = time;
	    period = repeatDate;
	    this.taskType = Type.REPEATED;
    }

	@Override
    public Date getDeadline() {
		return deadline;
	}
	
	public String getRepeatPeriod() {
		return period;
	}
	
	@Override
    public void setDeadline(Date dl) {
		deadline = dl;
	}
	
	public void setRepeatPeriod(String repeatPeriod) {
		period = repeatPeriod;
	}
	
	private void setDoneDate() {
		doneDate = new Date(System.currentTimeMillis());
	}
	
	@Override
	public Date getDoneDate() {
		return doneDate;
	}

	@Override
    public Task markDone() throws Exception {
		if (!this.getIsDone()) {
            Task taskToRepeat = new RepeatedTask(this.description,
                                                 this.deadline,
                                                 this.period);
		    this.setDoneDate();
		    super.markDone();
		    return taskToRepeat;
		    
		} else {
			throw new Exception();
		}
    }

}
