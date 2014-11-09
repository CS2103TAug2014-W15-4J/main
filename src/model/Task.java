package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import exception.TaskDoneException;
import exception.TaskInvalidDateException;
import exception.TaskTagDuplicateException;
import exception.TaskTagException;

/**
 *  This class models a Task object.
 */

public abstract class Task {
	protected static final String DATE_FORMAT = "EEE HH:mm dd/MMM/yyyy";

	public enum Type {
		FLOAT, DEADLINE, FIXED, REPEATED
	}
	
	protected String description;
	protected List<String> tags;
	protected boolean isDone;
	protected Type taskType;
	protected Date addedTime;
	protected Date doneDate;
	protected boolean isOverdue;
	
	//@author A0115384H
	/**
	 * This constructor creates a new Task object, with the specified description.
	 * 
	 * @param description  The description of the task.
	 */
	public Task(String description) {
		this.description = description;
		this.isDone = false;
		this.tags = new ArrayList<String>();
		this.addedTime = new Date(System.currentTimeMillis());
	}
	
	/**
	 * This method sets a new description for the task.
	 * 
	 * @param desc The new description of the task.
	 */
	public void setDescription(String desc) {
		this.description = desc;
	}

	/**
	 * This method returns the description of the task.
	 * 
	 * @return The String object representing the description of the task.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * This method returns the list of tags of the task.
	 * 
	 * @return The List<String> object representing the list of tags of the task. 
	 */
	public List<String> getTags() {
		return tags;
	}
	
	//@author A0119414L
	/**
	 * This method checks if the task is overdue.
	 * 
	 * @return	true if the task is overdue.
	 */
	public boolean getIsOverdue() {
		return isOverdue;
	}
	
	//@author A0115384H
	/**
	 * This method adds a tag to a task.
	 * 
	 * @param tag  The tag to be added to a task.
	 * @throws TaskTagDuplicateException   If task already has the specified tag.
	 */
	public void addTag(String tag) throws TaskTagDuplicateException {
	    if (tags.contains(tag.toLowerCase())) {
	        throw new TaskTagDuplicateException();
	        
	    } else {
	        tags.add(tag.toLowerCase());
	    }
	}
	
	/**
	 * This method removes a tag from a task.
	 * 
	 * @param tag  The tag to be removed from a task.
	 * @throws TaskTagException    If task does not have the specified tag.
	 */
	public void deleteTag(String tag) throws TaskTagException {
	    if (tags.contains(tag.toLowerCase())) {
	        tags.remove(tag.toLowerCase());
	        
	    } else {
	        throw new TaskTagException();
	    }
	}
	
	/**
	 * This method marks a task as done.
	 * 
	 * @return The newly created RepeatedTask if the task is a repeated task, null otherwise.
	 * @throws TaskDoneException   If task is already done.
	 */
	public Task markDone() throws TaskDoneException {
	    if (!isDone) {
    		isDone = true;
    	    doneDate = new Date(System.currentTimeMillis());
    		return null;
	    } else {
	        throw new TaskDoneException();
	    }
	}
	
	/**
	 * This method marks a task as done.
	 * This method supports the undo/redo functionality.
	 */
	public void markRedone() {
	    if (isDone) {
	        assert false;
	    } else {
	        isDone = true;
	    }
	}
	
    /**
     * This method marks a task as undone.
     * This method supports the undo/redo functionality.
     */
	public void markUndone() {
	    if (!isDone) {
	        assert false;
	    } else {
	        isDone = false;
	    }
	}
	
	/**
	 * This method checks if a task is done.
	 * 
	 * @return true if the task is done, false otherwise.
	 */
	public boolean getIsDone() {
		return this.isDone;
	}

	/**
	 * This method sets a new due date for the task.
	 * 
	 * @param time The Date object representing the new due date.
	 * @throws TaskInvalidDateException    If a floating task tries to call this method.
	 */
	public void setDeadline(Date time) throws TaskInvalidDateException {
	    // sub class override for repeated / fixed / deadline tasks
	    // else trying to add date to otherwise non date task, error
	    
	    throw new TaskInvalidDateException();
    }

