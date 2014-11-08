package controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import controller.UserInput.RepeatDate;
import exception.RedoException;
import exception.TaskDoneException;
import exception.TaskInvalidDateException;
import exception.TaskInvalidIdException;
import exception.TaskNoSuchTagException;
import exception.TaskTagDuplicateException;
import exception.TaskTagException;
import exception.UndoException;
import log.ULogger;
import model.TaskList;
import model.Task;

//@author A0115384H
/**
 * The class that manages the TaskList, corresponding to the controls input by the user.
 */

public class Logic {

	static Scanner scanner = new Scanner(System.in);
	static TaskList listOfTasks;

	final static String MESSAGE_TASK_ADDED = "\"%s\" is added to your list.";
	final static String MESSAGE_TASK_EDITED = "Task edited successfully.";
	final static String MESSAGE_TASK_EDITED_DESCRITPION = "Task \"%s\" is renamed as \"%s\"";
	final static String MESSAGE_TASK_EDITED_DEADLINE = "The deadline is changed to \"%s\"";
	final static String MESSAGE_TASK_EDITED_ALL = "Task is renamed as \"%s\". New deadline: \"%s\"";
	final static String MESSAGE_TASK_DELETED = "Task(s) deleted successfully.";
	final static String MESSAGE_TASK_CLEARED = "Task list cleared successfully.";
	final static String MESSAGE_TASK_MARKED_DONE = "Task(s) marked done successfully.";
	final static String MESSAGE_TASK_TAGGED = "Task tagged successfully.";
	final static String MESSAGE_TASK_UNTAGGED = "Task untagged successfully.";
	final static String MESSAGE_TASK_EXPORT = "Export successfully.";
	final static String MESSAGE_TASKTAG_RETURNED = "Tasks with tag %1$s\n%2$s";

	final static String MESSAGE_PROGRAM_REDO = "redo successful.";
	final static String MESSAGE_PROGRAM_UNDO = "undo successful.";
	final static String MESSAGE_PROGRAM_EXIT = "Program terminated successfully.";

	final static String MESSAGE_EMPTY_TASK_LIST = "Your task list is empty.";
	final static String MESSAGE_EMPTY_SEARCH_RESULT = "Nothing found.";

	final static String MESSAGE_INVALID_EDIT = "Invalid edit.";
	final static String MESSAGE_INVALID_DELETE = "Error deleting task(s).";
	final static String MESSAGE_INVALID_MARKED_DONE = "Error: task(s) already marked done.";

	final static String MESSAGE_INVALID_TAG_DELETE = "No such tag to remove.";
	final static String MESSAGE_INVALID_TAG_DUPLICATE = "Task already contains this tag.";
	final static String MESSAGE_INVALID_TAG_NONEXISTENT = "No such tag";

	final static String MESSAGE_INVALID_UNDO = "No previous operation to undo.";
	final static String MESSAGE_INVALID_REDO = "No next operation to redo.";

	final static String MESSAGE_INVALID_COMMAND = "Invalid command. Type 'help' to see the list of available commands.";
	final static String MESSAGE_INVALID_DESCRIPTION = "Invalid description.";
	final static String MESSAGE_INVALID_TASKID = "Invalid taskid(s).";
	final static String MESSAGE_INVALID_DATE = "Invalid date(s).";
	final static String MESSAGE_INVALID_DATE_NUMBER = "Invalid number of dates.";

