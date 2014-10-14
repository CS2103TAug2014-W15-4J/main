package model;

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
	private Date doneDate;

	public static String[] namesOfDays =  {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

	private void updatePeriodString() {
	    
	    Date time = deadline;
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(time);
	    
	    if (repeatPeriod == RepeatDate.DAILY) {
	        period = "daily";
	        
	    } else if (repeatPeriod == RepeatDate.WEEKLY) {
	        period = namesOfDays[cal.get(Calendar.DAY_OF_WEEK)];
	        
	    } else if (repeatPeriod == RepeatDate.MONTHLY) {
	        period = "day " + cal.get(Calendar.DAY_OF_MONTH) + " of the month";
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
	
	private void setDoneDate() {
		doneDate = new Date(System.currentTimeMillis());
	}
	
	@Override
	public Date getDoneDate() {
		return doneDate;
	}

	@Override
    public Task markDone() throws TaskDoneException {
		if (!this.getIsDone()) {
            Task taskToRepeat = new RepeatedTask(this.description,
                                                 this.deadline,
                                                 this.repeatPeriod);
		    this.setDoneDate();
		    super.markDone();
		    return taskToRepeat;
		    
		} else {
			throw new TaskDoneException();
		}
    }

}
