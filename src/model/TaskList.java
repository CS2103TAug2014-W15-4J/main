package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class TaskList {

	@XStreamAlias("TaskList")
	private ArrayList<Task> tasks;
	@XStreamAlias("TasksCount")
	private int totalTasks;

	public TaskList() {
		this.tasks = new ArrayList<Task>();
	}
	
	public ArrayList<Task> getList() {
		return this.tasks;
	}
	
	public void addToList(Task task) {
		this.tasks.add(task);
		this.totalTasks++;
		System.out.println("task added");
	}
	
	public void editTaskDescription(int taskIndex, String description) {
		if ((taskIndex > totalTasks) || (taskIndex <= 0)) {
			System.out.println("no such task index: " + taskIndex);
		} else {
			this.tasks.get(taskIndex-1).setDescription(description);
			System.out.println("task description changed");
		}
	}
		
	public void deleteFromList(List<Integer> taskIndexList) {
		boolean isDeleted = false;
		Collections.sort(taskIndexList);
		for (int i=taskIndexList.size()-1; i>=0; i--) {
			int indexToRemove = taskIndexList.get(i);
			
			if ((indexToRemove > totalTasks) || (indexToRemove <= 0)) {
				System.out.println("no such task index: " + indexToRemove);
			} else {
				this.tasks.remove(indexToRemove-1);
				this.totalTasks--;
				isDeleted = true;
			}
		}
		
		// display feedback message when deleted successfully
		if (isDeleted) {
			System.out.println("task deleted.");
		}
	}
	
	public void clearList() {
		this.tasks.clear();
		this.totalTasks = 0;
	}

	public void markTaskDone(List<Integer> taskIndexList) {
		boolean isDone = false;
		for (int i=0; i<taskIndexList.size(); i++) {
			int taskToMarkDone = taskIndexList.get(i);
			
			if ((taskToMarkDone > totalTasks) || (taskToMarkDone <= 0)) {
				System.out.println("no such task index: " + taskToMarkDone);
			} else {
				this.tasks.get(taskToMarkDone-1).markDone();
				isDone = true;
			}
		}
		
		// display feedback message when done successfully
		if (isDone) {
			System.out.println("task marked done.");
		}
    }

	public int getNumberOfTasks() {
		return this.totalTasks;
	}
	
	@Override
	public String toString() {
		String output = "";
		int i=1;
		for (Task task : this.tasks) {
			output+=i;
			output+=": ";
			output+= task.getDescription();
			output+="\n";
			i++;
		}
		return output;
	}
	
	public void test() {
		for (int i=0;i<50;i++) {
			this.tasks.add(new Task("No."+i));
		}
	}

}
