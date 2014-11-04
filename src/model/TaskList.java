package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import controller.LastState;
import controller.Storage;
import controller.LastState.LastCommand;
import controller.UserInput.RepeatDate;
import exception.RedoException;
import exception.TaskDoneException;
import exception.TaskInvalidDateException;
import exception.TaskInvalidIdException;
import exception.TaskNoSuchTagException;
import exception.TaskTagDuplicateException;
import exception.TaskTagException;
import exception.UndoException;

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
	
	static class DoneDateComparator implements Comparator<Task> {
	    
	    @Override
	    public int compare(Task o1, Task o2) {
	        return o1.getDoneDate().compareTo(o2.getDoneDate());
	    }
	}

	private static Logger logger = Logger.getLogger("TaskList");

	
	static Stack<LastState> undoStack = new Stack<LastState>();
	static Stack<LastState> redoStack = new Stack<LastState>();
	
	@XStreamAlias("TaskListTimed")
	private List<Task> tasksTimed;
	@XStreamAlias("TaskListUntimed")
	private List<Task> tasksUntimed;
	@XStreamAlias("TaskFinished")
	private List<Task> tasksFinished;
	
	@XStreamOmitField
	private List<Task> tasksToDisplay;
	
	@XStreamAlias("TaskRepeated")
	private List<Task> tasksRepeated;

	
	@XStreamAlias("showDisplay")
	private boolean isDisplay;
	@XStreamAlias("showDone")
	private boolean isShowingDone;

	@XStreamAlias("TasksCount")
	private int totalTasks;
	
	@XStreamAlias("Tags")
	private HashMap<String, List<Task>> tags;

	// count the total number of tasks finished.
	@XStreamAlias("TotalTaskFinished")
	private int totalFinished;

	public TaskList() {
		// let the logger only display warning log message.
		logger.setLevel(Level.WARNING);

		this.tasksTimed = new SortedArrayList<Task>(new DeadlineComparator());
		this.tasksUntimed = new SortedArrayList<Task>(new AddedDateComparator());
		this.tasksFinished = new SortedArrayList<Task>(new DoneDateComparator());
		this.tasksToDisplay = new ArrayList<Task>();
		this.tasksRepeated = new ArrayList<Task>();
		this.totalTasks = this.tasksTimed.size() + this.tasksUntimed.size();
		this.tags = new HashMap<String, List<Task>>();
		this.totalFinished = 0;
		this.isDisplay = false;
	}

	/**
	 * If setShowDisplayListToFalse is called, display the whole list.
	 */
	public void setShowDisplayListToFalse() {
		// lazy evaluation
		if (this.tasksToDisplay == null) {
			this.tasksToDisplay = new ArrayList<Task>();
		}
		this.isDisplay = false;
		this.tasksToDisplay.clear();
	}

	public Task getTask(int taskIndex) {
		if (isDisplay) {
			if ((taskIndex >= tasksToDisplay.size()) || (taskIndex < 0)) {
				throw new TaskInvalidIdException("Error index for editing!");
			} else {
				return tasksToDisplay.get(taskIndex);
			}
		} else {
			if ((taskIndex >= totalTasks) || (taskIndex < 0)) {
				throw new TaskInvalidIdException("Error index for editing!");
			} else {
				// get edit id from timed/ untimed list
				if (taskIndex < tasksTimed.size()) {
					return tasksTimed.get(taskIndex);
				} else {
					return tasksUntimed.get(taskIndex - tasksTimed.size());
				}
			}
		}
	}

	/**
	 * 
	 * @param task
	 * @return taskid of given task 
	 */
	public int getTaskIndex(Task task) {
	    if (tasksTimed.contains(task)) {
	        return tasksTimed.indexOf(task);
	        
	    } else if (tasksUntimed.contains(task)) {
	        return tasksUntimed.indexOf(task) + tasksTimed.size();
	        
	    } else {
	        return -1;
	    }
	    
	    
	}
	/**
	 * This method adds a task to the corresponding lists in its position
	 * This method is called by markTaskDone, if task is a repeat task;
	 * This method is called by undo, to revert the deleting of tasks.
	 * 
     * @param task      The task to be added
     */
	private void addToList(Task task) {
	    
	    if (task.getIsDone()) {
	        ((SortedArrayList<Task>) this.tasksFinished).addOrder(task);
	        
	    } else {
    		if (task instanceof FloatingTask) {
    			((SortedArrayList<Task>) this.tasksUntimed).addOrder(task);
    
    		} else {
    			((SortedArrayList<Task>) this.tasksTimed).addOrder(task);
    			addToTaskRepeated(task);
    		}
    		this.totalTasks++;
	    }
	    
	    
	}

	/**
	 * Adding a floating task
	 * 
	 * @param description
	 *            the description of the task
	 */
	public void addToList(String description) {
		Task newTask = new FloatingTask(description);
		this.tasksUntimed.add(newTask);
		this.totalTasks++;
		logger.log(Level.INFO, "A floating task added");
		
		addToUndoList(LastCommand.ADD, newTask);
	}

	/**
	 * Add a deadline task
	 * 
	 * @param description
	 *            the description of the task
	 * @param time
	 *            the deadline of the task
	 */
	public void addToList(String description, Date time) {
		Task newTask = new DeadlineTask(description, time);
		((SortedArrayList<Task>) this.tasksTimed).addOrder(newTask);
		this.totalTasks++;
		logger.log(Level.INFO, "A deadline task added");
		
		addToUndoList(LastCommand.ADD, newTask);
	}

	/**
	 * Add a Repeated Task
	 * 
	 * @param description
	 *            the description of the task
	 * @param time
	 *            the deadline of the task
	 * @param repeatDate
	 *            the repeat frequency
	 */
	public void addToList(String description, Date time, RepeatDate repeatDate) {
		Task newTask = new RepeatedTask(description, time, repeatDate);
		((SortedArrayList<Task>) this.tasksTimed).addOrder(newTask);
		this.tasksRepeated.add(newTask);
		this.totalTasks++;
		logger.log(Level.INFO, "A repeated task added");
		
		addToUndoList(LastCommand.ADD, newTask);
	}

	/**
	 * Add a fixed(timed) task
	 * 
	 * @param description
	 *            the description of the task
	 * @param startTime
	 *            the start time of the task
	 * @param endTime
	 *            the deadline of the task
	 * @throws TaskInvalidDateException
	 */
	public void addToList(String description, Date startTime, Date endTime)
			throws TaskInvalidDateException {
		Task newTask = new FixedTask(description, startTime, endTime);

		if (!startTime.before(endTime)) {
			throw new TaskInvalidDateException(
					"Invalid: Start date/time cannot be after end date/time.");

		} else {

			((SortedArrayList<Task>) this.tasksTimed).addOrder(newTask);
			this.totalTasks++;
			logger.log(Level.INFO, "A fixed task added");
			
			addToUndoList(LastCommand.ADD, newTask);
		}
	}
	
	public void editTaskDescriptionOnly(int taskIndex, String description) {

        if (isInvalidIndex(taskIndex)) {
            throw new TaskInvalidIdException();
            
        } else {
            Task taskToEdit = getTask(taskIndex - 1);
            Task clonedTask = taskToEdit.clone();
            editTaskDescription(taskToEdit, description);
            addToUndoList(LastCommand.EDIT, clonedTask, taskToEdit);
        }
	}
	
    public void editTaskDeadlineOnly(int taskIndex, Date time) throws TaskInvalidIdException, TaskInvalidDateException {
        if (isInvalidIndex(taskIndex)) {
            throw new TaskInvalidIdException();
            
        } else {
            Task taskToEdit = getTask(taskIndex - 1);
            Task clonedTask = taskToEdit.clone();
            editTaskDeadline(taskToEdit, time);
            addToUndoList(LastCommand.EDIT, clonedTask, taskToEdit);
        }
	}
	
	public void editTaskDescriptionDeadline(int taskIndex, String desc, Date time) throws TaskInvalidIdException, TaskInvalidDateException {
        if (isInvalidIndex(taskIndex)) {
            throw new TaskInvalidIdException();
            
        } else {
            Task taskToEdit = getTask(taskIndex - 1);
            Task clonedTask = taskToEdit.clone();
            editTaskDescription(taskToEdit, desc);
            editTaskDeadline(taskToEdit, time);
            addToUndoList(LastCommand.EDIT, clonedTask, taskToEdit);
        }
	}
	
	public void editTaskTimes(int taskIndex, Date startDate, Date endDate) throws TaskInvalidIdException, TaskInvalidDateException {
        if (isInvalidIndex(taskIndex)) {
            throw new TaskInvalidIdException();
            
        } else {
            Task taskToEdit = getTask(taskIndex - 1);
            Task clonedTask = taskToEdit.clone();
            editTaskStartDate(taskToEdit, startDate);
            editTaskDeadline(taskToEdit, endDate);
            addToUndoList(LastCommand.EDIT, clonedTask, taskToEdit);
        }
	}
	
	public void editTaskDescriptionTimes(int taskIndex, String desc, Date startDate, Date endDate) throws TaskInvalidIdException, TaskInvalidDateException {
        if (isInvalidIndex(taskIndex)) {
            throw new TaskInvalidIdException();
            
        } else {
            Task taskToEdit = getTask(taskIndex - 1);
            Task clonedTask = taskToEdit.clone();
            editTaskDescription(taskToEdit, desc);
            editTaskStartDate(taskToEdit, startDate);
            editTaskDeadline(taskToEdit, endDate);
            addToUndoList(LastCommand.EDIT, clonedTask, taskToEdit);
        }
	}
	

	public void editTaskDescription(Task taskToRemove, String description)
			throws TaskInvalidIdException {

	    int indexToEdit = getTaskIndex(taskToRemove);
	    
	    // if the index comes from a list used for displaying, 
	    // use time to find
	    if (isDisplay) {
	        boolean isFound = false;
	        // trace the task by added time.
	        for (Task task : this.tasksTimed) {
	            if (task.getAddedTime().equals(taskToRemove.getAddedTime())) {
	                task.setDescription(description);
	                isFound = true;
	                break;
	            }
	        }
	        if (!isFound) {
	            for (Task task : this.tasksUntimed) {
	                if (task.getAddedTime().equals(taskToRemove.getAddedTime())) {
	                    task.setDescription(description);
	                    break;
	                }
	            }
	        }
	        
	    } else {
	        if (indexToEdit < tasksTimed.size()) {
	            this.tasksTimed.get(indexToEdit)
	            .setDescription(description);
	            
	        } else {
	            // update the index to the proper value in tasksUntimed.
	            indexToEdit -= tasksTimed.size();
	            this.tasksUntimed.get(indexToEdit).setDescription(description);
	        }
	    }
	}


	public void editTaskDeadline(Task taskToRemove, Date time)
			throws TaskInvalidIdException, TaskInvalidDateException {

	    int indexToEdit = getTaskIndex(taskToRemove);
	    
	    // if the index comes from a list used for displaying, use time to
	    // find
	    if (isDisplay) {
	        boolean isFound = false;
	        // trace the task by added time.
	        for (Task task : this.tasksTimed) {
	            if (task.getAddedTime().equals(taskToRemove.getAddedTime())) {
	                task.setDeadline(time);
	                isFound = true;
	                break;
	            }
	        }
	        if (!isFound) {
	            for (Task task : this.tasksUntimed) {
	                if (task.getAddedTime().equals(
	                                               taskToRemove.getAddedTime())) {
	                    task.setDeadline(time);
	                    break;
	                }
	            }
	        }
	    } else {
	        if (indexToEdit < tasksTimed.size()) {
	            this.tasksTimed.get(indexToEdit).setDeadline(time);
	            
	        } else {
	            // update the index to the proper value in tasksUntimed.
	            indexToEdit -= tasksTimed.size();
	            this.tasksUntimed.get(indexToEdit).setDeadline(time);
	        }
	    }
	    ((SortedArrayList<Task>) this.tasksTimed).updateListOrder(indexToEdit);
	}

	public void editTaskStartDate(Task taskToRemove, Date startDate)
			throws TaskInvalidIdException {

	    int indexToEdit = getTaskIndex(taskToRemove);
	    
	    // if the index comes from a list used for displaying, use time to
	    // find
	    if (isDisplay) {
	        boolean isFound = false;
	        // trace the task by added time.
	        for (Task task : this.tasksTimed) {
	            if (task.getAddedTime().equals(taskToRemove.getAddedTime())) {
	                task.setStartTime(startDate);
	                isFound = true;
	                break;
	            }
	        }
	        if (!isFound) {
	            for (Task task : this.tasksUntimed) {
	                if (task.getAddedTime().equals(taskToRemove.getAddedTime())) {
	                    task.setStartTime(startDate);
	                    break;
	                }
	            }
	        }
	    } else {
	        if (indexToEdit < tasksTimed.size()) {
	            this.tasksTimed.get(indexToEdit).setStartTime(startDate);
	            
	        } else {
	            // update the index to the proper value in tasksUntimed.
	            indexToEdit -= tasksTimed.size();
	            this.tasksUntimed.get(indexToEdit).setStartTime(startDate);
	        }
	    }
	}

	/*
	 * public void editTaskRepeatPeriod(int taskIndex, String repeatPeriod) { if
	 * ((taskIndex > totalTasks) || (taskIndex <= 0)) { // error here } else {
	 * this.tasks.get(taskIndex-1).setRepeatPeriod(repeatPeriod); }
	 * 
	 * }
	 */
	
	/**
	 * This method is called by undo, to revert the adding of tasks
	 * 
	 * @param task     The task that was added and is to be deleted 
	 */
	private void deleteFromList(Task task) {
	    for (int i = 0; i < this.count(); i++) {
	        Task taskI = getTask(i);
	        if (taskI.equals(task)) {
	            if (i < this.tasksTimed.size()) {
	                tasksTimed.remove(taskI);
	                deleteFromTasksRepeated(taskI);
	            } else {
	                tasksUntimed.remove(taskI);
	            }
	            this.totalTasks--;
	        }
	    }
	}

	public void deleteFromList(List<Integer> taskIndexList)
			throws TaskInvalidIdException {
		if (taskIndexList.isEmpty()) {
			throw new TaskInvalidIdException("Nothing to delete");
		} else {

		    ArrayList<Task> tasksRemoved = new ArrayList<Task>();
		    
			Collections.sort(taskIndexList);
			for (int i = taskIndexList.size() - 1; i >= 0; i--) {
				int indexToRemove = taskIndexList.get(i);
				if (isShowingDone) {
					// displaying done tasks
					if (indexToRemove>this.tasksFinished.size() || indexToRemove<=0) {
						throw new TaskInvalidIdException("Error index for deleting!");
					} else {
					    Task taskRemoved = this.tasksFinished.remove(indexToRemove - 1); 
					    deleteFromTasksRepeated(taskRemoved);
					    tasksRemoved.add(taskRemoved);
					}
					this.totalTasks--;
				} else {
					if (isInvalidIndex(indexToRemove)) {

						throw new TaskInvalidIdException("Error index for deleting!");

					} else {
						Task taskToRemove = getTask(indexToRemove - 1);
						deleteFromTasksRepeated(taskToRemove);
						tasksRemoved.add(taskToRemove);
						// if the index comes from a list used for displaying, use
						// time to find
						boolean isFound = false;
						// trace the task by added time.
						for (Task task : this.tasksTimed) {
							if (task.getAddedTime().equals(
									taskToRemove.getAddedTime())) {
								this.tasksTimed.remove(task);
								isFound = true;
								break;
							}
						}
						if (!isFound) {
							for (Task task : this.tasksUntimed) {
								if (task.getAddedTime().equals(
										taskToRemove.getAddedTime())) {
									this.tasksUntimed.remove(task);
									break;
								}
							}
						}

						this.totalTasks--;
					}
				}	
			}
			addToUndoList(LastCommand.DELETE, tasksRemoved);
		}
	}
	
	/**
	 * This method deletes the specified task from tasksRepeat list,
	 * if the list contains the task and the task is a RepeatedTask
	 *  
	 * @param taskToRemove The task to be removed from tasksRepeat
	 */
	private void deleteFromTasksRepeated(Task taskToRemove) {
	    if (taskToRemove instanceof RepeatedTask) {
	        if (tasksRepeated.contains(taskToRemove)) {
	            tasksRepeated.remove(taskToRemove);
	        }
	    }
	}
	
	/**
	 * This method adds the specified task to tasksRepeat list,
	 * if the list does not contain the task and the task is a RepeatedTask
	 * 
	 * @param taskToAdd    The task to be added to tasksRepeat
	 */
	private void addToTaskRepeated(Task taskToAdd) {
	    if (taskToAdd instanceof RepeatedTask) {
	        if (!tasksRepeated.contains(taskToAdd)) {
	            tasksRepeated.add(taskToAdd);
	        }
	    }
	}

    public void clearList() {
        ArrayList<Task> tasksRemoved = new ArrayList<Task>();
        tasksRemoved.addAll(tasksUntimed);
        tasksRemoved.addAll(tasksTimed);
        tasksRemoved.addAll(tasksFinished);
        addToUndoList(LastCommand.DELETE, tasksRemoved);
        
		this.isDisplay = false;
		this.tasksUntimed.clear();
		this.tasksTimed.clear();
		this.tasksUntimed.clear();
		this.tasksFinished.clear();
		this.tasksRepeated.clear();
		this.tags.clear();
		this.totalTasks = 0;
		this.totalFinished = 0;

	}

	public void markTaskDone(List<Integer> taskIndexList)
			throws TaskDoneException, TaskInvalidIdException {

		if (taskIndexList.isEmpty()) {
			throw new TaskInvalidIdException("Error index input.");

		} else {
		    
		    List<Task> tasksToMarkDone = new ArrayList<Task>();
		    List<Task> tasksBeforeMarkingDone = new ArrayList<Task>();
		    List<Task> newRepeatTaskList = new ArrayList<Task>();
		    
		    // putting the tasks to be marked done into a list,
		    // since marking a task done would move it into a new list, 
		    // changing the order (using index might not work)
		    for (int i = 0; i < taskIndexList.size(); i++) {
		        int taskIdToMarkDone = taskIndexList.get(i);
		        if (isInvalidIndex(taskIdToMarkDone)) {
		            throw new TaskInvalidIdException("Error index input.");
		        } else {
		            Task taskToMarkDone = getTask(taskIdToMarkDone - 1);
		            tasksToMarkDone.add(taskToMarkDone);
		        }
		    }
		    
		    for (Task target : tasksToMarkDone) {
		        tasksBeforeMarkingDone.add(target.clone());
		        Task newRepeatTask = null;
		        
		        if (this.tasksUntimed.contains(target)) {
		            newRepeatTask = target.markDone();
		            this.tasksUntimed.remove(target);
		            this.tasksFinished.add(target);
		            
		        } else if (this.tasksTimed.contains(target)) {
		            newRepeatTask = target.markDone();
		            this.tasksTimed.remove(target);
		            this.tasksFinished.add(target);
		        }
		        
		        this.totalFinished++;
		        if (newRepeatTask != null) {
		            this.addToList((RepeatedTask) newRepeatTask);
		            newRepeatTaskList.add(newRepeatTask);
		        }
		    }
		    
		    addToUndoList(LastCommand.DONE, tasksBeforeMarkingDone, tasksToMarkDone, newRepeatTaskList);
		}
	}
	
	/**
	 * This method is called by redo, to revert the undo on marking tasks done
	 * 
	 * @param task     The task that is to be re-marked done 
	 */
	private void markTaskRedone(Task task) {
	    task.markRedone();
	}
	
	/**
	 * This method is called by undo, to revert the marking done of tasks
	 * 
	 * @param task     The task that is to be marked un-done
	 */
	private void markTaskUndone(Task task) {
	    task.markUndone();
	}

	public void tagTask(int taskIndexToTag, String tag)
			throws TaskInvalidIdException, TaskTagDuplicateException {
	    
	    if (isInvalidIndex(taskIndexToTag)) {
	        throw new TaskInvalidIdException();
	        
	    } else if (tag.equals(null)) {
	        assert false;
	       
	    } else if (tag.isEmpty()) {
	        assert false;
	        
	    } else {
	        Task givenTaskToTag = getTask(taskIndexToTag - 1);
	        Task clonedTask = givenTaskToTag.clone();
	        tagGivenTask(givenTaskToTag, tag);
	        
	        addToUndoList(LastCommand.TAG, clonedTask, givenTaskToTag, tag);
	    }
	}
	
	/**
	 * This method attaches a tag to the task.
	 * This method is called by tagTask, when tagging a task;
	 * This method is called by undo, when reverting the untag operation;
	 * and is called by redo, when reverting the undo of the tag operation
	 * 
	 * @param taskToTag    The task that is to be tagged
	 * @param tag          The tag to be attached to the task
	 * @throws TaskTagDuplicateException   if the task already has the tag
	 */
    private void tagGivenTask(Task taskToTag, String tag) throws TaskTagDuplicateException {
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

	public void untagTask(int taskIndexToUntag, String tag)
			throws TaskInvalidIdException, TaskTagException {
	    if (isInvalidIndex(taskIndexToUntag)) {
	        throw new TaskInvalidIdException();
	        
        } else if (tag.equals(null)) {
            assert false;
            
        } else if (tag.isEmpty()) { 
            assert false;
            
        } else {
	        Task givenTaskToUntag = getTask(taskIndexToUntag - 1);
	        Task clonedTask = givenTaskToUntag.clone();
	        untagGivenTask(givenTaskToUntag, tag);
            addToUndoList(LastCommand.UNTAG, clonedTask, givenTaskToUntag, tag);
	    }
	}
	
	/**
	 * This method removes a tag to the task. 
	 * If no tag is given, all tags from the given task are removed.
	 *  
	 * @param taskToUntag  The task that is to be untagged
	 * @param tag          The tag to be removed from the task
	 * @throws TaskTagException    if the task does not have the tag to remove
	 */
    private void untagGivenTask(Task taskToUntag, String tag) throws TaskTagException {
	    if (tag.isEmpty()) {
	        untagTaskAll(taskToUntag);
	        
	    } else {
	        taskToUntag.deleteTag(tag);
	        
	        if (tags.get(tag.toLowerCase()).size() == 1) {
	            tags.remove(tag.toLowerCase());
	            
	        } else {
	            List<Task> tagTaskList = tags.remove(tag.toLowerCase());
	            tagTaskList.remove(taskToUntag);
	            tags.put(tag.toLowerCase(), tagTaskList);
	        }
	    }
	}

    /**
     * This method removes all tags from the task.
     * This method is called by untagGivenTask.
     * 
     * @param taskToUntag   The task that is to be untagged
     * @throws TaskTagException     if the task does not have any tags to remove
     */
	private void untagTaskAll(Task taskToUntag) throws TaskTagException {
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
	
	public List<Task> getFinishedTasks() {
		isShowingDone = true;
		return this.tasksFinished;
	}
	
	/**
	 * @author A0119414L
	 * 
	 * @param tag	
	 * @return 		true if tag is contained in the task list
	 */
	public boolean isTagContained(String tag) {
		return tags.containsKey(tag.toLowerCase());
	}
	
	public List<Task> getTasksWithTag(String tag) throws TaskNoSuchTagException {
		if (tags.containsKey(tag.toLowerCase())) {
			List<Task> taskListOfTag = tags.get(tag.toLowerCase());
			return taskListOfTag;
		} else {
			throw new TaskNoSuchTagException();
		}
	}

	/***
	 * This method search tasks by a given keyword
	 * 
	 * @param keyword
	 *            the keyword for searching
	 * @return a list of result
	 * 
	 *         Noticed: this method will find keyword in a task's description as
	 *         well as tags
	 */
	public List<Task> searchTaskByKeyword(String keyword) {
		keyword = keyword.toLowerCase();
		List<Task> result = new ArrayList<Task>();
		if (isShowingDone) {
			for (Task task : tasksFinished) {
				if (task.getDescription().toLowerCase().indexOf(keyword) != -1) {
					result.add(task);
					continue; // find one
				} else {
					for (String tag : task.getTags()) {
						if (tag.toLowerCase().indexOf(keyword) != -1) {
							result.add(task);
							break; // find one
						}
					}
				}
			}
		} else {
			// search task in timed list, search description and tags
			for (Task task : tasksTimed) {
				if (task.getDescription().toLowerCase().indexOf(keyword) != -1) {
					result.add(task);
					continue; // find one
				} else {
					for (String tag : task.getTags()) {
						if (tag.toLowerCase().indexOf(keyword) != -1) {
							result.add(task);
							break; // find one
						}
					}
				}
			}

			// search task in untimed list, search description and tags
			for (Task task : tasksUntimed) {
				if (task.getDescription().toLowerCase().indexOf(keyword) != -1) {
					result.add(task);
					continue; // find one
				} else {
					for (String tag : task.getTags()) {
						if (tag.toLowerCase().indexOf(keyword) != -1) {
							result.add(task);
							break; // find one
						}
					}
				}
			}
			isDisplay = true;
		}

		tasksToDisplay = result;
		return tasksToDisplay;
	}
	
	/**
	 * This method will find out the tasks which deadline is within the given
	 * range. 
	 * Notice: For displaying purpose, all the floating tasks will be added as well.
	 * @param showDate     the input time range
	 * @return A list of tasks that satisfies the range
	 * @throws TaskInvalidDateException
	 */
	public List<Task> getDateRangeTask(List<Date> showDate) throws TaskInvalidDateException {
		assert(showDate.size() == 2);
		
		SortedArrayList<Task> output = new SortedArrayList<Task>(new DeadlineComparator());
		Date startTime = showDate.get(0);
		Date endTime = showDate.get(1);
		
		// find task within the date range
		for (Task task : tasksTimed) {
			if (task instanceof RepeatedTask) {
		        // repeated tasks are checked separately
		    } else if ((task.getDeadline().after(startTime) || task.getDeadline().equals(startTime)) && 
					(task.getDeadline().before(endTime) || task.getDeadline().equals(endTime))) {

				output.add(task);
			}
		}
		
		Calendar taskTimeCal = Calendar.getInstance();
		Calendar searchTimeStartCal = Calendar.getInstance();
		Calendar searchTimeEndCal = Calendar.getInstance();
		
		searchTimeStartCal.setTime(startTime);
		searchTimeEndCal.setTime(endTime);
		
		// search if repeat task is in date range
		for (Task task : tasksRepeated) {
		    RepeatedTask repeatedTask = (RepeatedTask) task;
		    taskTimeCal.setTime(task.getDeadline());		    
		    String periodString = repeatedTask.getRepeatPeriod();
		    
		    if (periodString.equals("daily")) {
		        if ((taskTimeCal.get(Calendar.HOUR_OF_DAY) >= searchTimeStartCal.get(Calendar.HOUR_OF_DAY)) &&
		            (taskTimeCal.get(Calendar.HOUR_OF_DAY) <= searchTimeEndCal.get(Calendar.HOUR_OF_DAY))) {
		            
		            output.addOrder(repeatedTask);
		        }

		    } else if (periodString.split(" ")[0].equals("every")) {

		        if ((taskTimeCal.get(Calendar.DAY_OF_WEEK) >= searchTimeStartCal.get(Calendar.DAY_OF_WEEK)) && 
		            (taskTimeCal.get(Calendar.DAY_OF_WEEK) <= searchTimeEndCal.get(Calendar.DAY_OF_WEEK)) &&
		            (taskTimeCal.get(Calendar.HOUR_OF_DAY) >= searchTimeStartCal.get(Calendar.HOUR_OF_DAY)) &&
		            (taskTimeCal.get(Calendar.HOUR_OF_DAY) <= searchTimeEndCal.get(Calendar.HOUR_OF_DAY))) {
		            
		            output.addOrder(repeatedTask);
		        }

            } else if (periodString.split(" ")[0].equals("day")) {
                
                if ((taskTimeCal.get(Calendar.DAY_OF_MONTH) >= searchTimeStartCal.get(Calendar.DAY_OF_MONTH)) && 
                    (taskTimeCal.get(Calendar.DAY_OF_MONTH) <= searchTimeEndCal.get(Calendar.DAY_OF_MONTH)) &&
                    (taskTimeCal.get(Calendar.DAY_OF_WEEK) >= searchTimeStartCal.get(Calendar.DAY_OF_WEEK)) &&
                    (taskTimeCal.get(Calendar.DAY_OF_WEEK) <= searchTimeEndCal.get(Calendar.DAY_OF_WEEK)) &&
                    (taskTimeCal.get(Calendar.HOUR_OF_DAY) >= searchTimeStartCal.get(Calendar.HOUR_OF_DAY)) &&
                    (taskTimeCal.get(Calendar.HOUR_OF_DAY) <= searchTimeEndCal.get(Calendar.HOUR_OF_DAY))) {
                    
                    output.addOrder(repeatedTask);
                }
            } 
		}
		
		// add all floating task
		output.addAllUnordered(tasksUntimed);
		isDisplay = true;
		tasksToDisplay = output;
		return output;
	}

	/**
	 * This methods will find out the tasks that are overdue.
	 * @return a list of overdue tasks.
	 */
	public List<Task> getOverdueTask() {
		List<Task> output = new ArrayList<Task>();
		for (Task task : this.tasksTimed) {
			try {
				task.checkOverdue();
				if (task.isOverdue) {
					output.add(task);
				}
			} catch (TaskInvalidDateException e) {
				logger.log(Level.WARNING,
						"Invalid Deadline when checking Overdue!");
			}
		}
		return output;
	}
	
	/**
	 * 
	 * @param tag
	 * @return
	 * @throws TaskNoSuchTagException
	 */
	public List<Task> prepareDisplayList(String tag)
			throws TaskNoSuchTagException {
		
		List<Task> output;
		if (tags.containsKey(tag.toLowerCase())) {
			checkOverdue(this.tasksTimed);
			tasksToDisplay = new ArrayList<Task>(tags.get(tag.toLowerCase()));

			isDisplay = true;
			
			
			return tasksToDisplay;
		} else {
			throw new TaskNoSuchTagException();
		}
	}
	
	/**
	 * This method will prepare a list for displaying, the order is decide by the boolean 
	 * value isDisplayedByAddTime. 
	 * If isDisplayedByAddTime is true, the result will be ordered by added time;
	 * if isDisplayedByAddTime is false, the result will be ordered by deadline.
	 * 
	 * @param isDisplayedByAddTime
	 * @return
	 */
	public List<Task> prepareDisplayList(boolean isDisplayedByAddTime) {
		List<Task> output;
		// check overdue for each task
		checkOverdue(this.tasksTimed);

		if (isDisplayedByAddTime) {
			// using comparator AddedDateComparator
			output = new SortedArrayList<Task>(this.count(),
					                           new AddedDateComparator());
			((SortedArrayList<Task>) output).addAllOrdered(tasksTimed);
			((SortedArrayList<Task>) output).addAllOrdered(tasksUntimed);
			isDisplay = true;
			tasksToDisplay = output;

		} else {
			output = new ArrayList<Task>(this.tasksTimed);
			for (int i = 0; i < tasksUntimed.size(); i++) {
				output.add(tasksUntimed.get(i));
			}
			isDisplay = false;
		}

		// add all tasks from Timed task list and Untimed task list to the
		// output list
		return output;
	}
	
	/**
	 * This method will let tasks in listToCheck  check if 
	 * they are overdue, and update their properties.
	 * 
	 * @param listToCheck the list to be checked
	 */
	public void checkOverdue(List<Task> listToCheck) {
		for (Task task : listToCheck) {
			try {
				task.checkOverdue();
			} catch (TaskInvalidDateException e) {
				logger.log(Level.WARNING,
						"Invalid Deadline when checking Overdue!");
			}
		}
	}

	public boolean isShowingDone() {
		return this.isShowingDone;
	}
	public void setNotShowingDone() {
		this.isShowingDone = false;
	}
	
	public void export() {
		Storage.export(tasksTimed, tasksUntimed, tasksFinished);
	}
	
	
	/**
	 * @param taskIndex
	 * @return
	 */
	public boolean isInvalidIndex(int taskIndex) {
		return (taskIndex > this.countUndone()) || (taskIndex <= 0);
	}

	/**
	 * This method reverts the last operation that changed the TaskList.
	 * 
	 * @throws UndoException   if there is no operation to undo 
	 */
	public void undo() throws UndoException {
	    if (undoStack.isEmpty()) {
	        throw new UndoException();
	    } else {
	        
	        setShowDisplayListToFalse();
	        LastState lastState = undoStack.pop();
	        redoStack.push(lastState);
	        
	        if (lastState.getLastCommand() == LastCommand.ADD) {
	            Task task = lastState.getPreviousTaskState();
	            this.deleteFromList(task);
	            
	        } else if (lastState.getLastCommand() == LastCommand.DELETE) {
	            List<Task> tasksToReadd = lastState.getPreviousTaskStateList();
	            for (Task task : tasksToReadd) {
	                this.addToList(task);
	            }
	            
	        } else if (lastState.getLastCommand() == LastCommand.DONE) {
	            List<Task> tasksAfterDone = lastState.getCurrentTaskStateList();
	            List<Task> repeatTaskList = lastState.getRepeatTaskList();
	            
	            for (Task doneTask : tasksAfterDone) {
	                this.markTaskUndone(doneTask);
	                this.tasksFinished.remove(doneTask);
	                this.totalFinished--;
	                this.addToList(doneTask);
	            }
	            
	            for (Task newRepeatTask : repeatTaskList) {
	                this.tasksTimed.remove(newRepeatTask);
	                this.totalTasks--;
	            }
	            
	        } else if (lastState.getLastCommand() == LastCommand.TAG) {
	            Task currentTaskState = lastState.getCurrentTaskState();
	            String tag = lastState.getTag();
	            
	            if (tag.isEmpty()) {
	                assert false;
	                
	            } else {
	                try {
                        untagGivenTask(currentTaskState, tag);
                    } catch (TaskTagException e) {
                        assert false;
                    }
                }
	            
	        } else if (lastState.getLastCommand() == LastCommand.UNTAG) {
	            Task currentTaskState = lastState.getCurrentTaskState();
	            Task previousTaskState = lastState.getPreviousTaskState();
	            String tag = lastState.getTag();
	            
	            try {
	                if (tag.isEmpty()) {
	                    List<String> tagsToReadd = previousTaskState.getTags();
	                    for (String tagToReadd : tagsToReadd) {
	                        tagGivenTask(currentTaskState, tagToReadd);
	                    }
	                } else {
	                    tagGivenTask(currentTaskState, tag);
	                }
	            } catch (TaskTagDuplicateException e) {
	                assert false;
	            }

	        } else if (lastState.getLastCommand() == LastCommand.EDIT) {
	            Task currentTaskState = lastState.getCurrentTaskState();
	            Task prevTaskState = lastState.getPreviousTaskState();
	            
	            for (int i = 0; i < this.totalTasks; i++) {
	                Task task = this.getTask(i);
	                if (task.equals(currentTaskState)) {
	                    deleteFromList(task);
	                    addToList(prevTaskState);
	                    break;
	                }
	            }
	            
	        } else {
	            // add on other undo operations with a new else if statement,
	            // should not actually reach here
	            assert false;
	        }
	    }
	}
	
	/**
	 * This method reverts the undo operation done.
	 * 
	 * @throws RedoException   if there is no operation undid
	 */
    public void redo() throws RedoException{
        if (redoStack.isEmpty()) {
            throw new RedoException();
        } else {
            
            setShowDisplayListToFalse();
            LastState lastState = redoStack.pop();
            undoStack.push(lastState);
            
            if (lastState.getLastCommand() == LastCommand.ADD) {
                Task task = lastState.getPreviousTaskState();
                this.addToList(task);
                
            } else if (lastState.getLastCommand() == LastCommand.DELETE) {
                List<Task> tasksToDelete = lastState.getPreviousTaskStateList();
                for (Task task : tasksToDelete) {
                    this.deleteFromList(task);
                }
                
            } else if (lastState.getLastCommand() == LastCommand.DONE) {
                List<Task> tasksAfterUndone = lastState.getCurrentTaskStateList();
                List<Task> repeatTaskList = lastState.getRepeatTaskList();

                for (Task doneTask : tasksAfterUndone) {
                    this.markTaskRedone(doneTask);
                    this.deleteFromList(doneTask);
                    ((SortedArrayList<Task>) this.tasksFinished).addOrder(doneTask);
                    this.totalFinished++;
                }
                
                for (Task newRepeatTask : repeatTaskList) {
                    this.addToList(newRepeatTask);
                }
                
            } else if (lastState.getLastCommand() == LastCommand.TAG) {
                Task currentTaskState = lastState.getCurrentTaskState();
                String tag = lastState.getTag();
                
                if (tag.isEmpty()) {
                    assert false;
                    
                } else {
                    try {
                        tagGivenTask(currentTaskState, tag);
                        
                    } catch (TaskTagDuplicateException e) {
                        assert false;
                    }
                }
                
            } else if (lastState.getLastCommand() == LastCommand.UNTAG) {
                Task currentTaskState = lastState.getCurrentTaskState();
                String tag = lastState.getTag();
                
                try {
                    if (tag.isEmpty()) {
                        List<String> tagsToUntag = currentTaskState.getTags();
                        for (String tagToUntag : tagsToUntag) {
                            untagGivenTask(currentTaskState, tagToUntag);
                        }
                    } else {
                        untagGivenTask(currentTaskState, tag);
                    }
                } catch (TaskTagException e) {
                    assert false;
                }
                
            } else if (lastState.getLastCommand() == LastCommand.EDIT) {
                Task currentTaskState = lastState.getCurrentTaskState();
                Task prevTaskState = lastState.getPreviousTaskState();
                
                for (int i = 0; i < this.totalTasks; i++) {
                    Task task = this.getTask(i);
                    if (task.equals(prevTaskState)) {
                        deleteFromList(task);
                        addToList(currentTaskState);
                        break;
                    }
                }
                
            } else {
                // add on other redo operations with a new else if statement,
                // should not actually reach here
                assert false;
            }
            
        }
        
    }
	
	public RepeatedTask getLatestConsecutiveRepeatedTask(RepeatedTask repeatedTask) {
	    RepeatedTask consecutiveTask = null;
	    for (int j = this.tasksTimed.size() - 1; j >= 0; j--) {
	        Task timedTask = this.tasksTimed.get(j);
	        if (timedTask instanceof RepeatedTask) {
	            if (((RepeatedTask) timedTask).isConsecutiveTasks(repeatedTask)) {
	                consecutiveTask = (RepeatedTask) timedTask;
	                break;
	            }
	        }
	    }
	    if (consecutiveTask == null) {
	        assert false;
	    } 
	    return consecutiveTask;
	}

	/**
	 * This method creates a LastState object to undo for add command
	 * 
	 * @param cmd
	 * @param task
	 */
	private void addToUndoList(LastCommand cmd, Task task) {
	    LastState currentTaskState = new LastState(cmd, task);
	    undoStack.push(currentTaskState);
	}

	/**
	 * This method creates a LastState object to undo for edit command
	 * 
	 * @param cmd
	 * @param taskPrev
	 * @param taskNext
	 */
    private void addToUndoList(LastCommand cmd, Task taskPrev, Task taskNext) {
        LastState currentTaskState = new LastState(cmd, taskPrev, taskNext);
        undoStack.push(currentTaskState);        
    }

    /**
     * This method creates a LastState object to undo for delete command
     * 
     * @param cmd
     * @param taskListPrev
     */
    private void addToUndoList(LastCommand cmd, List<Task> taskListPrev) {
        LastState currentTaskState = new LastState(cmd, taskListPrev);
        undoStack.push(currentTaskState);
    }

    /**
     * This method creates a LastState object to undo for mark done command
     * 
     * @param cmd
     * @param taskListPrev
     * @param taskListNext
     * @param repeatTaskList
     */
    private void addToUndoList(LastCommand cmd, List<Task> taskListPrev, 
                               List<Task> taskListNext, List<Task> repeatTaskList) {
        LastState currentTaskState = new LastState(cmd, taskListPrev, taskListNext, repeatTaskList);
        undoStack.push(currentTaskState);
    }

    /**
     * This method creates a LastState object to undo for tagging commands
     * 
     * @param cmd
     * @param taskPrev
     * @param taskNext
     * @param tag
     */
    private void addToUndoList(LastCommand cmd, Task taskPrev, Task taskNext, String tag) {
        LastState currentTaskState = new LastState(cmd, taskPrev, taskNext, tag);
        undoStack.push(currentTaskState);        
    }

	public int count() {
		return this.totalTasks;
	}
	
	public int countUndone() {
		return this.countTimedTask()+this.countUntimedTask();
	}

	public int countTimedTask() {
		return this.tasksTimed.size();
	}

	public int countUntimedTask() {
		return this.tasksUntimed.size();
	}

	public int countFinished() {
		return this.totalFinished;
	}
	
	/**
	 * This method find out the separation index between non-floating task and
	 * floating task.
	 * If not found, return -1;
	 * 
	 * @param taskList
	 * @return
	 */
	public int indexOfFirstFloatingTask(List<Task> taskList) {
		for (int i = 0; i < taskList.size(); i++) {
			Task task = taskList.get(i);
			if (task instanceof FloatingTask) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * This method compares two TaskLists, 
	 * and returns true if all tasks in both task lists are equal
	 * This method is used for testing purposes
	 *  
	 * @param t2   The TaskList to compare with
	 * @return     true if the tasklists are equal, false otherwise
	 */
	public boolean isEqual(TaskList t2) {
	    boolean isEqual = true;
	    
	    isEqual = isEqual && 
	              (this.countFinished() == t2.countFinished()) &&
	              (this.countTimedTask() == t2.countTimedTask()) &&
	              (this.countUntimedTask() == t2.countUntimedTask());
	    
	    int i = 0;
	    int j = 0;
	    while (isEqual && (i < this.count())) {
	        Task thisTask = this.getTask(i);
	        Task thatTask = t2.getTask(i);

    	    try {
    	        if (thisTask instanceof DeadlineTask) {
    	            isEqual = (thisTask.getDescription().equals(thatTask.getDescription())) &&
    	                      (thatTask instanceof DeadlineTask) &&
                              (thisTask.getDeadline().equals(thatTask.getDeadline())) &&
                              (thisTask.getAddedTime().equals(thatTask.getAddedTime()));
    	            
    	        } else if (thisTask instanceof FixedTask) {
                    isEqual = (thisTask.getDescription().equals(thatTask.getDescription())) &&
                              (thatTask instanceof FixedTask) &&
                              (thisTask.getDeadline().equals(thatTask.getDeadline())) &&
                              (((FixedTask) thisTask).getStartTime().equals(((FixedTask) thatTask).getStartTime())) &&
                              (thisTask.getAddedTime().equals(thatTask.getAddedTime()));

    	        } else if (thisTask instanceof RepeatedTask) {
                    isEqual = (thisTask.getDescription().equals(thatTask.getDescription())) &&
                              (thatTask instanceof RepeatedTask) &&
                              (thisTask.getDeadline().equals(thatTask.getDeadline())) &&
                              (thisTask.getAddedTime().equals(thatTask.getAddedTime())) &&
                              (((RepeatedTask) thisTask).getRepeatPeriod().equals(((RepeatedTask) thatTask).getRepeatPeriod()));
                    
    	        } else if (thisTask instanceof FloatingTask) {
    	            isEqual = (thisTask.getDescription().equals(thatTask.getDescription())) &&
    	                      (thatTask instanceof FloatingTask) &&
    	                      (thisTask.getAddedTime().equals(thatTask.getAddedTime()));
    	            
    	        } else {
    	            assert false;
    	        }
    	        i++;
    	    } catch (TaskInvalidDateException e) {
    	        assert false;
    	    }
    	    
	    }
	    
	    while (isEqual && (j < this.countFinished())) {
	        isEqual = this.getFinishedTasks().get(j).getDoneDate().equals(t2.getFinishedTasks().get(j).getDoneDate());
	        j++;
	    }
	    
	    
	    return isEqual;
	}

	/**
	 * This method clears both the undoStack and the redoStack
	 * This method is used for testing purposes
	 */
	public void clearUndoRedoStack() {
	    undoStack.clear();
	    redoStack.clear();
	}
}


