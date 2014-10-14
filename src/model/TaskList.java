package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class TaskList {
	
	@XStreamAlias("TaskList")
	private ArrayList<Task> tasks;
	@XStreamAlias("TasksCount")
	private int totalTasks;

	public TaskList() {
		this.tasks = new ArrayList<Task>();
		this.totalTasks = this.tasks.size();
	}
	
	public ArrayList<Task> getList() {
		return this.tasks;
	}
	
	public Task get(int index) {
		return this.tasks.get(index);
	}
	
	public void addToList(Task task) {
		this.tasks.add(task);
		this.totalTasks++;
	}
	
	public void editTaskDescription(int taskIndex, String description) throws IndexInvalidException {
		if ((taskIndex > totalTasks) || (taskIndex <= 0)) {
			throw new IndexInvalidException("Error indedx for editing!");
		} else {
            this.tasks.get(taskIndex - 1).setDescription(description);
		}
	}
	
	public void editTaskDeadline(int taskIndex, Date time) throws IndexInvalidException {
		if ((taskIndex > totalTasks) || (taskIndex <= 0)) {
			throw new IndexInvalidException("Error indedx for editing!");
		} else {
            this.tasks.get(taskIndex - 1).setDeadline(time);
		}   
    }
	
	public void editTaskStartDate(int taskIndex, Date startDate) throws IndexInvalidException {
		if ((taskIndex > totalTasks) || (taskIndex <= 0)) {
			throw new IndexInvalidException("Error indedx for editing!");
		} else {
            this.tasks.get(taskIndex - 1).setStartTime(startDate);
		}
	    
    }
		
	/*
	public void editTaskRepeatPeriod(int taskIndex, String repeatPeriod) {
	    if ((taskIndex > totalTasks) || (taskIndex <= 0)) {
	    	// error here
	    } else {
	    	this.tasks.get(taskIndex-1).setRepeatPeriod(repeatPeriod);
	    }
	    
    }
    */


	
	public void deleteFromList(List<Integer> taskIndexList) throws IndexInvalidException {

		Collections.sort(taskIndexList);
        for (int i = taskIndexList.size() - 1; i >= 0; i--) {
			int indexToRemove = taskIndexList.get(i);
			
			if ((indexToRemove > totalTasks) || (indexToRemove <= 0)) {
				throw new IndexInvalidException("Error indedx for editing!");

			} else {
                this.tasks.remove(indexToRemove - 1);
				this.totalTasks--;
			}
		}
	}
	
	public void clearList() {
		this.tasks.clear();
		this.totalTasks = 0;
	}

	public void markTaskDone(List<Integer> taskIndexList) throws Exception {
		
		if (taskIndexList.isEmpty()) {
			throw new IndexInvalidException("Error indedx for editing!");
		} else {
            for (int i = 0; i < taskIndexList.size(); i++) {
				int taskToMarkDone = taskIndexList.get(i);
				
				if ((taskToMarkDone > totalTasks) || (taskToMarkDone <= 0)) {
					throw new IndexInvalidException("Error indedx for editing!");
					
				} else {
                    Task newRepeatTask = this.tasks.get(taskToMarkDone - 1).markDone();
					if (newRepeatTask != null) {
						this.addToList(newRepeatTask);
					}

				}
			}
		}

    }

	public int count() {
		return this.totalTasks;
	}
	
	class IndexInvalidException extends IndexOutOfBoundsException{
		public IndexInvalidException() {
		}
		public IndexInvalidException(String message) {
			super(message);
		}
	}
	
	@Override
	public String toString() {
		String output = "";
        int i = 1;
		for (Task task : this.tasks) {
            output += i;
            output += ": ";
            output += task.getDescription();
            output += "\n";
			i++;
		}
		return output;
	}

}