	/**
	 * This method returns the due date of the task.
	 * 
	 * @return The Date object representing the due date of the task.
	 * @throws TaskInvalidDateException    If a floating task tries to call this method.
	 */
	public Date getDeadline() throws TaskInvalidDateException {
	    // sub class override, 
	    // else exception
	    
	    throw new TaskInvalidDateException(); 
    }	
	
	//@author A0119446B
	/**
	 * This method returns the added time of the task.
	 * 
	 * @return The Date object representing the time the task was added.
	 */
	public Date getAddedTime() {
		return this.addedTime;
    }

    /**
     * This method returns the done time of the task.
     * 
     * @return The Date object representing the time the task was done.
     */
	public Date getDoneDate() {
	    return this.doneDate;
    }
	
    /**
     * This method returns the type of the task
     * 
     * @return The Type object representing the task type.
     */
	public Type getType() {
		return taskType;
	}

	//@author A0115384H
    /**
     * This method sets a new start time for the (fixed) task.
     * 
     * @param time The Date object representing the new start time.
     */
	public void setStartTime(Date startDate) {
	    
    }
	
	//@author A0119446B
	/**
     * This method return the days remain to the due date.
     * 
     * To be override by subclass.
     * 
     * @return 	an integer represent how many days left for doing task
     */
	public int getReminingDays() {
		return 0;
    }
	
	//@author A0119446B
	/**
	 * This method calculates two days' difference (Unit: day)
	 * 
	 * @param date1 the old date
	 * @param date2 the new date
	 * @return the difference value, in days
	 */
	public int getDateDiff(Date date1, Date date2) {
		return Days.daysBetween(new DateTime(date1).toLocalDate(), new DateTime(date2).toLocalDate()).getDays();
	}
	
	/**
	 * This method represents the tags of the task in a string, and returns it.
	 * 
	 * @return The string representation of the task tags.
	 */
	public String displayTags() {
		StringBuilder output = new StringBuilder();
		if (this.tags.isEmpty()) {
			output.append("Tags: None");
        } else {
            String tagDisplay = "";
            for (int j = 0; j < tags.size(); j++) {
                if (j == 0) {
                    tagDisplay = tags.get(0);
                } else {
                    tagDisplay += ", " + tags.get(j);
                }
            }

            output.append("Tags: " + tagDisplay);
        }
		return output.toString();
	}
	
	/**
	 * This method returns the status of the task in a string representation.
	 * 
	 * @return The string representation of the task status.
	 */
	public String displayDone() {
		if (this.isDone) {
			return "Status: Done";
		} else if (this.isOverdue) {
			return "Status: OVERDUE!!!";
		} else {
			return "Status: Ongoing";
		}
	}
	
	/**
	 * This method checks if the task is overdue.
	 * 
	 * @throws TaskInvalidDateException    if task being checked is a floating task.
	 */
	public void checkOverdue() throws TaskInvalidDateException {
		Date now = new Date();
		if (now.after(getDeadline())) {
			this.isOverdue = true;
		} else {
			this.isOverdue = false;
		}
	}
	
    /**
     * This abstract method returns a formatted string of the task information.
     * 
     * @return The String object representing the task display information.
     */
	@Override
    public abstract String toString();
	
	//@author A0115384H
    /**
     * This abstract method creates a copy of the task object 
     * (same values for fields, but different references).
     * This method supports the undo/redo functionality.
     * 
     * @return The task object newly created.
     */
	@Override
    public abstract Task clone();
	
	/**
	 * This method checks if two tasks are equal.
	 * Two tasks are equal if they have the same value for different properties:
	 * Added time, Description, Class, Tags, ..
	 * Subclass override for subclasses with more properties.
	 * 
	 * @param task The task to be compared to.
	 * @return     true if the tasks are equal, false otherwise.
	 */
	public boolean equals(Task task) {
	    boolean isEqual = this.getAddedTime().equals(task.getAddedTime()) &&
	                      this.getClass() == task.getClass() &&
	                      this.getDescription().equals(task.getDescription()) &&
	                      this.getTags().equals(task.getTags());	    
	    return isEqual;
	}

}
