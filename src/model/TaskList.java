package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	
    @XStreamAlias("TaskListTimed")
    private List<Task> tasksTimed;
    @XStreamAlias("TaskListUntimed")
    private List<Task> tasksUntimed;
    @XStreamAlias("TaskToDisplay")
    private List<Task> tasksToDisplay;
    @XStreamAlias("showDisplay")
    private boolean showDisplayList;

    @XStreamAlias("TasksCount")
    private int totalTasks;
    @XStreamAlias("Tags")
    private HashMap<String, List<Task>> tags;
    
    // count the total number of tasks finished.
    @XStreamAlias("TotalTaskFinished")
    private int taskFinished;
    
    public TaskList() {
		// let the logger only display warning log message.
		logger.setLevel(Level.WARNING);
		
        this.tasksTimed = new SortedArrayList<Task>(new DeadlineComparator());
        this.tasksUntimed = new SortedArrayList<Task>(new AddedDateComparator());
        this.tasksToDisplay = new ArrayList<Task>();
        this.totalTasks = this.tasksTimed.size() + this.tasksUntimed.size();
        this.tags = new HashMap<String, List<Task>>();
        this.taskFinished = 0;
        this.showDisplayList = false;
    }
    
    /**
     * Logic calls this method to set showDisplayList to false if the tasks are edited.
     */
    public void setShowDisplayListToFalse() {
        this.showDisplayList = false;
    }

    public Task getTask(int taskIndex) {
        if (showDisplayList) {
            return tasksToDisplay.get(taskIndex);
            
        } else {
            // get edit id from timed/ untimed list
            if (taskIndex < tasksTimed.size()) {
                return tasksTimed.get(taskIndex);
                
            } else {
                return tasksUntimed.get(taskIndex - tasksTimed.size());
            }           
        }

    }
    
    private void addToList(Task task) {
        Comparator<Task> a = new AddedDateComparator();
        a.compare(new FloatingTask("haha"), new FloatingTask("hehe"));
        if (task instanceof FloatingTask) {
            this.tasksUntimed.add(task);
            
        } else {
        this.tasksTimed.add(task);
        }
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
    	((SortedArrayList<Task>) this.tasksTimed).addOrder(newTask);
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
        ((SortedArrayList<Task>) this.tasksTimed).addOrder(newTask);
        this.totalTasks++;
        logger.log(Level.INFO, "A repeated task added");
    }
    
	/**
	 * Add a fixed(timed) task
	 * @param description the description of the task
	 * @param startTime the start time of the task
	 * @param endTime the deadline of the task
	 * @throws TaskInvalidDateException 
	 */
    public void addToList(String description, Date startTime, 
                          Date endTime) throws TaskInvalidDateException {
        Task newTask = new FixedTask(description, startTime, endTime);
        
        if (!startTime.before(endTime)) {
            throw new TaskInvalidDateException("Invalid: Start date/time cannot be after end date/time.");
            
        } else {
        
            ((SortedArrayList<Task>) this.tasksTimed).addOrder(newTask);
            this.totalTasks++;
            logger.log(Level.INFO, "A fixed task added");
        }
    }
    
    
    public void editTaskDescription(int taskIndex, String description) throws TaskInvalidIdException {
        if ((taskIndex > totalTasks) || (taskIndex <= 0)) {
            throw new TaskInvalidIdException("Error index for editing!");
        } else {
        	if (taskIndex <= tasksTimed.size()) {
        	    this.tasksTimed.get(taskIndex - 1).setDescription(description);
        	    
        	} else {
        	    this.tasksUntimed.get(taskIndex - 1 - tasksTimed.size()).setDescription(description);
        	}
        }
    }
    
    
    public void editTaskDeadline(int taskIndex, Date time) throws TaskInvalidIdException, TaskInvalidDateException {
        if ((taskIndex > totalTasks) || (taskIndex <= 0)) {
            throw new TaskInvalidIdException("Error index for editing!");
        } else {
            this.tasksTimed.get(taskIndex - 1).setDeadline(time);
            ((SortedArrayList<Task>) this.tasksTimed).updateListOrder(taskIndex - 1);
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
                int indexToRemove = taskIndexList.get(i) - 1;
                
                if ((indexToRemove >= totalTasks) || (indexToRemove < 0)) {
                    
                    throw new TaskInvalidIdException("Error index for editing!");
                    
                } else {
                    Task taskToRemove = getTask(indexToRemove);
                    
                    if (indexToRemove < tasksTimed.size()) {
                        this.tasksTimed.remove(taskToRemove);
                        
                    } else {
                        this.tasksUntimed.remove(taskToRemove);
                    }    
                    this.totalTasks--;
                }
            }
        }
    }
    
    public void clearList() {
    	this.tasksUntimed.clear();
        this.tasksTimed.clear();
        this.tasksUntimed.clear();
        this.totalTasks = 0;
        this.taskFinished = 0;
    }
    
    public void markTaskDone(List<Integer> taskIndexList) throws TaskDoneException, TaskInvalidIdException {
        
        if (taskIndexList.isEmpty()) {
            throw new TaskInvalidIdException("Error index for editing!");
            
        } else {
            for (int i = 0; i < taskIndexList.size(); i++) {
                int taskIndexToMarkDone = taskIndexList.get(i) - 1;
                
                if ((taskIndexToMarkDone >= totalTasks) || (taskIndexToMarkDone < 0)) {
                    throw new TaskInvalidIdException("Error index for editing!");
                    
                } else {
                    Task newRepeatTask;
                    
                    if (taskIndexToMarkDone < tasksTimed.size()) {
                        newRepeatTask = this.tasksTimed.get(taskIndexToMarkDone).markDone();
                        
                    } else {
                        newRepeatTask = this.tasksUntimed.get(taskIndexToMarkDone).markDone();
                    }
                    
                    this.taskFinished++;
                    if (newRepeatTask != null) {
                        
                        this.addToList((RepeatedTask) newRepeatTask);
                    }
                }
            }
        }
    }
    
    public void tagTask(int taskIndexToTag, String tag) throws TaskInvalidIdException, TaskTagDuplicateException {
        if ((taskIndexToTag > totalTasks) || (taskIndexToTag <= 0)) {
            throw new TaskInvalidIdException();
            
        } else {
            
            Task taskToTag = getTask(taskIndexToTag - 1);
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
            untagTaskAll(taskIndexToUntag - 1);
            
        } else {
            Task taskToTag = getTask(taskIndexToUntag - 1);
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
        Task taskToUntag = getTask(taskIndexToUntag);
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
    
    public List<Task> prepareDisplayList(boolean isDisplayedByAddTime) {
    	List<Task> output;
    	if (isDisplayedByAddTime) {
    		// using comparator AddedDateComparator
    	    output = new SortedArrayList<Task>(this.count(), new AddedDateComparator());
            output.addAll(tasksTimed);
            output.addAll(tasksUntimed);   
            showDisplayList = true;
            tasksToDisplay = output;

    	} else {
    		// using comparator DeadlineComparator
    		output = new SortedArrayList<Task>(this.count(), new DeadlineComparator());
            output.addAll(tasksTimed);
            for (int i = 0; i < tasksUntimed.size(); i++) {
                output.add(tasksUntimed.get(i));
            }
            showDisplayList = false;
    	}
    	
		// add all tasks from Timed task list and Untimed task list to the output list
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