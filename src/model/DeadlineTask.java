package model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import exception.TaskInvalidDateException;

//@author A0115384H
/**
 * This class models a deadline task: tasks to be done by a specified time.
 */
public class DeadlineTask extends Task {
	@XStreamAlias("Deadline")
	private Date deadline;

	/**
	 * This constructor creates a new DeadlineTask object, 
	 * with the specified description and deadline.
	 *  
	 * @param description  The description of the deadline task.
	 * @param dl           The due date of the deadline task.
	 */
	public DeadlineTask(String description, Date dl) {
		super(description);
		deadline = dl;
		this.taskType = Type.DEADLINE;
	}
	
	/**
	 * This method returns the due date of the deadline task.
	 * 
	 * @return The Date object representing the due date of the task.
	 */
	@Override
	public Date getDeadline() {
		return deadline;
	}
	
	/**
	 * This method sets a new due date for the deadline task.
	 * 
     * @param time The Date object representing the new due date.
	 */
	@Override
    public void setDeadline(Date dl) {
		deadline = dl;
	}

    //@author A0119446B
	/**
	 * This method returns a formatted string of the task information.
	 * 
	 * @return The String object representing the task display information.
	 */
	@Override
	public String toString() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
		return this.description + "\nDue: "+ dateFormatter.format(this.deadline) + 
		       "\n" + this.displayTags() + "\n" + this.displayDone();
	}
	
	//@author A0115384H
	/**
	 * This method creates a copy of the DeadlineTask object 
	 * (same values for fields, but different references).
	 * This method supports the undo/redo functionality.
	 * 
	 * @return The DeadlineTask object newly created.
	 */
	@Override
    public Task clone() {
	    DeadlineTask newTask = new DeadlineTask(this.description, this.deadline);
	    newTask.addedTime = this.addedTime;
        newTask.tags = new ArrayList<String>(this.tags);
	    newTask.isDone = this.isDone;
	    
	    return newTask;
	}	
	
    /**
     * This method checks if two deadline tasks are equal.
     * Two deadline tasks are equal if they have the same value for different properties:
     * Added time, Description, Class, Deadline
     * 
     * @param task The deadline task to be compared to.
     * @return     true if the tasks are equal, false otherwise.
     */
    @Override
    public boolean equals(Task task) {
        boolean isEqual = true;
        try {
            isEqual = super.equals(task) &&
                      this.getDeadline().equals(task.getDeadline());
        } catch (TaskInvalidDateException e) {
            return false;
        }
        return isEqual;
    }

	@Override
	public int getReminingDays() {
		return getDateDiff(new Date(), deadline);
	}
}
