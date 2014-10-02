package model;

/**
 *  This class models a Task object
 */

public abstract class Task {

	private String description;
	private String[] tags;
	private boolean isDone;

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
	
	

}
