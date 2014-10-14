package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import exception.TaskDoneException;
import exception.TaskInvalidDateException;
import exception.TaskInvalidIdException;
import exception.TaskTagDuplicateException;

public class TaskList {
    
    @XStreamAlias("TaskList")
    private ArrayList<Task> tasks;
    @XStreamAlias("TasksCount")
    private int totalTasks;
    private HashMap<String, List<Task>> tags;
    
    public TaskList() {
        this.tasks = new ArrayList<Task>();
        this.totalTasks = this.tasks.size();
        this.tags = new HashMap<String, List<Task>>();
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
    
    
    public void editTaskDescription(int taskIndex, String description) throws TaskInvalidIdException {
        if ((taskIndex > totalTasks) || (taskIndex <= 0)) {
            throw new TaskInvalidIdException("Error index for editing!");
            
        } else {
            this.tasks.get(taskIndex - 1).setDescription(description);
        }
    }
    
    
    public void editTaskDeadline(int taskIndex, Date time) throws TaskInvalidIdException, TaskInvalidDateException {
        if ((taskIndex > totalTasks) || (taskIndex <= 0)) {
            throw new TaskInvalidIdException("Error index for editing!");
            
        } else {
            this.tasks.get(taskIndex - 1).setDeadline(time);
        }   
    }
    
    public void editTaskStartDate(int taskIndex, Date startDate) throws TaskInvalidIdException {
        if ((taskIndex > totalTasks) || (taskIndex <= 0)) {
            throw new TaskInvalidIdException("Error index for editing!");
            
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
    
    
    
    
    public void deleteFromList(List<Integer> taskIndexList) throws TaskInvalidIdException {
        if (taskIndexList.isEmpty()) {
            throw new TaskInvalidIdException("nothing to delete");
            
        } else {
            Collections.sort(taskIndexList);
            for (int i = taskIndexList.size() - 1; i >= 0; i--) {
                int indexToRemove = taskIndexList.get(i);
                
                if ((indexToRemove > totalTasks) || (indexToRemove <= 0)) {
                    
                    throw new TaskInvalidIdException("Error index for editing!");
                    
                } else {
                    this.tasks.remove(indexToRemove - 1);
                    this.totalTasks--;
                }
            }
        }
    }
    
    public void clearList() {
        this.tasks.clear();
        this.totalTasks = 0;
    }
    
    public void markTaskDone(List<Integer> taskIndexList) throws TaskDoneException, TaskInvalidIdException {
        
        if (taskIndexList.isEmpty()) {
            throw new TaskInvalidIdException("Error index for editing!");
            
        } else {
            for (int i = 0; i < taskIndexList.size(); i++) {
                int taskToMarkDone = taskIndexList.get(i);
                
                if ((taskToMarkDone > totalTasks) || (taskToMarkDone <= 0)) {
                    throw new TaskInvalidIdException("Error index for editing!");
                    
                } else {
                    Task newRepeatTask = this.tasks.get(taskToMarkDone - 1).markDone();
                    if (newRepeatTask != null) {
                        this.addToList(newRepeatTask);
                    }
                }
            }
        }
        
    }
    
    public void tagTask(int taskIndexToTag, String tag) throws TaskInvalidIdException, TaskTagDuplicateException {
        if ((taskIndexToTag > totalTasks) || (taskIndexToTag <= 0)) {
            throw new TaskInvalidIdException();
            
        } else {
            Task taskToTag = tasks.get(taskIndexToTag - 1);
            taskToTag.addTag(tag);
            
            if (!tags.containsKey(tag.toLowerCase())) {
                List<Task> tagTaskList = new ArrayList<Task>();
                tagTaskList.add(taskToTag);
                tags.put(tag.toLowerCase(), tagTaskList);
                
            } else {
                List<Task> tagTaskList = tags.remove(tag.toLowerCase());
                tagTaskList.add(taskToTag);
                tags.put(tag.toLowerCase(), tagTaskList);
            }
        }
    }
    
    public int count() {
        return this.totalTasks;
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
    
    
    // this method is only for testing purpose
    // generate 50 dummy tasks
    public void test() {
        for (int i = 0; i < 50; i++) {
            this.tasks.add(new FloatingTask("No." + i));
        }
    }

}