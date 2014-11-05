package model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

//@author A0115384H
/**
 * This class models a fixed task: tasks to be done within an interval.
 */
public class FixedTask extends Task {
	
	@XStreamAlias("StartTime")
	private Date startTime;
	@XStreamAlias("EndTime")
	private Date endTime;
	
	/**
	 * This constructor creates a new FixedTask object,
	 * with the specified description and times.
	 * 
	 * @param description  The description of the fixed task.
	 * @param startTime    The start time/date of the fixed task.
	 * @param endTime      The end time/date of the fixed task.
	 */
	public FixedTask(String description, Date startTime, Date endTime) {
		super(description);
		this.startTime = startTime;
		this.endTime = endTime;
		this.taskType = Type.FIXED;
	}

	/**
	 * This method returns the start time of the fixed task.
	 * 
	 * @return The Date object representing the start time of the fixed task.
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * This method sets a new start time for the fixed task.
	 * 
     * @param time The Date object representing the new start time.
	 */
	@Override
    public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * This method returns the due date of the fixed task.
	 * 
	 * @return The Date object representing the due date of the task.
	 */
	@Override
    public Date getDeadline() {
		return endTime;
	}
	
	/**
	 * This method sets a new end time for the fixed task.
	 * 
     * @param time The Date object representing the new end time.
	 */
	@Override
    public void setDeadline(Date endtime) {
		this.endTime = endtime;
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
		return this.description + "\nStart: "+ dateFormatter.format(this.startTime) + "\nEnd: " +
		       dateFormatter.format(this.endTime) + "\n" + this.displayTags() +
		       "\n" + this.displayDone();
	}
	
	//@author A0115384H
    /**
     * This method creates a copy of the FixedTask object 
     * (same values for fields, but different references).
     * This method supports the undo/redo functionality.
     * 
     * @return The FixedTask object newly created.
     */
	@Override
    public Task clone() {
        
        FixedTask newTask = new FixedTask(this.description, startTime, endTime);
        newTask.addedTime = this.addedTime;
        newTask.tags = new ArrayList<String>(this.tags);
        newTask.isDone = this.isDone;
        
        return newTask;
    }   
}