	final static String MESSAGE_COMMAND_NOT_ALLOW = "You can only search or delete tasks when showing finished tasks.";
	final static String MESSAGE_HELP = "Current list of available commands: \n" +
			"- add a floating task     : add <description>\n" +
			"- add a deadline task     : add <description> by <time/date>\n" +
			"- add a fixed time        : add <description> <time/date1> to <time/date2>\n" +
			"- add a repeated task     : add <description> every <time/date> <period(daily/weekly/monthly)>\n" +
			"- edit a task description : edit <taskID> <description>\n" +
			"- edit a task time/date   : edit <taskID> <time/date>\n" +
			"- delete task(s)          : delete <taskID> [<taskID> <taskID> ...]\n" +
			"- clear all tasks         : clear\n" +
			"- mark task(s) done       : done <taskID> [<taskID> <taskID> ...]\n" +
			"- tag a task              : tag <taskID> <tag>\n" +
			"- untag a task            : untag <taskID> <tag>\n" +
			"- untag all tags from task: untag <taskID>\n" +
			"- show all tasks          : show / show all\n" +
			"- show tasks (add order)  : show added\n" +
			"- show tasks with tag     : show <tag>\n" +
			"- show tasks that are done: show done\n" +
			"(You can only delete or search tasks when displying tasks that are done)\n" +
			"- search tasks            : search <keyword>\n" +
			"- exit the program        : exit";

	static Storage storage = new Storage();

	static ULogger log = ULogger.getLogger();

//	public static void main(String[] args) {
//		// get existing tasks from storage
//		listOfTasks = storage.load();
//		listOfTasks.setShowDisplayListToFalse();
//
//		// get and execute new tasks
//		while (true) {
//
//			log.info("Getting user input");
//			String userInput = getUserInput();
//
//			// parse and execute command
//			System.out.println(readAndExecuteCommands(userInput));
//
//			log.info("Execution complete.");
//
//			// update the history and storage file
//			storage.save(listOfTasks);
//
//		}
//	}
	
	//@author A0119414L
	/**
	 * Loads the task list from the storage.
	 */
	public static void loadTaskList() {
		listOfTasks = storage.load();
	}
	
	//@author A0119414L
	/**
	 * Get the task list for MainView Controller.
	 * 
	 * @return	the task list in the Storage.
	 */
	public static TaskList getTaskList() {
		return listOfTasks;
	}
	
	//@author A0119414L
	/**
	 * Call Logic to save the task list to Storage.
	 */
	public static void saveTaskList() {
		storage.save(listOfTasks);
	}
	
	//@author A0119414L
	/**
	 * Returns the date list, if user input is show command.
	 * 
     * @param userInput Command that user enter.
     * @return          The date list if it is a show command.
     */
    public static List<Date> getDateList(String userInput) {
        Parser parser = new Parser();
        UserInput userCommand = parser.parse(userInput);
        List<Date> showDate = null;
        if (userCommand.getCommand() == UserInput.CMD.SHOW) {
            showDate = userCommand.getDate();
        } else {
            
        }
        return showDate;
    }
	
	//@author A0119414L
	/**
	 * Returns true if the current command a show date period command.  
	 * 
	 * @param userInput	Command that user enter.
	 * @return			true if it is a show date period command.
	 */
	public static boolean isShowDateCommand(String userInput) {
		Parser parser = new Parser();
		UserInput userCommand = parser.parse(userInput);
		
		return (!userCommand.getDate().isEmpty());
	}

	//@author A0115384H
	/**
	 * This method gets the userInput from the UI, calls the parser for processing, 
	 * and executes the command given, returning the feedback string at the end.
	 * 
	 * @param userInput        The input from the user.
	 * @return                 The feedback string regard the success of the command.
	 */
	public static String readAndExecuteCommands(String userInput) {

		log.info("Processing user input: start parsing.");
		// parse and execute command
		Parser parser = new Parser();
		UserInput userCommand = parser.parse(userInput);

		log.info("Done parsing. Executing command..");

		return executeCommand(userCommand);
	}

