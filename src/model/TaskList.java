package model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class TaskList {

	@XStreamAlias("TaskList")
	private ArrayList<Task> tasks;

	public TaskList() {
		this.tasks = new ArrayList<Task>();
	}
	
	public ArrayList<Task> getList() {
		return this.tasks;
	}
	
	public void addToList(Task task) {
		this.tasks.add(task);
	}
	
	public void editTaskDescription(int taskIndex, String description) {
		this.tasks.get(taskIndex).setDescription(description);
	}
	
	public void deleteFromList(Integer taskIndex) {
		this.tasks.remove(taskIndex);
	}
	
	public void deleteFromList(List<Integer> taskIndex) {
		for (int i=taskIndex.size()-1; i>=0; i--) {
			int indextoRemove = taskIndex.get(0);
			this.tasks.remove(indextoRemove);
		}
	}
	
	public void clearList() {
		this.tasks.clear();
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
