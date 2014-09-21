package model;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class TaskList {

	@XStreamAlias("TaskList")
	private ArrayList<Task> tasks;

	public TaskList() {
		this.tasks = new ArrayList<>();
	}
	
	public ArrayList<Task> getList() {
		return this.tasks;
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