	/**
	 * This method reads the UserInput object and executes the command given;
	 * the commands include to add/ edit/ delete a task, to undo/ redo an operation.
	 * 
	 * @param userCommand      the UserInput object from parsing the user input.
	 * @return                 the feedback string regarding the success of the command.
	 */
	private static String executeCommand(UserInput userCommand) {

		if (userCommand.getCommand() == UserInput.CMD.HELP) {
			return MESSAGE_HELP;

		} else if (userCommand.getCommand() == UserInput.CMD.ADD) {
			String desc = userCommand.getDescription();
			List<Date> dateList = userCommand.getDate();
			listOfTasks.setShowDisplayListToFalse();

			assert (desc != null);

			if (desc.isEmpty()) {
				return MESSAGE_INVALID_DESCRIPTION;

			} else {
				if (userCommand.isFloat()) {
					return addTask(desc);

				} else if (userCommand.isDeadline()) {
					if (dateList.size() == 1) {
						Date date = dateList.get(0);
						return addTask(desc, date);

					} else {
						return MESSAGE_INVALID_DATE_NUMBER;
					}
				} else if (userCommand.isRepeated()) {

					if (dateList.size() == 1) {
						Date date = dateList.get(0);
						RepeatDate repeatDate = userCommand.repeatDate();

						return addTask(desc, date, repeatDate);

					} else {
						return MESSAGE_INVALID_DATE_NUMBER;
					}

				} else {
					// is fixed task
					if (dateList.size() == 2) {
						Date startDate = dateList.get(0);
						Date endDate = dateList.get(1);
						return addTask(desc, startDate, endDate);

					} else {
						return MESSAGE_INVALID_DATE_NUMBER;
					}
				}
			}

		} else if (userCommand.getCommand() == UserInput.CMD.EDIT) {
			if (listOfTasks.isShowingDone()) {
				return MESSAGE_COMMAND_NOT_ALLOW;
			}
			int editID = userCommand.getEditID();
			String desc = userCommand.getDescription();
			String editCommand = userCommand.getEditCommand();
			List<Date> dateList = userCommand.getDate();

			Task taskToEdit = listOfTasks.getTask(editID - 1);
			Task.Type taskType = taskToEdit.getType();
			// listOfTasks.setShowDisplayListToFalse();

			assert (desc != null);

			if (editCommand != null) {
				// additional functions
				// editCommand --> no-repeat / no-time
				return null;

			} else if (taskType == Task.Type.FLOAT) {
				if (desc.isEmpty()) {
					return MESSAGE_INVALID_DESCRIPTION;

				} else {
					return editTask(editID, desc);
				}

			} else if (taskType == Task.Type.DEADLINE) {

				if ((dateList.size() == 1) && (!desc.isEmpty())) {
					Date date = dateList.get(0);
					return editTask(editID, desc, date);

				} else if (!desc.isEmpty()) {
					return editTask(editID, desc);

				} else if (dateList.size() == 1) {
					Date date = dateList.get(0);
					return editTask(editID, date);

				} else {
					return MESSAGE_INVALID_DATE_NUMBER;
				}

			} else if (taskType == Task.Type.REPEATED) {

				if ((dateList.size() == 1) && (!desc.isEmpty())) {
					Date date = dateList.get(0);
					return editTask(editID, desc, date);

				} else if (!desc.isEmpty()) {
					return editTask(editID, desc);

				} else if (dateList.size() == 1) {
					Date date = dateList.get(0);
					return editTask(editID, date);

				} else {
					return MESSAGE_INVALID_DATE_NUMBER;
				}

			} else if (taskType == Task.Type.FIXED) {

				if ((dateList.size() == 2) && (!desc.isEmpty())) {
					Date dateStart = dateList.get(0);
					Date dateEnd = dateList.get(1);
					return editTask(editID, desc, dateStart, dateEnd);

				} else if (dateList.size() == 2) {
					Date dateStart = dateList.get(0);
					Date dateEnd = dateList.get(1);
					return editTask(editID, dateStart, dateEnd);

				} else if (!desc.isEmpty()) {
					return editTask(editID, desc);

				} else {
					return MESSAGE_INVALID_DATE_NUMBER;
				}

			} else {
				// other types of edits here
				return MESSAGE_INVALID_EDIT;
			}

		} else if (userCommand.getCommand() == UserInput.CMD.DELETE) {
			// listOfTasks.setShowDisplayListToFalse();
			return deleteTask(userCommand.getDeleteID());

		} else if (userCommand.getCommand() == UserInput.CMD.SHOW) {
			String showCommand = userCommand.getShowCommand();
			List<Date> showDate = userCommand.getDate();
			return display(showCommand, showDate);

		} else if (userCommand.getCommand() == UserInput.CMD.CLEAR) {
			listOfTasks.setShowDisplayListToFalse();
			return clearTaskList();

		} else if (userCommand.getCommand() == UserInput.CMD.DONE) {
			if (listOfTasks.isShowingDone()) {
				return MESSAGE_COMMAND_NOT_ALLOW;
			}
			return markDone(userCommand.getDoneID());

		} else if (userCommand.getCommand() == UserInput.CMD.TAG) {
			if (listOfTasks.isShowingDone()) {
				return MESSAGE_COMMAND_NOT_ALLOW;
			}
			return tagTask(userCommand.getTagID(), userCommand.getDescription());

		} else if (userCommand.getCommand() == UserInput.CMD.UNTAG) {
			if (listOfTasks.isShowingDone()) {
				return MESSAGE_COMMAND_NOT_ALLOW;
			}
			return untagTask(userCommand.getTagID(),
					userCommand.getDescription());

		} else if (userCommand.getCommand() == UserInput.CMD.SEARCH) {
			return searchTask(userCommand.getDescription());

		} else if (userCommand.getCommand() == UserInput.CMD.UNDO) {
		    return undo();
		    
		} else if (userCommand.getCommand() == UserInput.CMD.REDO) {
		    return redo();

		} else if (userCommand.getCommand() == UserInput.CMD.EXPORT) {
			return exportTasks();

		} else if (userCommand.getCommand() == UserInput.CMD.EXIT) {
			listOfTasks.setShowDisplayListToFalse();
			storage.save(listOfTasks);
			storage.close();
			System.exit(0);
			return MESSAGE_PROGRAM_EXIT;

		} else {
			// other functions here

			return MESSAGE_INVALID_COMMAND;
		}

	}

