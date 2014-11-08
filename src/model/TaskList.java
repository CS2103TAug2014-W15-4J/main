package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import log.ULogger;

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
	
	//@author A0119446B
	/**
	 * This Comparator is used in outputting task list order by deadline
	 */
	static class DeadlineComparator implements Comparator<Task> {

		@Override
		public int compare(Task o1, Task o2) {
			try {
				return o1.getDeadline().compareTo(o2.getDeadline());
			} catch (TaskInvalidDateException e) {
				logger.warning("Error comparing deadline.");
			}
			return 0;
		}
	}
	
	//@author A0119446B
	/**
	 * This Comparator is used in outputting task list order by added time
	 */
	static class AddedDateComparator implements Comparator<Task> {

		@Override
		public int compare(Task o1, Task o2) {
			return o1.getAddedTime().compareTo(o2.getAddedTime());
		}
	}
	
	//@author A0115384H
	/**
	 * This Comparator is used in outputting task list order by done time
	 */
	static class DoneDateComparator implements Comparator<Task> {
	    
	    @Override
	    public int compare(Task o1, Task o2) {
	        return o1.getDoneDate().compareTo(o2.getDoneDate());
	    }
	}

	//@author A0119446B
	private static ULogger logger = ULogger.getLogger();

	//@author A0115384H
	static Stack<LastState> undoStack = new Stack<LastState>();
	static Stack<LastState> redoStack = new Stack<LastState>();
	
	//@author A0119446B
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
	private int totalTasksOngoing;
	
	@XStreamAlias("Tags")
	private HashMap<String, List<Task>> tags;

	// count the total number of tasks finished.
	@XStreamAlias("TotalTaskFinished")
	private int totalFinished;

	/**
	 * This constructor creates a new TaskList object, and initialises all values. 
	 */
	public TaskList() {

		this.tasksTimed = new SortedArrayList<Task>(new DeadlineComparator());
		this.tasksUntimed = new SortedArrayList<Task>(new AddedDateComparator());
		this.tasksFinished = new SortedArrayList<Task>(new DoneDateComparator());
		this.tasksToDisplay = new ArrayList<Task>();
		this.tasksRepeated = new ArrayList<Task>();
		this.totalTasksOngoing = this.tasksTimed.size() + this.tasksUntimed.size();
		this.tags = new HashMap<String, List<Task>>();
		this.totalFinished = 0;
		this.isDisplay = false;
	}

	/**
	 * This method sets the isDisplay attribute to false when called.
	 * When isDisplay is set to false, the whole list is displayed.
	 */
	public void setShowDisplayListToFalse() {
		// lazy evaluation
		if (this.tasksToDisplay == null) {
			this.tasksToDisplay = new ArrayList<Task>();
		}
		this.isDisplay = false;
		this.tasksToDisplay.clear();
	}

	//@author A0115384H
	/**
	 * This method returns the Task with the specified task index.
	 * 
	 * @param taskIndex    The task index of the task to get.
	 * @return             The Task with the specified task index.
	 */
	public Task getTask(int taskIndex) {
		if (isDisplay) {
			if ((taskIndex >= tasksToDisplay.size()) || (taskIndex < 0)) {
				throw new TaskInvalidIdException("Error index for editing!");
			} else {
				return tasksToDisplay.get(taskIndex);
			}
		} else {
			if ((taskIndex >= totalTasksOngoing) || (taskIndex < 0)) {
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
	 * This method returns task index of the specified Task.
	 * 
	 * @param task     The specified Task to get the task index of.
	 * @return         The task index of the task. 
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
	 * This method adds a task to the corresponding lists in its position.
	 * This method is called by markTaskDone, if task is a repeat task;
	 * This method is called by undo, to revert the deleting of tasks.
	 * 
     * @param task      The task to be added.
     */
	private void addToList(Task task) {
	    
	    if (task.getIsDone()) {
	        ((SortedArrayList<Task>) this.tasksFinished).addOrder(task);
	        this.totalFinished++;
	        
	    } else {
    		if (task instanceof FloatingTask) {
    			((SortedArrayList<Task>) this.tasksUntimed).addOrder(task);
    
    		} else {
    			((SortedArrayList<Task>) this.tasksTimed).addOrder(task);
    			addToTaskRepeated(task);
    		}
    		this.totalTasksOngoing++;
	    }
	    
	    logger.info("task added");
	}
	
	//@author A0119446B
	/**
	 * This method adds a floating task.
	 * 
	 * @param description  the description of the task.
	 */
	public void addToList(String description) {
		Task newTask = new FloatingTask(description);
		this.tasksUntimed.add(newTask);
		this.totalTasksOngoing++;
		logger.info("A floating task added");
		
		addToUndoList(LastCommand.ADD, newTask);
	}

	/**
	 * This method adds a deadline task.
	 * 
	 * @param description  the description of the task.
	 * @param time         the deadline of the task.
	 */
	public void addToList(String description, Date time) {
		Task newTask = new DeadlineTask(description, time);
		((SortedArrayList<Task>) this.tasksTimed).addOrder(newTask);
		this.totalTasksOngoing++;
		logger.info("A deadline task added");
		
		addToUndoList(LastCommand.ADD, newTask);
	}

	/**
	 * This method adds a Repeated Task.
	 * 
	 * @param description  the description of the task.
	 * @param time         the deadline of the task.
	 * @param repeatDate   the repeat frequency.
	 */
	public void addToList(String description, Date time, RepeatDate repeatDate) {
		Task newTask = new RepeatedTask(description, time, repeatDate);
		((SortedArrayList<Task>) this.tasksTimed).addOrder(newTask);
		this.tasksRepeated.add(newTask);
		this.totalTasksOngoing++;
		logger.info("A repeated task added");
		
		addToUndoList(LastCommand.ADD, newTask);
	}

	/**
	 * This method adds a fixed (timed) task.
	 * 
	 * @param description  the description of the task.
	 * @param startTime    the start time of the task.
	 * @param endTime      the deadline of the task.
	 * @throws TaskInvalidDateException    if the start time is after the end time.
	 */
	public void addToList(String description, Date startTime, Date endTime)
			throws TaskInvalidDateException {
		Task newTask = new FixedTask(description, startTime, endTime);

		if (!startTime.before(endTime)) {
			throw new TaskInvalidDateException(
					"Invalid: Start date/time cannot be after end date/time.");

		} else {

			((SortedArrayList<Task>) this.tasksTimed).addOrder(newTask);
			this.totalTasksOngoing++;
			logger.info("A fixed task added");
			
			addToUndoList(LastCommand.ADD, newTask);
		}
	}
	
	//@author A0115384H
    /**
     * This method edits the description of a task in the file.
     *  
     * @param taskIndex    The task index of the task to be edited.
     * @param description  The new description to be entered.
     * @return the original description
	 */
	public String editTaskDescriptionOnly(int taskIndex, String description) {

        if (isInvalidIndex(taskIndex)) {
            throw new TaskInvalidIdException();
            
        } else {
            Task taskToEdit = getTask(taskIndex - 1);
            String oldDescription = taskToEdit.getDescription();
            
            Task clonedTask = taskToEdit.clone();
            editTaskDescription(taskToEdit, description);
            addToUndoList(LastCommand.EDIT, clonedTask, taskToEdit);

            logger.info("Task description edited");            
            return oldDescription;
        }
	}
	
	/**
     * This method edits the time of a task in the file (non-fixed/floating tasks).
     * 
     * @param taskIndex    The task index of the task to be edited.
     * @param time         The new time to be entered.
	 * @throws TaskInvalidIdException      if the task index entered is invalid.
	 * @throws TaskInvalidDateException    if the date entered is invalid.
	 */
    public void editTaskDeadlineOnly(int taskIndex, Date time) throws TaskInvalidIdException, TaskInvalidDateException {
        if (isInvalidIndex(taskIndex)) {
            throw new TaskInvalidIdException();
            
        } else {
            Task taskToEdit = getTask(taskIndex - 1);
            Task clonedTask = taskToEdit.clone();
            editTaskDeadline(taskToEdit, time);
            addToUndoList(LastCommand.EDIT, clonedTask, taskToEdit);
            logger.info("Task deadline edited");
        }
	}
	
    /**
     * This method edits both the time and description of a task in the file (non-fixed/floating tasks).
     * 
     * @param taskIndex    The task index of the task to be edited.
     * @param desc         The new description to be entered.
     * @param time         The new time to be entered.
     * @throws TaskInvalidIdException      if the task index entered is invalid.
     * @throws TaskInvalidDateException    if the date entered is invalid.
     */
	public void editTaskDescriptionDeadline(int taskIndex, String desc, Date time) throws TaskInvalidIdException, TaskInvalidDateException {
        if (isInvalidIndex(taskIndex)) {
            throw new TaskInvalidIdException();
            
        } else {
            Task taskToEdit = getTask(taskIndex - 1);
            Task clonedTask = taskToEdit.clone();
            editTaskDescription(taskToEdit, desc);
            editTaskDeadline(taskToEdit, time);
            addToUndoList(LastCommand.EDIT, clonedTask, taskToEdit);
            logger.info("Task description and deadline edited");
        }
	}
	
	/**
     * This method edits the times of a fixed task.
     * 
     * @param taskIndex    The task index of the task to be edited.
     * @param startDate    The new start time of the task.
     * @param endDate      The new end time of the task.
     * @throws TaskInvalidIdException      if the task index entered is invalid.
     * @throws TaskInvalidDateException    if the date entered is invalid.
	 */
	public void editTaskTimes(int taskIndex, Date startDate, Date endDate) throws TaskInvalidIdException, TaskInvalidDateException {
        if (isInvalidIndex(taskIndex)) {
            throw new TaskInvalidIdException();
            
        } else {
            Task taskToEdit = getTask(taskIndex - 1);
            Task clonedTask = taskToEdit.clone();
            editTaskStartDate(taskToEdit, startDate);
            editTaskDeadline(taskToEdit, endDate);
            addToUndoList(LastCommand.EDIT, clonedTask, taskToEdit);
            logger.info("Task start/end times edited");
        }
	}
	
	/**
     * This method edits both the description and times of a fixed task.
     * 
     * @param taskIndex    The task index of the task to be edited.
     * @param desc         The new description to be entered.
     * @param startDate    The new start time of the task.
     * @param endDate      The new end time of the task.
     * @throws TaskInvalidIdException      if the task index entered is invalid.
     * @throws TaskInvalidDateException    if the date entered is invalid.
	 */
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
            logger.info("Task description and start/end times edited");
        }
	}
	
	/**
     * This method edits the description of a task in the file.
     *  
	 * @param taskToEdit   The task object to be edited.
     * @param description  The new description to be entered.
	 * @throws TaskInvalidIdException  if the task index entered is invalid.
	 */
	public void editTaskDescription(Task taskToEdit, String description)
			throws TaskInvalidIdException {

	    int indexToEdit = getTaskIndex(taskToEdit);
	    
	    // if the index comes from a list used for displaying, 
	    // use time to find
	    if (isDisplay) {
	        boolean isFound = false;
	        // trace the task by added time.
	        for (Task task : this.tasksTimed) {
	            if (task.getAddedTime().equals(taskToEdit.getAddedTime())) {
	                task.setDescription(description);
	                isFound = true;
	                break;
	            }
	        }
	        if (!isFound) {
	            for (Task task : this.tasksUntimed) {
	                if (task.getAddedTime().equals(taskToEdit.getAddedTime())) {
	                    task.setDescription(description);
	                    break;
	                }
	            }
	        }
	        
	    } else {
	        if (indexToEdit < tasksTimed.size()) {
	            this.tasksTimed.get(indexToEdit).setDescription(description);
	            
	        } else {
	            // update the index to the proper value in tasksUntimed.
	            indexToEdit -= tasksTimed.size();
	            this.tasksUntimed.get(indexToEdit).setDescription(description);
	        }
	    }
	}

	/**
     * This method edits the time of a task in the file (non-fixed/floating tasks).
     * 
     * @param taskToEdit    The task object to be edited.
     * @param time          The new time to be entered.
     * @throws TaskInvalidIdException      if the task index entered is invalid.
     * @throws TaskInvalidDateException    if the date entered is invalid.
	 */
	public void editTaskDeadline(Task taskToEdit, Date time)
			throws TaskInvalidIdException, TaskInvalidDateException {

	    int indexToEdit = getTaskIndex(taskToEdit);
	    
	    // if the index comes from a list used for displaying, use time to
	    // find
	    if (isDisplay) {
	        boolean isFound = false;
	        // trace the task by added time.
	        for (Task task : this.tasksTimed) {
	            if (task.getAddedTime().equals(taskToEdit.getAddedTime())) {
	                task.setDeadline(time);
	                isFound = true;
	                break;
	            }
	        }
	        if (!isFound) {
	            for (Task task : this.tasksUntimed) {
	                if (task.getAddedTime().equals(
	                                               taskToEdit.getAddedTime())) {
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

	/**
	 * This method edits the start time of a task in the file.
	 * 
	 * @param taskToEdit   The task object to be edited.
	 * @param startDate    The new start date to be entered.
	 * @throws TaskInvalidIdException  if the task index entered is invalid.
	 */
	public void editTaskStartDate(Task taskToEdit, Date startDate)
			throws TaskInvalidIdException {

	    int indexToEdit = getTaskIndex(taskToEdit);
	    
	    // if the index comes from a list used for displaying, use time to
	    // find
	    if (isDisplay) {
	        boolean isFound = false;
	        // trace the task by added time.
	        for (Task task : this.tasksTimed) {
	            if (task.getAddedTime().equals(taskToEdit.getAddedTime())) {
	                task.setStartTime(startDate);
	                isFound = true;
	                break;
	            }
	        }
	        if (!isFound) {
	            for (Task task : this.tasksUntimed) {
	                if (task.getAddedTime().equals(taskToEdit.getAddedTime())) {
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
	
	/**
	 * This method is called by undo, to revert the adding of tasks.
	 * 
	 * @param task     The task that was added and is to be deleted. 
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
	            this.totalTasksOngoing--;
	            logger.info("Deleted a task");
	        }
	    }
	}

	/**
	 * This method deletes the specified tasks from the file.
	 * 
	 * @param taskIndexList    The list of task indices of tasks to be deleted.
	 * @throws TaskInvalidIdException  if the task index entered is invalid. 
	 */
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
					this.totalTasksOngoing--;
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

						this.totalTasksOngoing--;
					}
				}	
			}
			addToUndoList(LastCommand.DELETE, tasksRemoved);
		}
	}
	
	/**
	 * This method deletes the specified task from tasksRepeat list,
	 * if the list contains the task and the task is a RepeatedTask.
	 *  
	 * @param taskToRemove The task to be removed from tasksRepeat.
	 */
	private void deleteFromTasksRepeated(Task taskToRemove) {
	    if (taskToRemove instanceof RepeatedTask) {
	        if (tasksRepeated.contains(taskToRemove)) {
	            tasksRepeated.remove(taskToRemove);
	            logger.info("deleted a task from the repeated list.");
	        }
	    }
	}
	
	/**
	 * This method adds the specified task to tasksRepeat list,
	 * if the list does not contain the task and the task is a RepeatedTask.
	 * 
	 * @param taskToAdd    The task to be added to tasksRepeat.
	 */
	private void addToTaskRepeated(Task taskToAdd) {
	    if (taskToAdd instanceof RepeatedTask) {
	        if (!tasksRepeated.contains(taskToAdd)) {
	            tasksRepeated.add(taskToAdd);
	            logger.info("added a task to the repeated list.");
	        }
	    }
	}

	/**
	 * This method clears the TaskList, and all its tasks.
	 */
    public void clearList() {
        ArrayList<Task> tasksRemoved = new ArrayList<Task>();
        tasksRemoved.addAll(tasksUntimed);
        tasksRemoved.addAll(tasksTimed);
        tasksRemoved.addAll(tasksFinished);
        addToUndoList(LastCommand.CLEAR, tasksRemoved);
        
		this.isDisplay = false;
		this.tasksUntimed.clear();
		this.tasksTimed.clear();
		this.tasksUntimed.clear();
		this.tasksFinished.clear();
		this.tasksRepeated.clear();
		this.tags.clear();
		this.totalTasksOngoing = 0;
		this.totalFinished = 0;
		
		logger.info("Task list cleared");
	}

    /**
     * This method marks done the specified tasks from the file.
     * 
     * @param taskIndexList     The list of task indices of tasks to be marked done.
     * @throws TaskDoneException        if the task is already done.
     * @throws TaskInvalidIdException   if the task index given is invalid.
     */
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
		        if (target.getIsDone()) {
		            throw new TaskDoneException();
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
		        this.totalTasksOngoing--;
		        if (newRepeatTask != null) {
		            this.addToList((RepeatedTask) newRepeatTask);
		            newRepeatTaskList.add(newRepeatTask);
		            deleteFromTasksRepeated(target);
		        }
		    }
		    
		    addToUndoList(LastCommand.DONE, tasksBeforeMarkingDone, tasksToMarkDone, newRepeatTaskList);
		    logger.info("Tasks marked done");
		}
	}
	
	/**
	 * This method is called by redo, to revert the undo on marking tasks done.
	 * 
	 * @param task     The task that is to be re-marked done .
	 */
	private void markTaskRedone(Task task) {
	    task.markRedone();
	    logger.info("task has been marked redone.");
	}
	
	/**
	 * This method is called by undo, to revert the marking done of tasks.
	 * 
	 * @param task     The task that is to be marked un-done.
	 */
	private void markTaskUndone(Task task) {
	    task.markUndone();
	       logger.info("task has been marked undone.");
	}

	/**
	 * This method assigns the tag (non case-sensitive) to a specified task.
	 * 
	 * @param taskIndexToTag   The task index of the task to be tagged.
     * @param tag              The tag to be assigned.
     * @throws TaskInvalidIdException      if the task index given is invalid.
	 * @throws TaskTagDuplicateException   if the task already contains the specified tag.
	 */
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
	        logger.info("Task tagged.");
	    }
	}
	
	/**
	 * This method attaches a tag to the task.
	 * This method is called by tagTask, when tagging a task;
	 * This method is called by undo, when reverting the untag operation;
	 * and is called by redo, when reverting the undo of the tag operation.
	 * 
	 * @param taskToTag    The task that is to be tagged.
	 * @param tag          The tag to be attached to the task.
	 * @throws TaskTagDuplicateException   if the task already has the tag.
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

    /**
     * This method removes a tag from the task.
     * 
     * @param taskIndexToTag   The task index of the task to be tagged.
     * @param tag              The tag to be assigned.
     * @throws TaskInvalidIdException   if the task index given is invalid. 
     * @throws TaskTagException         if the task does not have the tag to remove.
     */
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
            logger.info("Task untagged");
	    }
	}
	
	/**
	 * This method removes a tag from the task. 
	 * If no tag is given, all tags from the given task are removed.
	 *  
	 * @param taskToUntag  The task that is to be untagged.
	 * @param tag          The tag to be removed from the task.
	 * @throws TaskTagException    if the task does not have the tag to remove.
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
     * @param taskToUntag   The task that is to be untagged.
     * @throws TaskTagException     if the task does not have any tags to remove.
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
	
	//@author A0119446B
	public List<Task> getFinishedTasks() {
		this.isShowingDone = true;
		return this.tasksFinished;
	}
	
	//@author A0119414L
	/**
	 * This method checks if the tag is contained in the task list.
	 * 
	 * @param tag  The specified tag to check.	
	 * @return     true if tag is contained, false otherwise.
	 */
	public boolean isTagContained(String tag) {
		return tags.containsKey(tag.toLowerCase());
	}
	
	//@author A0115384H
	/**
	 * This method returns a list of tasks that contain the specified tag.
	 * 
	 * @param tag  The specified tag.
	 * @return     The list of tasks with the tag.
	 * @throws TaskNoSuchTagException  if the TaskList does not contain this tag.
	 */
	public List<Task> getTasksWithTag(String tag) throws TaskNoSuchTagException {
		if (tags.containsKey(tag.toLowerCase())) {
			List<Task> taskListOfTag = tags.get(tag.toLowerCase());
			return taskListOfTag;
		} else {
			throw new TaskNoSuchTagException();
		}
	}
	
	
	//@author A0119446B
	/**
	 * This method searches for tasks by a given keyword.
	 * This method will find the keyword in a task's description as well as tags.
	 * 
	 * @param keyword  The keyword for searching.
	 * @return         A list of results.
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
	 * This method will return the tasks if the deadline is within the given range. 
	 * For displaying purpose, all the floating tasks will be added as well.
	 * 
	 * @param showDate The input time range.
	 * @return         A list of tasks that satisfies the range.
	 * @throws TaskInvalidDateException    if the dates are invalid.
	 */
	public List<Task> getDateRangeTask(List<Date> showDate) throws TaskInvalidDateException {
		assert(showDate.size() == 2);
		
		SortedArrayList<Task> output = new SortedArrayList<Task>(new DeadlineComparator());
		Date startTime = showDate.get(0);
		Date endTime = showDate.get(1);
		
		// shows overdue tasks at the top
		List<Task> overdue = getOverdueTask();
		output.addAllUnordered(overdue);

		// find task within the date range
		for (Task task : tasksTimed) {
			if (task instanceof RepeatedTask) {
		        // repeated tasks are checked separately
		    } else if ((task.getDeadline().after(startTime) || task.getDeadline().equals(startTime)) && 
					(task.getDeadline().before(endTime) || task.getDeadline().equals(endTime))) {

				output.addUnique(task);
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
		        if (sameHour(taskTimeCal, searchTimeStartCal, searchTimeEndCal) &&
		           (task.getDeadline().before(endTime) || task.getDeadline().equals(endTime))) {
		            
		            output.addUnique(repeatedTask);
		        }

		    } else if (periodString.split(" ")[0].equals("every")) {

		        if (sameHour(taskTimeCal, searchTimeStartCal, searchTimeEndCal) &&
		            sameWeekday(taskTimeCal, searchTimeStartCal, searchTimeEndCal) &&
		            task.getDeadline().before(endTime) || task.getDeadline().equals(endTime)) {
		            
		            output.addUnique(repeatedTask);
		        }

            } else if (periodString.split(" ")[0].equals("day")) {
                
                if (sameHour(taskTimeCal, searchTimeStartCal, searchTimeEndCal) &&
                    sameWeekday(taskTimeCal, searchTimeStartCal, searchTimeEndCal) &&
                    sameMonthDay(taskTimeCal, searchTimeStartCal, searchTimeEndCal) &&
                    task.getDeadline().before(endTime) || task.getDeadline().equals(endTime)) {

                    output.addUnique(repeatedTask);
                }
            } 
		}
		
		// add all floating task
		output.addAllUnordered(tasksUntimed);
		isDisplay = true;
		tasksToDisplay = output;
		return output;
	}

	//@author A0115384H
	/**
	 * This method checks if the time of the task is between the times of the two calendars
	 * 
	 * @param taskTimeCal          Calendar representing the time of the task
	 * @param searchTimeStartCal   Calendar representing the start time being searched
	 * @param searchTimeEndCal     Calendar representing the end time being searched
	 * @return true if the time of the task being checked is between the other two times
	 */
	private boolean sameHour(Calendar taskTimeCal, Calendar searchTimeStartCal,
                             Calendar searchTimeEndCal) {
	    return (taskTimeCal.get(Calendar.HOUR_OF_DAY) >= searchTimeStartCal.get(Calendar.HOUR_OF_DAY) &&
                taskTimeCal.get(Calendar.HOUR_OF_DAY) <= searchTimeEndCal.get(Calendar.HOUR_OF_DAY));
    }

	 /**
     * This method checks if the day of the task is within the days of the two calendars
     * 
     * @param taskTimeCal          Calendar representing the day of the task
     * @param searchTimeStartCal   Calendar representing the start day being searched
     * @param searchTimeEndCal     Calendar representing the end day being searched
     * @return true if the day of the task being checked is between the other two days
     */
    private boolean sameWeekday(Calendar taskTimeCal, Calendar searchTimeStartCal,
                                Calendar searchTimeEndCal) {
        int startDay = searchTimeStartCal.get(Calendar.DAY_OF_WEEK);
        int endDay = searchTimeEndCal.get(Calendar.DAY_OF_WEEK);
        int taskDay = taskTimeCal.get(Calendar.DAY_OF_WEEK);
        
        if (startDay == 1) {
            startDay = 7;
        }
        if (endDay == 1) {
            endDay = 7;
        }
        if (taskDay == 1) {
            taskDay = 7;
        }
        
        return ((taskDay >= startDay) &&
                (taskDay <= endDay));
    }	

    /**
     * This method checks if the day of the task is between the days of the two calendars
     * 
     * @param taskTimeCal          Calendar representing the day of the task
     * @param searchTimeStartCal   Calendar representing the start day being searched
     * @param searchTimeEndCal     Calendar representing the end day being searched
     * @return true if the day of the task being checked is between the other two days
     */
    private boolean sameMonthDay(Calendar taskTimeCal, Calendar searchTimeStartCal,
                                 Calendar searchTimeEndCal) {
        return (taskTimeCal.get(Calendar.DAY_OF_MONTH) >= searchTimeStartCal.get(Calendar.DAY_OF_MONTH) &&
                taskTimeCal.get(Calendar.DAY_OF_MONTH) <= searchTimeEndCal.get(Calendar.DAY_OF_MONTH));
    }
    
    //@author A0119446B
    /**
	 * This methods will find out the tasks that are overdue.
	 * 
	 * @return A list of overdue tasks.
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
				logger.warning(
						"Invalid Deadline when checking Overdue!");
			}
		}
		return output;
	}
	
	/**
	 * This method returns a list of tasks containing the given tag.
	 * 
	 * @param tag  The given tag to check.
	 * @return     A list of tasks with that tag.
	 * @throws TaskNoSuchTagException  if the given tag is not found.
	 */
	public List<Task> prepareDisplayList(String tag)
			throws TaskNoSuchTagException {
		
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
	 * @param isDisplayedByAddTime Boolean to decide ordering (by added time or otherwise).
	 * @return                     The list of ordered tasks.
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
	 * This method will let tasks in listToCheck check if 
	 * they are overdue, and update their properties.
	 * 
	 * @param listToCheck the list to be checked
	 */
	private void checkOverdue(List<Task> listToCheck) {
		for (Task task : listToCheck) {
			try {
				task.checkOverdue();
			} catch (TaskInvalidDateException e) {
				logger.warning(
						"Invalid Deadline when checking Overdue!");
			}
		}
	}
	
	public void checkOverdue() {
		this.checkOverdue(tasksTimed);
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
	 * This method checks if the given task index is valid.
	 * 
	 * @param taskIndex    The task index to check.
	 * @return             true if task index is valid, false otherwise.
	 */
	public boolean isInvalidIndex(int taskIndex) {
	    if (isDisplay) {
	        return (taskIndex > this.tasksToDisplay.size()) || (taskIndex <= 0);
	    } else {
	        return (taskIndex > this.countUndone()) || (taskIndex <= 0);
	    }
	}

	//@author A0115384H
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
	            
	            logger.info("Operation undone: add");
	            
	        } else if (lastState.getLastCommand() == LastCommand.DELETE) {
	            List<Task> tasksToReadd = lastState.getPreviousTaskStateList();
	            for (Task task : tasksToReadd) {
	                this.addToList(task);
	            }
	            
                logger.info("Operation undone: delete");
	            
	        } else if (lastState.getLastCommand() == LastCommand.CLEAR) {
	            List<Task> tasksToReadd = lastState.getPreviousTaskStateList();
	            for (Task task : tasksToReadd) {
	                this.addToList(task);
	            }
	            
	            updateTagsHash(tasksToReadd);
	            logger.info("Operation undone: clear");
	            
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
	                this.totalTasksOngoing--;
	            }
	            
	            logger.info("Operation undone: mark done");
	            
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
	            
	            logger.info("Operation undone: tag");
	            
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
	            
	            logger.info("Operation undone: untag");

	        } else if (lastState.getLastCommand() == LastCommand.EDIT) {
	            Task currentTaskState = lastState.getCurrentTaskState();
	            Task prevTaskState = lastState.getPreviousTaskState();
	            
	            for (int i = 0; i < this.totalTasksOngoing; i++) {
	                Task task = this.getTask(i);
	                if (task.equals(currentTaskState)) {
	                    deleteFromList(task);
	                    addToList(prevTaskState);
	                    break;
	                }
	            }
	            
	            logger.info("Operation undone: edit");
	            
	        } else {
	            // add on other undo operations with a new else if statement,
	            // should not actually reach here
	            assert false;
	        }
	    }
	}
	
	/**
	 * This method updates the <tags: tasks> pairs in the tags HashMap.
	 * This method is only called by undo to revert the clear command.
	 * 
	 * @param tasksToReadd List of tasks that are re-added after the clear command
	 */
	private void updateTagsHash(List<Task> tasksToReadd) {
        for (Task task : tasksToReadd) {
            List<String> taskTags = task.getTags();
            for (String tag : taskTags) {
                if (tags.containsKey(tag)) {
                    tags.get(tag).add(task);
                    
                } else {
                    List<Task> taskWithTag = new ArrayList<Task>();
                    taskWithTag.add(task);
                    tags.put(tag, taskWithTag);
                }
            }
        }
        
        logger.info("updating the tags hashmap");
        
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
                
                logger.info("Operation redone: add");
                
            } else if (lastState.getLastCommand() == LastCommand.DELETE) {
                List<Task> tasksToDelete = lastState.getPreviousTaskStateList();
                for (Task task : tasksToDelete) {
                    this.deleteFromList(task);
                }
                
                logger.info("Operation redone: delete");
                
            } else if (lastState.getLastCommand() == LastCommand.CLEAR) {
                List<Task> tasksToDelete = lastState.getPreviousTaskStateList();
                for (Task task : tasksToDelete) {
                    this.deleteFromList(task);
                }                
                
                logger.info("Operation redone: clear");
                
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
                
                logger.info("Operation redone: done");
                
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
                
                logger.info("Operation redone: tag");
                
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
                
                logger.info("Operation redone: untag");
                
            } else if (lastState.getLastCommand() == LastCommand.EDIT) {
                Task currentTaskState = lastState.getCurrentTaskState();
                Task prevTaskState = lastState.getPreviousTaskState();
                
                for (int i = 0; i < this.totalTasksOngoing; i++) {
                    Task task = this.getTask(i);
                    if (task.equals(prevTaskState)) {
                        deleteFromList(task);
                        addToList(currentTaskState);
                        break;
                    }
                }
                
                logger.info("Operation redone: edit");
                
            } else {
                // add on other redo operations with a new else if statement,
                // should not actually reach here
                assert false;
            }            
        }
    }
	
    /**
     * This method returns the first consecutive repeated task found, given one repeated task.
     * Due to the ordering of tasks, the first consecutive repeated task found will always 
     * come after a second consecutive repeated task (if any). 
     * 
     * @param repeatedTask  The repeated task given, to search for consecutive repeated tasks.
     * @return              The consecutive repeated task if found, null otherwise.
     */
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
	 * This method creates a LastState object to undo for add command.
	 * 
	 * @param cmd  The command type.
	 * @param task The task state to store.
	 */
	private void addToUndoList(LastCommand cmd, Task task) {
	    LastState currentTaskState = new LastState(cmd, task);
	    undoStack.push(currentTaskState);
	}

	/**
	 * This method creates a LastState object to undo for edit command.
	 * 
	 * @param cmd      The command type.
	 * @param taskPrev The previous task state to store.
	 * @param taskNext The current task state to store.
	 */
    private void addToUndoList(LastCommand cmd, Task taskPrev, Task taskNext) {
        LastState currentTaskState = new LastState(cmd, taskPrev, taskNext);
        undoStack.push(currentTaskState);        
    }

    /**
     * This method creates a LastState object to undo for delete command.
     * 
     * @param cmd           The command type.
     * @param taskListPrev  The previous task list state to store.
     */
    private void addToUndoList(LastCommand cmd, List<Task> taskListPrev) {
        LastState currentTaskState = new LastState(cmd, taskListPrev);
        undoStack.push(currentTaskState);
    }

    /**
     * This method creates a LastState object to undo for mark done command.
     * 
     * @param cmd               The command type.
     * @param taskListPrev      The previous task list state to store.
     * @param taskListNext      The current task list state to store.
     * @param repeatTaskList    The repeatedTaskList to store, if any.
     */
    private void addToUndoList(LastCommand cmd, List<Task> taskListPrev, 
                               List<Task> taskListNext, List<Task> repeatTaskList) {
        LastState currentTaskState = new LastState(cmd, taskListPrev, taskListNext, repeatTaskList);
        undoStack.push(currentTaskState);
    }

    /**
     * This method creates a LastState object to undo for tagging commands.
     * 
     * @param cmd       The command type.
     * @param taskPrev  The previous task state to store.
     * @param taskNext  The current task state to store.
     * @param tag       The specified tag to (un)tag.
     */
    private void addToUndoList(LastCommand cmd, Task taskPrev, Task taskNext, String tag) {
        LastState currentTaskState = new LastState(cmd, taskPrev, taskNext, tag);
        undoStack.push(currentTaskState);        
    }
    
    //@author A0119446B
    /**
     * This method returns the number of ongoing tasks.
     * 
     * @return  the number of ongoing tasks.
     */
	public int count() {
		return this.totalTasksOngoing;
	}
	
	/**
	 * This method returns the number of undone tasks.
	 * 
	 * @return the number of undone tasks.
	 */
	public int countUndone() {
		return this.countTimedTask() + this.countUntimedTask();
	}

    /**
     * This method returns the number of ongoing timed tasks.
     * 
     * @return the number of ongoing timed tasks.
     */
	public int countTimedTask() {
		return this.tasksTimed.size();
	}

    /**
     * This method returns the number of ongoing untimed tasks.
     * 
     * @return the number of ongoing untimed tasks.
     */
	public int countUntimedTask() {
		return this.tasksUntimed.size();
	}
	
    /**
     * This method returns the number of finished tasks.
     * 
     * @return the number of finished tasks.
     */
	public int countFinished() {
		return this.totalFinished;
	}
	
	/**
	 * This method finds out the separation index between non-floating task and
	 * floating task.
	 * If not found, return -1;
	 * 
	 * @param taskList The task list to find the separation index of.
	 * @return         The separation index found, or -1 otherwise.
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

	//@author A0115384H
	/**
	 * This method compares two TaskLists, 
	 * and returns true if all tasks in both task lists are equal.
	 * This method is used for testing purposes.
	 *  
	 * @param t2   The TaskList to compare with.
	 * @return     true if the tasklists are equal, false otherwise.
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

	        isEqual = thisTask.equals(thatTask);
	        i++;
	    }
	    
	    while (isEqual && (j < this.countFinished())) {
	        Task thisFinishedTask = this.getFinishedTasks().get(j);
	        Task thatFinishedTask = t2.getFinishedTasks().get(j);
	        isEqual = thisFinishedTask.equals(thatFinishedTask) &&
	                  thisFinishedTask.getDoneDate().equals(thatFinishedTask.getDoneDate());
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
	
	/**
	 * This method adds a task to the task list, given the task.
	 * This method is used for testing purposes
	 * 
	 * @param task     Task to be added to the list 
	 */
	public void addTaskToTaskList(Task task) {
	    addToList(task);
	}
}


