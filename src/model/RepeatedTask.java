package model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import controller.UserInput.RepeatDate;
import exception.TaskDoneException;

public class RepeatedTask extends Task {
	@XStreamAlias("Deadline")
	private Date deadline;
	@XStreamAlias("Period")
	private RepeatDate repeatPeriod;
	private String period;

	public static String[] namesOfDays =  {"DUMMY", "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

	private void updatePeriodString() {
	    
	    Date time = deadline;
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(time);
	    
	    if (repeatPeriod == RepeatDate.DAILY) {
	        period = "daily";
	        
	    } else if (repeatPeriod == RepeatDate.WEEKLY) {
	        period = "every" + namesOfDays[cal.get(Calendar.DAY_OF_WEEK)];
	        
	    } else if (repeatPeriod == RepeatDate.MONTHLY) {
	        period = "day " + cal.get(Calendar.DAY_OF_MONTH) + " of each month";
	    }
	}
	public RepeatedTask(String description, Date time, RepeatDate repeatDate) {
	    super(description);
	    deadline = time;
	    repeatPeriod = repeatDate;
	    
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
	}
	
	public void setRepeatPeriod(RepeatDate repeatP) {
		repeatPeriod = repeatP;
		updatePeriodString();
	}
	
	@Override
    public Task markDone() throws TaskDoneException {
		if (!this.getIsDone()) {
            Task taskToRepeat = new RepeatedTask(this.description,
                                                 this.deadline,
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
				"\nRepeat: " + this.repeatPeriod + "\n" +this.displayTags() + "\n" + this.displayDone();
	}

}