	/**
	 * This method adds a floating task to the file, with the specified description.
	 * 
	 * @param description  Description of task to be added.
	 * @return             the feedback string regarding the success of the command.
	 */
	private static String addTask(String description) {
		listOfTasks.addToList(description);
		return String.format(MESSAGE_TASK_ADDED, description);
	}

	/**
     * This method adds a deadline task to the file, with the specified description and time.
     * 
     * @param description   Description of task to be added.
     * @param time          Time for task to be done by. 
     * @return              The feedback string regarding the success of the command.
     */
	private static String addTask(String description, Date time) {
		listOfTasks.addToList(description, time);
		return String.format(MESSAGE_TASK_ADDED, description);
	}

    /**
     * This method adds a repeated task to the file, with the specified description and time.
     * along with the repeat period.
     * 
     * @param description   Description of task to be added.
     * @param time          Time for task to be done by.
     * @param repeatDate    The RepeatDate object representing the period to repeat.     
     * @return              The feedback string regarding the success of the command.
     */
	private static String addTask(String description, Date time,
	                              RepeatDate repeatDate) {
		listOfTasks.addToList(description, time, repeatDate);
		return String.format(MESSAGE_TASK_ADDED, description);
	}

	/**
	 * This method adds a fixed task to the file, with the specified description and times.
	 * 
	 * @param description  Description of task to be added.
	 * @param startTime    Time for the task to start.
	 * @param endTime      Time for the task to end.
	 * @return             The feedback string regarding the success of the command.
	 */
	private static String addTask(String description, Date startTime,
	                              Date endTime) {
		try {
			listOfTasks.addToList(description, startTime, endTime);
			return String.format(MESSAGE_TASK_ADDED, description);
		} catch (TaskInvalidDateException e) {
			return e.getMessage();
		}
	}

