package model;

import java.util.ArrayList;

public class FloatingTask extends Task {

	public FloatingTask(String description) {
		super(description);
		this.taskType = Type.FLOAT;
	}

	//@author A0119446B
	@Override
	public String toString() {
		return this.description + "\n" +this.displayTags() + "\n" + this.displayDone();
	}
	
    @Override
    public Task clone() {
        FloatingTask newTask = new FloatingTask(this.description);
        newTask.addedTime = this.addedTime;
        newTask.tags = new ArrayList<String>(this.tags);
        newTask.isDone = this.isDone;
        
        return newTask;
    }

}

