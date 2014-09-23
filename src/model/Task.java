package model;

/**
 *  This class models a (by default, floating) Task object
 */

public class Task {

	private String description;
	private String[] tags;
	private boolean done;

	public Task(String description) {
		this.description = description;
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
	
	

}
