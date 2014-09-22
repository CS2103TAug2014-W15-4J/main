package model;

import java.util.Date;

public class DeadlineTask extends Task {
	private Date deadline;

	public DeadlineTask(String description, Date dl) {
		super(description);
		deadline = dl;
	}
	
	public String getDeadline() {
		return null;
	}

}
