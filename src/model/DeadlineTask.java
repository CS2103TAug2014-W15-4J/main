package model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class DeadlineTask extends Task {
	@XStreamAlias("Deadline")
	private Date deadline;

	public DeadlineTask(String description, Date dl) {
		super(description);
		deadline = dl;
		this.taskType = Type.DEADLINE;
	}
	
	@Override
	public Date getDeadline() {
		return deadline;
	}
	
	@Override
    public void setDeadline(Date dl) {
		deadline = dl;
	}

	@Override
	public String toString() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
		return this.description + "\nDue: "+ dateFormatter.format(this.deadline) 
				+ "\n" +this.displayTags() + "\n" + this.displayDone();
	}
	

	@Override
    public Task clone() {
	    DeadlineTask newTask = new DeadlineTask(this.description, this.deadline);
	    newTask.addedTime = this.addedTime;
        newTask.tags = new ArrayList<String>(this.tags);
	    newTask.isDone = this.isDone;
	    
	    return newTask;
	}	
}
