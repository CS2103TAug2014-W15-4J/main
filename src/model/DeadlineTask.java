package model;

import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class DeadlineTask extends Task {
	@XStreamAlias("Deadline")
	private Date deadline;

	public DeadlineTask(String description, Date dl) {
		super(description);
		deadline = dl;
	}
	
	public Date getDeadline() {
		return deadline;
	}
	
	public void setDeadline(Date dl) {
		deadline = dl;
	}

}
