package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import controller.UserInput.RepeatDate;
import exception.TaskDoneException;
import exception.TaskInvalidDateException;
import exception.TaskInvalidIdException;
import exception.TaskNoSuchTagException;
import exception.TaskTagDuplicateException;
import exception.TaskTagException;

public class TaskList {
    private static Logger logger = Logger.getLogger("TaskList");
	
    @XStreamAlias("TaskList")
    private List<Task> tasksTimed;
    private List<Task> tasksUntimed;
    private List<Task> tasksByAddedTime;
    private boolean isDisplayedByAddTime;
    
    @XStreamAlias("TasksCount")
    private int totalTasks;
    @XStreamAlias("Tags")
    private HashMap<String, List<Task>> tags;
    
    // count the total number of tasks finished.
    @XStreamAlias("TotalTaskFinished")
    private int taskFinished;
    
    public TaskList() {
        this.tasksTimed = new ArrayList<Task>();
        this.tasksUntimed = new ArrayList<Task>();
        this.totalTasks = this.tasksTimed.size() + this.tasksUntimed.size();
        this.tags = new HashMap<String, List<Task>>();
        this.taskFinished = 0;
        this.isDisplayedByAddTime = false;
    }
    /**
     * 
     * things to change:
     * - PQ for display by added time
     * - by default: sorted by deadline
     * - dependencies between logic and tasklist.
     * 
     * - CHANGE ONE TO TWO LIST --> Timed and untimed.
     * 
     */
    
    
    
    public Task get(int index) {
        return this.tasksTimed.get(index);
    }
    
    private void addToList(Task task) {
        this.tasksTimed.add(task);
        this.totalTasks++;
    }
    
    public void addToList(String description) {
    	Task newTask = new FloatingTask(description);
    	this.tasksTimed.add(newTask);
        this.totalTasks++;
        logger.log(Level.INFO, "A floating task added");
    }
    
    
    public void addToList(String description, Date time) {
    	Task newTask = new DeadlineTask(description, time);
    	this.tasksTimed.add(newTask);
        this.totalTasks++;
        logger.log(Level.INFO, "A deadline task added");
    }
    
    public void addToList(String description, Date time,
                                  RepeatDate repeatDate) {
        Task newTask = new RepeatedTask(description, time, repeatDate);
        this.tasksTimed.add(newTask);
        this.totalTasks++;
        logger.log(Level.INFO, "A repeated task added");
    }
    
    public void addToList(String description, Date startTime,
                                  Date endTime) {
        Task newTask = new FixedTask(description, startTime, endTime);
        this.tasksTimed.add(newTask);
        this.totalTasks++;
        logger.log(Level.INFO, "A fixed task added");
    }
    
    
    public void editTaskDescription(int taskIndex, String description) throws TaskInvalidIdException {
        if ((taskIndex > totalTasks) || (taskIndex <= 0)) {
            throw new TaskInvalidIdException("Error index for editing!");
            
        } else {
            this.tasksTimed.get(taskIndex - 1).setDescription(description);
        }
    }
    
    
    public void editTaskDeadline(int taskIndex, Date time) throws TaskInvalidIdException, TaskInvalidDateException {
        if ((taskIndex > totalTasks) || (taskIndex <= 0)) {
            throw new TaskInvalidIdException("Error index for editing!");
            
        } else {
            this.tasksTimed.get(taskIndex - 1).setDeadline(time);
        }   
    }
    
    public void editTaskStartDate(int taskIndex, Date startDate) throws TaskInvalidIdException {
        if ((taskIndex > totalTasks) || (taskIndex <= 0)) {
            throw new TaskInvalidIdException("Error index for editing!");
            
        } else {
            this.tasksTimed.get(taskIndex - 1).setStartTime(startDate);
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
                    this.tasksTimed.remove(indexToRemove - 1);
                    this.totalTasks--;
                }
            }
        }
    }
    
    public void clearList() {
        this.tasksTimed.clear();
        this.totalTasks = 0;
        this.taskFinished = 0;
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
                    Task newRepeatTask = this.tasksTimed.get(taskToMarkDone - 1).markDone();
                    this.taskFinished++;
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
            Task taskToTag = tasksTimed.get(taskIndexToTag - 1);
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


    public void untagTask(int taskIndexToUntag, String tag) throws TaskInvalidIdException, TaskTagException {
        if ((taskIndexToUntag > totalTasks) || (taskIndexToUntag <= 0)) {
            throw new TaskInvalidIdException();
            
        } else if (tag.isEmpty()) {
            untagTaskAll(taskIndexToUntag);
            
        } else {
            Task taskToTag = tasksTimed.get(taskIndexToUntag - 1);
            taskToTag.deleteTag(tag);
            
            if (tags.get(tag.toLowerCase()).size() == 1) {
                tags.remove(tag.toLowerCase());
                
            } else {
                List<Task> tagTaskList = tags.remove(tag.toLowerCase());
                tagTaskList.remove(taskToTag);
                tags.put(tag.toLowerCase(), tagTaskList);
            }
        }
    }
    
    private void untagTaskAll(int taskIndexToUntag) throws TaskTagException {
        Task taskToUntag = tasksTimed.get(taskIndexToUntag - 1);
        List<String> taskTags = taskToUntag.getTags();
        
        if (taskTags.isEmpty()) {
            throw new TaskTagException("No tags to remove");
        }
        
        while (!taskTags.isEmpty()) {
            String tag = taskTags.remove(0);
            
            assert tags.get(tag.toLowerCase()).contains(taskToUntag);
            
            tags.get(tag.toLowerCase()).remove(taskToUntag);
            taskToUntag.deleteTag(tag);
        }
        
    }
    
    public List<Task> getTasksWithTag(String tag) throws TaskNoSuchTagException {
        if (tags.containsKey(tag.toLowerCase())) {
            List<Task> taskListOfTag = tags.get(tag.toLowerCase());
            return taskListOfTag;
            
        } else {
            throw new TaskNoSuchTagException();
        }
    }
    
	public int count() {
        return this.totalTasks;
    }
	
	public int countFinished() {
        return this.taskFinished;
    }
    
    
    @Override
    public String toString() {
        String output = "";
        int i = 1;
        for (Task task : this.tasksTimed) {
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
            this.tasksTimed.add(new FloatingTask("No." + i));
        }
    }

}