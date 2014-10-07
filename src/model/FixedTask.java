package model;

import java.util.Date;

import model.Task.Type;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class FixedTask extends Task {
	
	@XStreamAlias("StartTime")
	private Date startTime;
	@XStreamAlias("EndTime")
	private Date endTime;
	
	public FixedTask(String description, Date startTime, Date endTime) {
		super(description);
		// TODO Auto-generated constructor stub
		this.startTime = startTime;
		this.endTime = endTime;
		this.taskType = Type.FIXED;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getDeadline() {
		return endTime;
	}
	
	public void setDeadline(Date endtime) {
		this.endTime = endtime;
	}

}
