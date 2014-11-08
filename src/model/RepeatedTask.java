package model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import controller.UserInput.RepeatDate;
import exception.TaskDoneException;
import exception.TaskInvalidDateException;

//@author A0115384H
/**
 * This class models a repeated task: tasks that recur after a period of time
 */
public class RepeatedTask extends Task {
	@XStreamAlias("Deadline")
	private Date deadline;
	@XStreamAlias("Next")
	private Date next;
	@XStreamAlias("RepeatDate")
	private RepeatDate repeatPeriod;
	@XStreamAlias("Period")
	private String period;

	public static String[] namesOfDays =  {"DUMMY", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};

	/**
	 * This method updates the string representation of the repeatPeriod -- period. 
	 */
	private void updatePeriodString() {
	    
	    Date time = deadline;
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(time);
	    
	    if (repeatPeriod == RepeatDate.DAILY) {
	    	cal.add(Calendar.DATE, 1);
	    	next = cal.getTime();
	        period = "daily";
	    } else if (repeatPeriod == RepeatDate.WEEKLY) {
	        period = "every " + namesOfDays[cal.get(Calendar.DAY_OF_WEEK)];
	        cal.add(Calendar.DATE, 7);
	    	next = cal.getTime();
	    } else if (repeatPeriod == RepeatDate.MONTHLY) {
	        period = "day " + cal.get(Calendar.DAY_OF_MONTH) + " of each month";
	        cal.add(Calendar.MONTH, 1);
	    	next = cal.getTime();
	    }
	}

	/**
	 * This constructor creates a new RepeatedTask object,
	 * with the specified description, time, and repeat period.
	 * 
	 * @param description  The description of the repeated task.
	 * @param time         The due date of the repeated task.
	 * @param repeatDate   The period after which the task repeats.
	 */
	public RepeatedTask(String description, Date time, RepeatDate repeatDate) {
	    super(description);
	    deadline = time;
	    repeatPeriod = repeatDate;
	    taskType = Task.Type.REPEATED;
	    
	    updatePeriodString();
    }

    /**
     * This method returns the due date of the repeated task.
     * 
     * @return The Date object representing the due date of the task.
     */
	@Override
    public Date getDeadline() {
		return deadline;
	}
	
	/**
	 * This method returns the repeat period of the repeated task.
	 * 
	 * @return The String representation of the repeat period.
	 */
	public String getRepeatPeriod() {
		return period;
	}
	
	/**
	 * This method returns the next due date occurrence of the repeated task.
	 * 
	 * @return The Date object representing the next due date of the repeated task.
	 */
	public Date getNext() {
	    return next;
	}
	
    /**
     * This method sets a new due date for the repeated task.
     * 
     * @param time The Date object representing the new due date.
     */
	@Override
    public void setDeadline(Date dl) {
		deadline = dl;
		updatePeriodString();
	}
	
    /**
     * This method sets a new repeat period for the repeated task.
     * 
     * @param repeatP   The RepeatDate object representing the new repeat period.
     */
	public void setRepeatPeriod(RepeatDate repeatP) {
		repeatPeriod = repeatP;
		updatePeriodString();
	}
	
	/**
	 * This method marks done the current task, 
	 * and creates a new repeated task for the next occurrence.
	 * 
	 * @return The RepeatedTask object newly created.
	 * @throws TaskDoneException   if task is already done.
	 */
	@Override
    public Task markDone() throws TaskDoneException {
		if (!this.getIsDone()) {
            Task taskToRepeat = new RepeatedTask(this.description,
                                                 this.next,
                                                 this.repeatPeriod);
		    super.markDone();
		    return taskToRepeat;
		    
		} else {
			throw new TaskDoneException();
		}
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
		return this.description +"\nDeadline: " + dateFormatter.format(this.deadline) + 
			   "\nRepeat: " + this.period + "\n" +this.displayTags() + "\n" + this.displayDone();
	}

	//@author A0115384H
    /**
     * This method creates a copy of the RepeatedTask object 
     * (same values for fields, but different references).
     * This method supports the undo/redo functionality.
     * 
     * @return The RepeatedTask object newly created.
     */
    @Override
    public Task clone() {
        RepeatedTask newTask = new RepeatedTask(this.description, this.deadline, this.repeatPeriod);
        newTask.addedTime = this.addedTime;
        newTask.tags = new ArrayList<String>(this.tags);
        newTask.isDone = this.isDone;
        newTask.next = this.next;
        
        return newTask;
    }   
    
    /**
     * This method compares two RepeatedTask objects, and determines if one comes after another.
     * 
     * @param rt2   The RepeatedTask object to be compared to.
     * @return      true if both RepeatedTask objects occurs consecutively.
     */
    public boolean isConsecutiveTasks(RepeatedTask rt2) {
        
        boolean isConsecutive = (this.getRepeatPeriod().equals(rt2.getRepeatPeriod())) &&
                                ((this.getDeadline() == rt2.getNext()) || (this.getNext() == rt2.getDeadline())) &&
                                (this.getDescription().equals(rt2.getDescription())) &&
                                (this.getTags().equals(rt2.getTags())) &&
                                (this.getAddedTime() != rt2.getAddedTime());
        return isConsecutive;

        
    }
    
    /**
     * This method checks if two repeated tasks are equal.
     * Two repeated tasks are equal if they have the same value for different properties:
     * Added time, Description, Class, Deadline, Repeat period
     * 
     * @param task The repeated task to be compared to.
     * @return     true if the tasks are equal, false otherwise.
     */
    @Override
    public boolean equals(Task task) {
        boolean isEqual = true;
        try {
            isEqual = super.equals(task) &&
                      this.getDeadline().equals(task.getDeadline()) &&
                      this.getRepeatPeriod().equals(((RepeatedTask) task).getRepeatPeriod());
        } catch (TaskInvalidDateException e) {
            return false;
        }
        return isEqual;
    }
    
    @Override
    public int getReminingDays() {
    	return this.getDateDiff(new Date(), deadline);
    }


}
