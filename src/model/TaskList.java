package model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class TaskList {

	@XStreamAlias("TaskList")
	private ArrayList<Task> tasks;
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
	}
	
	public void editTaskDescription(int taskIndex, String description) {
		this.tasks.get(taskIndex).setDescription(description);
	}
	
	public void deleteFromList(Integer taskIndex) {
		this.tasks.remove(taskIndex);
		this.totalTasks--;
	}
	
	public void deleteFromList(List<Integer> taskIndexList) {
		for (int i=taskIndexList.size()-1; i>=0; i--) {
			int indexToRemove = taskIndexList.get(i);
			this.tasks.remove(indexToRemove);
		}
		this.totalTasks = 0;
	}
	
	public void clearList() {
		this.tasks.clear();
		this.totalTasks = 0;
	}

	public void markTaskDone(Integer taskIndex) {
		this.tasks.get(taskIndex).markDone();
    }

	public void markTaskDone(List<Integer> taskIndexList) {
		for (int i=0; i<taskIndexList.size(); i++) {
			int taskToMarkDone = taskIndexList.get(i);
			this.tasks.get(taskToMarkDone).markDone();
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