	/**
	 * This method edits the description of a task in the file.
	 *  
	 * @param taskIndex    The task index of the task to be edited.
	 * @param description  The new description to be entered.
	 * @return             The feedback string regarding the success of the command.
	 */
	private static String editTask(int taskIndex, String description) {
		try {
			String oldDescription = listOfTasks.editTaskDescriptionOnly(taskIndex, description);
			return String.format(MESSAGE_TASK_EDITED_DESCRITPION, oldDescription, description);

		} catch (TaskInvalidIdException e) {
			return MESSAGE_INVALID_TASKID;
		}
	}

	/**
	 * This method edits the time of a task in the file (non-fixed/floating tasks).
	 * 
	 * @param taskIndex    The task index of the task to be edited.
	 * @param time         The new time to be entered.
	 * @return             The feedback string regarding the success of the command.
	 */
	private static String editTask(int taskIndex, Date time) {
		try {
			listOfTasks.editTaskDeadlineOnly(taskIndex, time);
			String timeString = formatTime(time);
			return String.format(MESSAGE_TASK_EDITED_DEADLINE, timeString);

		} catch (TaskInvalidIdException e) {
			return MESSAGE_INVALID_TASKID;

		} catch (TaskInvalidDateException e) {
			return MESSAGE_INVALID_DATE;
		}
	}

	/**
	 * This method edits both the time and description of a task in the file (non-fixed/floating tasks).
	 * 
	 * @param taskIndex    The task index of the task to be edited.
	 * @param desc         The new description to be entered.
	 * @param time         The new time to be entered.
	 * @return             The feedback string regarding the success of the command.
	 */
	private static String editTask(int taskIndex, String desc, Date time) {
		try {
		    listOfTasks.editTaskDescriptionDeadline(taskIndex, desc, time);
		    String timeString = formatTime(time);
			return String.format(MESSAGE_TASK_EDITED_ALL, desc, timeString);

		} catch (TaskInvalidIdException e) {
			return MESSAGE_INVALID_TASKID;

		} catch (TaskInvalidDateException e) {
			return MESSAGE_INVALID_DATE;
		}
	}

	/**
	 * @param time
	 * @return
	 */
	private static String formatTime(Date time) {
		SimpleDateFormat timeFormat = new SimpleDateFormat("EEE, MMM d HH:mm");
		String timeString = timeFormat.format(time);
		return timeString;
	}

	/**
	 * This method edits the times of a fixed task.
	 * 
	 * @param taskIndex    The task index of the task to be edited.
	 * @param startDate    The new start time of the task.
	 * @param endDate      The new end time of the task.
	 * @return             The feedback string regarding the success of the command.
	 */
	private static String editTask(int taskIndex, Date startDate, Date endDate) {

		try {
		    listOfTasks.editTaskTimes(taskIndex, startDate, endDate);
		    String timeString = formatTime(endDate);
			return String.format(MESSAGE_TASK_EDITED_DEADLINE, timeString);

		} catch (TaskInvalidIdException e) {
			return MESSAGE_INVALID_TASKID;

		} catch (TaskInvalidDateException e) {
			return MESSAGE_INVALID_DATE;
		}
	}

	/**
	 * This method edits both the description and times of a fixed task.
	 * 
	 * @param taskIndex    The task index of the task to be edited.
	 * @param desc         The new description to be entered.
	 * @param startDate    The new start time of the task.
	 * @param endDate      The new end time of the task.
	 * @return             The feedback string regarding the success of the command.
	 */
	private static String editTask(int taskIndex, String desc, 
	                               Date startDate,Date endDate) {
		try {
		    listOfTasks.editTaskDescriptionTimes(taskIndex, desc, startDate, endDate);
		    String timeString = formatTime(endDate);
			return String.format(MESSAGE_TASK_EDITED_ALL, desc, timeString);

		} catch (TaskInvalidIdException e) {
			return MESSAGE_INVALID_TASKID;

		} catch (TaskInvalidDateException e) {
			return MESSAGE_INVALID_DATE;
		}
	}

