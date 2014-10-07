package model;

import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class FixedTask extends Task {
	
	@XStreamAlias("Star Time")
	private Date startTime;
	@XStreamAlias("End Time")
	private Date endTime;
	
	public FixedTask(String description, Date startTime, Date endTime) {
		super(description);
		// TODO Auto-generated constructor stub
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	


}
