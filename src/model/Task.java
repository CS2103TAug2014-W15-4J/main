package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import exception.TaskDoneException;
import exception.TaskInvalidDateException;
import exception.TaskTagDuplicateException;
import exception.TaskTagException;

/**
 *  This class models a Task object
 */

public abstract class Task {
	public enum Type {
		FLOAT, DEADLINE, FIXED, REPEATED
	}
	protected String description;
	protected List<String> tags;
	protected boolean isDone;
	protected Type taskType;
	protected Date addedTime;
	protected Date doneDate;
	
	public Task(String description) {
		this.description = description;
		this.isDone = false;
		this.tags = new ArrayList<String>();
		this.addedTime = new Date(System.currentTimeMillis());
	}
	
	public void setDescription(String desc) {
		this.description = desc;
	}

	public String getDescription() {
		return description;
	}
	
	public List<String> getTags() {
		return tags;
	}
	
	public void addTag(String tag) throws TaskTagDuplicateException {
	    if (tags.contains(tag.toLowerCase())) {
	        throw new TaskTagDuplicateException();
	        
	    } else {
	        tags.add(tag);
	    }
	}
	
	public void deleteTag(String tag) throws TaskTagException {
	    if (tags.contains(tag)) {
	        tags.remove(tag);
	        
	    } else {
	        throw new TaskTagException();
	    }
	}
	
	public Task markDone() throws TaskDoneException {
		isDone = true;
	    doneDate = new Date(System.currentTimeMillis());
		return null;
	}
	
	public boolean getIsDone() {
		return this.isDone;
	}

	public void setDeadline(Date time) throws TaskInvalidDateException {
	    // sub class override for repeated / fixed / deadline tasks
	    // else trying to add date to otherwise non date task, error
	    
	    throw new TaskInvalidDateException();
	    
    }

	public Date getDeadline() throws TaskInvalidDateException {
	    // sub class override, 
	    // else exception
	    
	    throw new TaskInvalidDateException(); 
    }	
	
	public Date getAddedTime() {
		return this.addedTime;
    }

	public Date getDoneDate() {
	    // TODO Auto-generated method stub
	    return null;
    }
	
	public Type getType() {
		return taskType;
	}

	public void setStartTime(Date startDate) {
	    // TODO Auto-generated method stub
	    
    }
	
	@Override
    public abstract String toString();

}
