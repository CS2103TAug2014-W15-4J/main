package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thoughtworks.xstream.annotations.XStreamAlias;

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
	@XStreamAlias("TaskToDisplay")
	private List<Task> tasksToDisplay;

	
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
		this.totalTasks = this.tasksTimed.size() + this.tasksUntimed.size();
		this.tags = new HashMap<String, List<Task>>();
		this.totalFinished = 0;
		this.isDisplay = false;
	}

	/**
	 * If setShowDisplayListToFalse is called, display the whole list.
	 */
	public void setShowDisplayListToFalse() {
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
     * @param task
     * 
     * this method is called by markTaskDone, if task is a repeat task
     * this method is called by undo, to undo the deleting of tasks.
     * this method will not call the addToUndoList function
     */
	private void addToList(Task task) {
	    if (task.getIsDone()) {
	        System.out.println(this.tasksFinished.getClass());
	        ((SortedArrayList<Task>) this.tasksFinished).addOrder(task);
	        
	    } else {
	        System.out.println(this.tasksUntimed.getClass());
    		if (task instanceof FloatingTask) {
    			((SortedArrayList<Task>) this.tasksUntimed).addOrder(task);
    
    		} else {
    			((SortedArrayList<Task>) this.tasksTimed).addOrder(task);
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
		
		addToUndoList(LastCommand.ADD, newTask, this.getTaskIndex(newTask));
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
		
		addToUndoList(LastCommand.ADD, newTask, this.getTaskIndex(newTask));
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
		this.totalTasks++;
		logger.log(Level.INFO, "A repeated task added");
		
		addToUndoList(LastCommand.ADD, newTask, this.getTaskIndex(newTask));
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
			
			addToUndoList(LastCommand.ADD, newTask, this.getTaskIndex(newTask));
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
	 * @param task
	 * 
	 * this method is called by undo, to undo the adding of tasks.
	 * this method will not call the addToUndoList function
	 */
	private void deleteFromList(Task task) {
	    if (tasksTimed.contains(task)) {
	        tasksTimed.remove(task);
	    } else {
	        tasksUntimed.remove(task);
	    }
	    
	    this.totalTasks--;
	    
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
						tasksRemoved.add(this.tasksFinished.remove(indexToRemove - 1));
					}
					this.totalTasks--;
				} else {
					if (isInvalidIndex(indexToRemove)) {

						throw new TaskInvalidIdException("Error index for deleting!");

					} else {
						Task taskToRemove = getTask(indexToRemove - 1);
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
			
			addToUndoList(LastCommand.DELETE, tasksRemoved, -1);
		}
	}

    public void clearList() {
        ArrayList<Task> tasksRemoved = new ArrayList<Task>();
        tasksRemoved.addAll(tasksUntimed);
        tasksRemoved.addAll(tasksTimed);
        tasksRemoved.addAll(tasksFinished);
        addToUndoList(LastCommand.DELETE, tasksRemoved, -1);
        
		this.isDisplay = false;
		this.tasksUntimed.clear();
		this.tasksTimed.clear();
		this.tasksUntimed.clear();
		this.tasksFinished.clear();
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
	
	private void markTaskRedone(Task task) {
	    task.markRedone();
	}
	
	private void markTaskUndone(Task task) {
	    task.markUndone();
	}

	public void tagTask(int taskIndexToTag, String tag)
			throws TaskInvalidIdException, TaskTagDuplicateException {
	    
	    if (isInvalidIndex(taskIndexToTag)) {
	        throw new TaskInvalidIdException();
	        
	    } else {
	        Task givenTaskToTag = getTask(taskIndexToTag - 1);
	        Task clonedTask = givenTaskToTag.clone();
	        tagGivenTask(givenTaskToTag, tag);
	        
	        addToUndoList(LastCommand.TAG, clonedTask, givenTaskToTag, tag);
	    }
	}
	
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
	        
	    } else {
	        Task givenTaskToUntag = getTask(taskIndexToUntag - 1);
	        Task clonedTask = givenTaskToUntag.clone();
	        untagGivenTask(givenTaskToUntag, tag);
            addToUndoList(LastCommand.UNTAG, clonedTask, givenTaskToUntag, tag);
	    }
	}
	
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
					System.out.println("find one");
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

	public List<Task> prepareDisplayList(String tag)
			throws TaskNoSuchTagException {

		if (tags.containsKey(tag.toLowerCase())) {

			tasksToDisplay = tags.get(tag.toLowerCase());
			// check overdue for each task
			for (Task task : tasksToDisplay) {
				try {
					task.checkOverdue();
				} catch (TaskInvalidDateException e) {
					logger.log(Level.WARNING,
							"Invalid Deadline when checking Overdue!");
				}
			}

			isDisplay = true;

			return tasksToDisplay;
		} else {
			throw new TaskNoSuchTagException();
		}

	}

	public List<Task> prepareDisplayList(boolean isDisplayedByAddTime) {
		List<Task> output;
		// check overdue for each task
		for (Task task : tasksTimed) {
			try {
				task.checkOverdue();
			} catch (TaskInvalidDateException e) {
				logger.log(Level.WARNING,
						"Invalid Deadline when checking Overdue!");
			}
		}

		if (isDisplayedByAddTime) {
			// using comparator AddedDateComparator
			output = new SortedArrayList<Task>(this.count(),
					new AddedDateComparator());
			output.addAll(tasksTimed);
			output.addAll(tasksUntimed);
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
		
	private void addToUndoList(LastCommand cmd, Task task, int taskIndex) {
	    LastState currentTaskState = new LastState(cmd, task, taskIndex);
	    undoStack.push(currentTaskState);
	}
	
    private void addToUndoList(LastCommand cmd, List<Task> tasks, int taskIndex) {
        LastState currentTaskState = new LastState(cmd, tasks, taskIndex);
        undoStack.push(currentTaskState);
        
    }
    
    private void addToUndoList(LastCommand cmd, Task taskPrev, Task taskNext) {
        LastState currentTaskState = new LastState(cmd, taskPrev, taskNext);
        undoStack.push(currentTaskState);        
    }
    
    private void addToUndoList(LastCommand cmd, List<Task> taskListPrev, 
                               List<Task> taskListNext, List<Task> repeatTaskList) {
        LastState currentTaskState = new LastState(cmd, taskListPrev, taskListNext, repeatTaskList);
        undoStack.push(currentTaskState);
    }
    
    private void addToUndoList(LastCommand cmd, Task taskPrev, Task taskNext, String tag) {
        LastState currentTaskState = new LastState(cmd, taskPrev, taskNext, tag);
        undoStack.push(currentTaskState);        
    }

	
    
    private void addToRedoList(LastCommand cmd, List<Task> taskListPrev, List<Task> taskListNext) {
        LastState currentTaskState = new LastState(cmd, taskListPrev, taskListNext);
        redoStack.push(currentTaskState);
        
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
}