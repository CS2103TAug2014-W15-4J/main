package model;

import java.util.Date;

/**
 *  This class models a Task object
 */

public abstract class Task {
	public enum Type {
		Float, Deadline, Fixed, Repeated
	}
	protected String description;
	protected String[] tags;
	protected boolean isDone;
	protected Type taskType;
	
	public Task(String description) {
		this.description = description;
		this.isDone = false;
	}
	
	public void setDescription(String desc) {
		this.description = desc;
	}

	public String getDescription() {
		return description;
	}
	
	public String[] getTags() {
		return tags;
	}
	
	public Task markDone() {
		isDone = true;
		return null;
	}
	
	public boolean getIsDone() {
		return this.isDone;
	}

	public void setDeadline(Date time) {
	    // task override for repeated / fixed / deadline tasks
	    
    }
	
	public Type getType() {
		return taskType;
	}
	
	

}
