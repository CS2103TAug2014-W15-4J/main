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

	public void editTaskDescription(int taskIndex, String description)
			throws TaskInvalidIdException {
		if (isInvalidIndex(taskIndex)) {
			throw new TaskInvalidIdException("Error index for editing!");
		} else {
			int indexToEdit = taskIndex - 1;
			Task taskToRemove = getTask(indexToEdit);

			// if the index comes from a list used for displaying, use time to
			// find
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
						if (task.getAddedTime().equals(
								taskToRemove.getAddedTime())) {
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
					this.tasksUntimed.get(indexToEdit).setDescription(
							description);
				}
			}
		}
	}

	public void editTaskDeadline(int taskIndex, Date time)
			throws TaskInvalidIdException, TaskInvalidDateException {
		if (isInvalidIndex(taskIndex)) {
			throw new TaskInvalidIdException("Error index for editing!");
		} else {
			int indexToEdit = taskIndex - 1;
			Task taskToRemove = getTask(indexToEdit);

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
			((SortedArrayList<Task>) this.tasksTimed)
					.updateListOrder(taskIndex - 1);
		}
	}

	public void editTaskStartDate(int taskIndex, Date startDate)
			throws TaskInvalidIdException {
		if (isInvalidIndex(taskIndex)) {
			throw new TaskInvalidIdException("Error index for editing!");

		} else {
			int indexToEdit = taskIndex - 1;
			Task taskToRemove = getTask(indexToEdit);

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
						if (task.getAddedTime().equals(
								taskToRemove.getAddedTime())) {
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
		    List<Task> tasksMarkedDone = new ArrayList<Task>();
		    
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
		        tasksMarkedDone.add(target.clone());
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
		        }
		    }
		    
		    addToUndoList(LastCommand.DONE, tasksMarkedDone, -1);
		}
	}

	public void tagTask(int taskIndexToTag, String tag)
			throws TaskInvalidIdException, TaskTagDuplicateException {
		if (isInvalidIndex(taskIndexToTag)) {
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

	public void untagTask(int taskIndexToUntag, String tag)
			throws TaskInvalidIdException, TaskTagException {
		if (isInvalidIndex(taskIndexToUntag)) {
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
	        
	        if (lastState.getLastCommand() == LastCommand.ADD) {
	            Task task = lastState.getPreviousTaskState();
	            deleteFromList(task);
	            
	        } else if (lastState.getLastCommand() == LastCommand.DELETE) {
	            List<Task> tasksToReadd = lastState.getPreviousTaskStateList();
	            for (Task task : tasksToReadd) {
	                addToList(task);
	            }
	            
	        } else if (lastState.getLastCommand() == LastCommand.DONE) {
	            List<Task> tasksNotDone = lastState.getPreviousTaskStateList();
	            for (Task undoneTask : tasksNotDone) {
	                for (int i = this.tasksFinished.size() - 1; i >= 0; i--) {
	                    Task task = this.tasksFinished.get(i);
	                    if (task.getAddedTime().equals(undoneTask.getAddedTime())) {
	                        
	                        if (task instanceof RepeatedTask) {
	                            // find the task added in timedTasks to delete
	                            RepeatedTask newRepeatedTask = getLatestConsecutiveRepeatedTask((RepeatedTask) task);
	                            this.tasksTimed.remove(newRepeatedTask);
	                            this.totalTasks--;
	                        }                        
	                        
	                        this.tasksFinished.remove(task);
	                        addToList(undoneTask);
	                        this.totalFinished--;
	                        break;
	                    }
	                }
	            }
	        } else {
	            // do other undo operations here
	        }
	        
	        
	    }
	}
	
    public void redo() throws RedoException{
        if (redoStack.isEmpty()) {
            throw new RedoException();
        } else {
            
            //redo here based on cmd type
            
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
	
	private void addToUndoList(LastCommand cmd, List<Task> tasks, List<Integer> taskIndices) {
	    LastState currentTasksState = new LastState(cmd, tasks, taskIndices);
	    undoStack.push(currentTasksState);
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