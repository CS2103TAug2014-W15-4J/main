package controller;

import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import controller.UserInput.RepeatDate;
import exception.RedoException;
import exception.TaskDoneException;
import exception.TaskInvalidDateException;
import exception.TaskInvalidIdException;
import exception.TaskNoSuchTagException;
import exception.TaskTagDuplicateException;
import exception.TaskTagException;
import exception.UndoException;
import model.TaskList;
import model.Task;


/**
 * main class that manages the TaskList
 */

public class Logic {

	static Scanner scanner = new Scanner(System.in);
	static TaskList listOfTasks;

	final static String MESSAGE_TASK_ADDED = "Task added successfully.";
	final static String MESSAGE_TASK_EDITED = "Task edited successfully.";
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
	final static String MESSAGE_HELP = "Current list of available commands: \n"
			+ "- add a floating task     : add <description>\n"
			+ "- add a deadline task     : add <description> by <time/date>\n"
			+ "- add a fixed time        : add <description> <time/date1> to <time/date2>\n"
			+ "- add a repeated task     : add <description> every <time/date> <period(daily/weekly/monthly)>\n"
			+ "- edit a task description : edit <taskID> <description>\n"
			+ "- edit a task time/date   : edit <taskID> <time/date>\n"
			+ "- delete task(s)          : delete <taskID> [<taskID> <taskID> ...]\n"
			+ "- clear all tasks         : clear\n"
			+ "- mark task(s) done       : done <taskID> [<taskID> <taskID> ...]\n"
			+ "- tag a task              : tag <taskID> <tag>\n"
			+ "- untag a task            : untag <taskID> <tag>\n"
			+ "- untag all tags from task: untag <taskID>\n"
			+ "- show all tasks          : show / show all\n"
			+ "- show tasks (add order)  : show added\n"
			+ "- show tasks with tag     : show <tag>\n"
			+ "- show tasks that are done: show done\n"
			+ "(You can only delete or search tasks when displying tasks that are done)\n"
			+ "- search tasks            : search <keyword>\n"
			+ "- exit the program        : exit";

	static Storage storage = new Storage();

	static Logger log = Logger.getLogger("controller.logic");

	public static void main(String[] args) {
		// let the logger only display warning log message.
		log.setLevel(Level.WARNING);

		// get existing tasks from storage
		listOfTasks = storage.load();
		listOfTasks.setShowDisplayListToFalse();

		// get and execute new tasks
		while (true) {

			log.info("Getting user input");
			String userInput = getUserInput();

			// parse and execute command
			System.out.println(readAndExecuteCommands(userInput));

			log.info("Execution complete.");

			// update the history and storage file
			storage.save(listOfTasks);

		}
	}

	public static void loadTaskList() {
		listOfTasks = storage.load();
	}

	public static TaskList getTaskList() {
		return listOfTasks;
	}

	public static void saveTaskList() {
		storage.save(listOfTasks);
	}

	public static String getDisplayInfo() {
		return display();
	}

	/**
	 * @param userInput
	 * @return feedback string
	 * 
	 *         this method gets the userInput from the UI, calls the parser for
	 *         processing, and executes the command given, returning the
	 *         feedback string at the end.
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
	 * @param userCommand
	 * @return feedback string
	 * 
	 *         this method reads the UserInput object and executes the command
	 *         given the commands will include to add/ edit/ delete a task, to
	 *         undo/ redo an operation, and more.
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
			return display(showCommand);

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
			return SearchTask(userCommand.getDescription());

		} else if (userCommand.getCommand() == UserInput.CMD.UNDO) {
		    return undo();

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
	 * @param description
	 * @param time
	 * @param period
	 * @return feedback string
	 * 
	 *         the following methods will add a task to the file, with the
	 *         specified parameters (floating, deadline, repeated, fixed tasks
	 *         respectively)
	 */
	private static String addTask(String description) {
		listOfTasks.addToList(description);
		return MESSAGE_TASK_ADDED;
	}

	private static String addTask(String description, Date time) {
		listOfTasks.addToList(description, time);
		return MESSAGE_TASK_ADDED;
	}

