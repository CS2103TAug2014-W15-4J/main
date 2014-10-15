package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
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
	/**
	 * This Comparator is used in outputting task list order by deadline
	 * 
	 * @author Jiang Sheng
	 *
	 */
	static class DeadlineComparator implements Comparator<Task> {
		
		@Override
		public int compare(Task o1, Task o2) {
			try {
				return o1.getDeadline().compareTo(o2.getDeadline());
			} catch (TaskInvalidDateException e) {
				logger.log(Level.WARNING, "Error comparing deadline.");
			}
			return 0;
		}
		
	}
	
	/**
	 * This Comparator is used in outputting task list order by added time
	 * 
	 * @author Jiang Sheng
	 *
	 */
	static class AddedDateComparator implements Comparator<Task> {

		@Override
		public int compare(Task o1, Task o2) {
				return o1.getAddedTime().compareTo(o2.getAddedTime());
		}
		
	}
	
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
     * JS wrote:
     * How about default by Deadline.
     * If type "display by abc"
     * Then just output the list, but the internal data structure donesn't change since 
     * we just create a list for output only?
     * -> In this case, isDisplayedByAddTime is redundant.
     * 
     */
   
    public Task get(int index) {
        return this.tasksTimed.get(index);
    }
    
    private void addToList(Task task) {
        this.tasksTimed.add(task);
        this.totalTasks++;
    }
    
    /**
     * Adding a floating task
     * @param description the description of the task
     */
    public void addToList(String description) {
    	Task newTask = new FloatingTask(description);
    	this.tasksUntimed.add(newTask);
        this.totalTasks++;
        logger.log(Level.INFO, "A floating task added");
    }
    
    /**
     * Add a deadline task
     * @param description the description of the task
     * @param time the deadline of the task
     */
    public void addToList(String description, Date time) {
    	Task newTask = new DeadlineTask(description, time);
    	this.tasksTimed.add(newTask);
        this.totalTasks++;
        logger.log(Level.INFO, "A deadline task added");
    }
    
    /**
     * Add a Repeated Task
     * @param description the description of the task
     * @param time the deadline of the task
     * @param repeatDate the repeat frequency
     */
    public void addToList(String description, Date time,
                                  RepeatDate repeatDate) {
        Task newTask = new RepeatedTask(description, time, repeatDate);
        this.tasksTimed.add(newTask);
        this.totalTasks++;
        logger.log(Level.INFO, "A repeated task added");
    }
    
	/**
	 * Add a fixed(timed) task
	 * @param description the description of the task
	 * @param startTime the start time of the task
	 * @param endTime the deadline of the task
	 */
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
        	// This one!!!!!!!!!
        	// not really inside the timed task list
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
    	this.tasksUntimed.clear();
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
    
    public PriorityQueue<Task> prepareDisplayList(boolean isDisplayedByAddTime) {
    	PriorityQueue<Task> output;
    	if (isDisplayedByAddTime) {
    		// using comparator AddedDateComparator
    		output = new PriorityQueue<Task>(this.count(), new AddedDateComparator());
    	} else {
    		// using comparator DeadlineComparator
    		output = new PriorityQueue<Task>(this.count(), new DeadlineComparator());
    	}
		// add all tasks from Timed task list and Untimed task list to the output list
		output.addAll(tasksTimed);
		output.addAll(tasksUntimed);
		return output;

    }
    
	public int count() {
        return this.totalTasks;
    }
	
	public int countTimedTask() {
        return this.tasksTimed.size();
    }
	
	public int countUntimedTask() {
        return this.tasksUntimed.size();
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
            this.tasksUntimed.add(new FloatingTask("No." + i));
        }
    }

}