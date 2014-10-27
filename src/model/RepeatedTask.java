package model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import controller.UserInput.RepeatDate;
import exception.TaskDoneException;

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
	
	public RepeatedTask(String description, Date time, RepeatDate repeatDate) {
	    super(description);
	    deadline = time;
	    repeatPeriod = repeatDate;
	    taskType = Task.Type.REPEATED;
	    
	    updatePeriodString();
    }

	@Override
    public Date getDeadline() {
		return deadline;
	}
	
	public String getRepeatPeriod() {
		return period;
	}
	
	@Override
    public void setDeadline(Date dl) {
		deadline = dl;
		updatePeriodString();
	}
	
	public void setRepeatPeriod(RepeatDate repeatP) {
		repeatPeriod = repeatP;
		updatePeriodString();
	}
	
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
	@Override
	public String toString() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
		return this.description +"\nDeadline: " + dateFormatter.format(this.deadline) + 
				"\nRepeat: " + this.period + "\n" +this.displayTags() + "\n" + this.displayDone();
	}
	
    @Override
    public Task clone() {
        RepeatedTask newTask = new RepeatedTask(this.description, this.deadline, this.repeatPeriod);
        newTask.addedTime = this.addedTime;
        newTask.tags = new ArrayList<String>(this.tags);
        newTask.isDone = this.isDone;
        newTask.next = this.next;
        
        return newTask;
    }   


}