	private static String addTask(String description, Date time,
			RepeatDate repeatDate) {
		listOfTasks.addToList(description, time, repeatDate);
		return MESSAGE_TASK_ADDED;
	}

	private static String addTask(String description, Date startTime,
			Date endTime) {
		try {
			listOfTasks.addToList(description, startTime, endTime);
			return MESSAGE_TASK_ADDED;
		} catch (TaskInvalidDateException e) {
			return e.getMessage();
		}

	}

	/**
	 * @param taskIndex
	 * @param description
	 * @param time
	 *            (for deadline/timed tasks)
	 * @return feedback string
	 * 
	 *         the following methods will edit a task in the file, with the
	 *         specified parameters
	 */
	private static String editTask(int taskIndex, String description) {
		try {
			listOfTasks.editTaskDescription(taskIndex, description);
			return MESSAGE_TASK_EDITED;

		} catch (TaskInvalidIdException e) {
			return MESSAGE_INVALID_TASKID;
		}
	}

	private static String editTask(int taskIndex, Date time) {
		try {
			listOfTasks.editTaskDeadline(taskIndex, time);
			return MESSAGE_TASK_EDITED;

		} catch (TaskInvalidIdException e) {
			return MESSAGE_INVALID_TASKID;

		} catch (TaskInvalidDateException e) {
			return MESSAGE_INVALID_DATE;
		}

	}

	private static String editTask(int taskIndex, String desc, Date time) {
		try {
			listOfTasks.editTaskDescription(taskIndex, desc);
			listOfTasks.editTaskDeadline(taskIndex, time);
			return MESSAGE_TASK_EDITED;

		} catch (TaskInvalidIdException e) {
			return MESSAGE_INVALID_TASKID;

		} catch (TaskInvalidDateException e) {
			return MESSAGE_INVALID_DATE;
		}

	}

	private static String editTask(int taskIndex, Date startDate, Date endDate) {

		try {
			listOfTasks.editTaskStartDate(taskIndex, startDate);
			listOfTasks.editTaskDeadline(taskIndex, endDate);
			return MESSAGE_TASK_EDITED;

		} catch (TaskInvalidIdException e) {
			return MESSAGE_INVALID_TASKID;

		} catch (TaskInvalidDateException e) {
			return MESSAGE_INVALID_DATE;
		}
	}

	private static String editTask(int taskIndex, String desc, Date startDate,
			Date endDate) {
		try {
			listOfTasks.editTaskDescription(taskIndex, desc);
			listOfTasks.editTaskStartDate(taskIndex, startDate);
			listOfTasks.editTaskDeadline(taskIndex, endDate);
			return MESSAGE_TASK_EDITED;

		} catch (TaskInvalidIdException e) {
			return MESSAGE_INVALID_TASKID;

		} catch (TaskInvalidDateException e) {
			return MESSAGE_INVALID_DATE;
		}
	}

	/*
	 * private static String editTask(int taskIndex, String desc, Date time,
	 * String repeatPeriod) { listOfTasks.editTaskDescription(taskIndex,desc);
	 * listOfTasks.editTaskDeadline(taskIndex, time);
	 * listOfTasks.editTaskRepeatPeriod(taskIndex, repeatPeriod); return
	 * MESSAGE_TASK_EDITED;
	 * 
	 * }
	 */

	/**
	 * Searching a task
	 * 
	 * @param keyword
	 *            the keyword to search
	 * @return the search result
	 * 
	 */
	private static String SearchTask(String keyword) {
		List<Task> result = listOfTasks.searchTaskByKeyword(keyword);
		if (result.size() == 0) {
			// disable user from manipulating on search result directly
			listOfTasks.setShowDisplayListToFalse();
			return MESSAGE_EMPTY_SEARCH_RESULT;
		}

		return displayTasks(result);
	}

	/**
	 * @param taskIndexList
	 * @return feedback string whether tasks are deleted successfully
	 * 
	 *         this method will delete the specified task(s) from the file
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
	 * @return feedback that task list is cleared
	 * 
	 *         this method clears the task list of all tasks
	 */
	private static String clearTaskList() {
		listOfTasks.clearList();
		return MESSAGE_TASK_CLEARED;
	}

