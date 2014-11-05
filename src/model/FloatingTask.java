package model;

import java.util.ArrayList;

//@author A0115384H
/**
 * This class models a floating task: tasks that can be done at any time.
 */
public class FloatingTask extends Task {

    /**
     * This constructor creates a new Floating Task object,
     * with the specified description.
     * 
     * @param description   The description of the floating task.
     */
	public FloatingTask(String description) {
		super(description);
		this.taskType = Type.FLOAT;
	}

	//@author A0119446B
	/**
	 * This method returns a formatted string of the task information.
	 * 
	 * @return The String object representing the task display information.
	 */
	@Override
	public String toString() {
		return this.description + "\n" +this.displayTags() + "\n" + this.displayDone();
	}
	
	/**
	 * This method creates a copy of the FloatingTask object
	 * (same values for fields, but different references).
	 * This method supports the undo/redo functionality.
	 * 
	 * @return The FloatingTask object newly created.
	 */
    @Override
    public Task clone() {
        FloatingTask newTask = new FloatingTask(this.description);
        newTask.addedTime = this.addedTime;
        newTask.tags = new ArrayList<String>(this.tags);
        newTask.isDone = this.isDone;
        
        return newTask;
    }
}

