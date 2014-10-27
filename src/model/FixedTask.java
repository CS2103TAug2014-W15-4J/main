package model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class FixedTask extends Task {
	
	@XStreamAlias("StartTime")
	private Date startTime;
	@XStreamAlias("EndTime")
	private Date endTime;
	
	public FixedTask(String description, Date startTime, Date endTime) {
		super(description);
		// TODO Auto-generated constructor stub
		this.startTime = startTime;
		this.endTime = endTime;
		this.taskType = Type.FIXED;
	}

	public Date getStartTime() {
		return startTime;
	}

	@Override
    public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Override
    public Date getDeadline() {
		return endTime;
	}
	
	@Override
    public void setDeadline(Date endtime) {
		this.endTime = endtime;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
		return this.description + "\nStart: "+ dateFormatter.format(this.startTime) + "\nEnd: " +
				dateFormatter.format(this.endTime) + "\n" + this.displayTags() 
					+ "\n" + this.displayDone();
	}
	
    @Override
    public Task clone() {
        
        FixedTask newTask = new FixedTask(this.description, startTime, endTime);
        newTask.addedTime = this.addedTime;
        newTask.tags = new ArrayList<String>(this.tags);
        newTask.isDone = this.isDone;
        
        return newTask;
    }   

}