	/**
	 * 
	 * @param userCommand
	 * @return user's task information
	 * 
	 *         This method displays the user's tasks information, specified by
	 *         userCommand
	 */
	private static String display(String userCommand) {

		assert userCommand != null;

		if (userCommand.equals("all")) {
			listOfTasks.setNotShowingDone();
			return displayTasks(listOfTasks.prepareDisplayList(false));

		} else if (userCommand.equals("added")) {
			listOfTasks.setNotShowingDone();
			return displayTasks(listOfTasks.prepareDisplayList(true));

		} else if (userCommand.equals("done")) {
			return displayTasks(listOfTasks.getFinishedTasks());

		} else {
			listOfTasks.setNotShowingDone();
			try {
				return String.format(MESSAGE_TASKTAG_RETURNED, userCommand,
						displayTasks(listOfTasks
								.prepareDisplayList(userCommand)));

			} catch (TaskNoSuchTagException e) {
				return MESSAGE_INVALID_TAG_NONEXISTENT;
			}

		}
	}

	/**
	 * This method will display user's tasks information.
	 * 
	 */
	private static String display() {
		if (listOfTasks.count() == 0) {
			// empty task list
			return MESSAGE_EMPTY_TASK_LIST;
		}

		StringBuilder taskDisplay = new StringBuilder();
		taskDisplay.append("\nCurrent tasks:\n");
		for (int i = 0; i < listOfTasks.count(); i++) {
			Task task = listOfTasks.getTask(i);
			taskDisplay.append((i + 1));
			taskDisplay.append(". ");
			taskDisplay.append(task.toString());
			taskDisplay.append("\n\n");
		}
		taskDisplay.append(displayProgress());

		return taskDisplay.toString();
	}

	private static String displayProgress() {
		StringBuilder display = new StringBuilder();
		display.append("\nProgress: ");
		display.append(listOfTasks.count() - listOfTasks.countFinished());
		display.append(" Unfinished / ");
		display.append(listOfTasks.count());
		display.append(" Total\n");
		return display.toString();
	}

	/**
	 * @param taskIndexList
	 * @return feedback string whether tasks are marked done successfully
	 * 
	 *         this method marks that specified task(s) has been done
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
	 * 
	 * @param taskIndexToTag
	 * @param tag
	 * @return feedback string on tagging of tasks.
	 * 
	 *         this method assigns the tag (non case-sensitive) to a specified
	 *         task
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

	private static String exportTasks() {
		listOfTasks.export();
	    return MESSAGE_TASK_EXPORT;
	}

	private static String displayTasksWithTag(String tag) {
		try {
			return String.format(MESSAGE_TASKTAG_RETURNED, tag,
					displayTasks(getTasksWithTag(tag)));

		} catch (TaskNoSuchTagException e) {
			return MESSAGE_INVALID_TAG_NONEXISTENT;
		}
	}

	private static List<Task> getTasksWithTag(String tag)
			throws TaskNoSuchTagException {

		List<Task> tasks = listOfTasks.getTasksWithTag(tag);
		return tasks;
	}

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

			} else {

			}

		}

		return taskDisplay.toString();

	}

	/**
	 * @return feedback string
	 * 
	 *         this method calls the last operation and reverts any changes.
	 *         this change can be re-obtained by using the redo method.
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
	 * @return feedback string
	 * 
	 *         this method re-does the last operation undo-ed.
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
	 * this method reads the user input from the command line and returns it as
	 * a string.
	 */
	public static String getUserInput() {
		System.out.print("Enter command: ");
		String userInput = scanner.nextLine();
		return userInput;
	}

	/**
	 * this method creates an empty task list for operations to be done on.
	 * 
	 * this method is for testing purposes only
	 */
	public static void setEmptyTaskList() {
		listOfTasks = new TaskList();
	}

	/**
	 * 
	 * @param taskId
	 * @return Task of id taskId
	 * 
	 *         this method returns the Task of the given taskId.
	 * 
	 *         this method is for testing purposes only
	 */
	public static Task getTask(int taskId) {
		return listOfTasks.getTask(taskId);
	}

}
