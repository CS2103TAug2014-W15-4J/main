package model;

import java.util.Date;

/**
 *  This class models a Task object
 */

public abstract class Task {

	protected String description;
	protected String[] tags;
	protected boolean isDone;

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

	public Date getDeadline() {
	    // TODO Auto-generated method stub
		return null;
	    
    }

	public Date getDoneDate() {
	    // TODO Auto-generated method stub
	    return null;
    }
	
	

}