	//@author A0119446B
	/**
	 * This method searches for a task.
	 * 
	 * @param keyword  The keyword to search.
	 * @return         The search result.
	 * 
	 */
	private static String searchTask(String keyword) {
		List<Task> result = listOfTasks.searchTaskByKeyword(keyword);
		if (result.size() == 0) {
			// disable user from manipulating on search result directly
			listOfTasks.setShowDisplayListToFalse();
			return MESSAGE_EMPTY_SEARCH_RESULT;
		}

		return displayTasks(result);
	}

	//@author A0115384H
	/**
	 * This method deletes the specified tasks from the file.
	 * 
	 * @param taskIndexList    The list of task indices of tasks to be deleted.
	 * @return                 The feedback string regarding the success of the command.
	 */
	private static String deleteTask(List<Integer> taskIndexList) {
		// listOfTasks.setShowDisplayListToFalse();
		try {
			listOfTasks.deleteFromList(taskIndexList);
			return MESSAGE_TASK_DELETED;

		} catch (TaskInvalidIdException e) {
			return MESSAGE_INVALID_TASKID;

		}
	}

	/**
	 * This method clears the task list of all tasks.
	 * 
	 * @return     The feedback string regarding the success of the command.
	 */
	private static String clearTaskList() {
		listOfTasks.clearList();
		return MESSAGE_TASK_CLEARED;
	}
	
	//@author A0119446B
	/**
	 * This method displays the user's tasks information, specified by the user command.
	 * 
	 * @param userCommand  The specific type of show command.
	 * @param showDate     The given date period for displaying.
	 * @return             User's task information.
	 */
	private static String display(String userCommand, List<Date> showDate) {

		assert (userCommand != null);
		if (!showDate.isEmpty()) {
			listOfTasks.setNotShowingDone();
			try {
				return displayTasks(listOfTasks.getDateRangeTask(showDate));
			} catch (TaskInvalidDateException e) {
				return MESSAGE_INVALID_DATE;
			}
		}
		if (userCommand.equals("all")) {
			listOfTasks.setNotShowingDone();
			return displayTasks(listOfTasks.prepareDisplayList(false));

		} else if (userCommand.equals("added")) {
			listOfTasks.setNotShowingDone();
			return displayTasks(listOfTasks.prepareDisplayList(true));

		} else if (userCommand.equals("done")) {
			return displayTasks(listOfTasks.getFinishedTasks());

		}  else if (userCommand.equals("overdue")) {
			return displayTasks(listOfTasks.getOverdueTask());

		} else {
			listOfTasks.setNotShowingDone();
			try {
				return String.format(MESSAGE_TASKTAG_RETURNED, userCommand,
				                     displayTasks(listOfTasks.prepareDisplayList(userCommand)));

			} catch (TaskNoSuchTagException e) {
				return MESSAGE_INVALID_TAG_NONEXISTENT;
			}
		}
	}

	//@author A0115384H
	/**
	 * This method marks done the specified tasks from the file.
	 * 
	 * @param taskIndexList    The list of task indices of tasks to be marked done.
	 * @return                 The feedback string regarding the success of the command.
	 */
	private static String markDone(List<Integer> taskIndexList) {
		try {
			listOfTasks.markTaskDone(taskIndexList);
			return MESSAGE_TASK_MARKED_DONE;

		} catch (TaskDoneException e) {
			return MESSAGE_INVALID_MARKED_DONE;

		} catch (TaskInvalidIdException e) {
			return MESSAGE_INVALID_TASKID;
		}
	}

	/**
	 * This method assigns the tag (non case-sensitive) to a specified task.
	 * 
	 * @param taskIndexToTag   The task index of the task to be tagged.
	 * @param tag              The tag to be assigned.
	 * @return                 The feedback string regarding the success of the command.
	 */
	private static String tagTask(int taskIndexToTag, String tag) {
		try {
			listOfTasks.tagTask(taskIndexToTag, tag);
			return MESSAGE_TASK_TAGGED;

		} catch (TaskInvalidIdException e) {
			return MESSAGE_INVALID_TASKID;

		} catch (TaskTagDuplicateException e) {
			return MESSAGE_INVALID_TAG_DUPLICATE;
		}
	}

    /**
     * This method removes the tag (non case-sensitive) from a specified task.
     * 
     * @param taskIndexToTag   The task index of the task to be untagged.
     * @param tag              The tag to be removed.
     * @return                 The feedback string regarding the success of the command.
     */
	private static String untagTask(int taskIndexToUntag, String tag) {
		try {
			listOfTasks.untagTask(taskIndexToUntag, tag);
			return MESSAGE_TASK_UNTAGGED;

		} catch (TaskInvalidIdException e) {
			return MESSAGE_INVALID_TASKID;

		} catch (TaskTagException e) {
			return MESSAGE_INVALID_TAG_DELETE;
		}
	}

	//@author A0119446B
	/**
	 * This method extracts the tasks information and exports them into a txt file.
	 * 
	 * @return The feedback string regarding the success of the command.
	 */
	private static String exportTasks() {
		listOfTasks.export();
	    return MESSAGE_TASK_EXPORT;
	}

	/**
	 * This method extracts tasks information into a string and returns it.
	 * 
	 * @param taskList List of tasks to be displayed.
	 * @return         The tasks information.
	 */
	private static String displayTasks(List<Task> taskList) {

		int taskListSize = taskList.size();

		if (taskList.size() == 0) {
			// empty task list
			return MESSAGE_EMPTY_TASK_LIST;
		}

		StringBuilder taskDisplay = new StringBuilder();

		for (int j = 0; j < taskListSize; j++) {
			Task task = taskList.get(j);

			taskDisplay.append((j + 1) + ". " + task.toString());

			if (j != taskListSize - 1) {
				taskDisplay.append("\n\n");

			}
		}

		return taskDisplay.toString();

	}

	//@author A0115384H
	/**
	 * This method reverts any changes done by the last operation.
	 * This change can be re-obtained by using the redo method.
	 *  
	 * @return The feedback string regarding the success of the command.
	 */
	private static String undo() {
	    try {
	        listOfTasks.undo();
			return MESSAGE_PROGRAM_UNDO;
	        
	    } catch (UndoException e) {
	        return MESSAGE_INVALID_UNDO;
		}
	}

	/**
	 * This method re-does the last operation undo-ed.
	 * 
	 * @return The feedback string regarding the success of the command.
	 */
	private static String redo() {
	    try {
	        listOfTasks.redo();
			return MESSAGE_PROGRAM_REDO;
	        
	    } catch (RedoException e) {
	        return MESSAGE_INVALID_REDO;
		}
	}
	

	/**
	 * This method reads the user input from the command line and returns it as a string.
	 */
	public static String getUserInput() {
		System.out.print("Enter command: ");
		String userInput = scanner.nextLine();
		return userInput;
	}

	/**
	 * This method creates an empty task list for operations to be done on.
	 * This method is for testing purposes only.
	 */
	public static void setEmptyTaskList() {
		listOfTasks = new TaskList();
	}

	/**
	 * This method returns the Task of the given task id.
	 * This method is for testing purposes only.
	 * 
	 * @param taskId   The taskId of the task to be found.
	 * @return         The task with the specified taskId.
	 */
	public static Task getTask(int taskId) {
		return listOfTasks.getTask(taskId);
	}
	
	/**
	 * This method empties the undoStack and redoStack. 
	 * This method is for testing purposes only.
	 */
	public static void emptyUndoRedoStack() {
	    listOfTasks.clearUndoRedoStack();
	}

}
