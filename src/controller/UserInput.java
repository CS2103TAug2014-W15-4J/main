package controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//@author A0119387U
/**
 * This class is an object of parsed command for Logic
 * 
 *
 */

public class UserInput {
	public enum CMD {
		ADD, DELETE, EDIT, CLEAR, DONE, SEARCH, EXIT, SHOW, HELP,TAG,UNDO,REDO, UNTAG, EXPORT
	};
	public enum RepeatDate {
		DAILY,WEEKLY,MONTHLY
	};

	private boolean valid = true;
	private CMD command = null;

	private List<Integer> ID = new ArrayList<Integer>();

	private int callingID = 0;
	private String afterCommand = null;
	private RepeatDate repeatDate = null;

	private String description = null;

	private boolean floating = false;
	private boolean repeated = false;
	private boolean deadline = false;

	private List<Date> dates = new ArrayList<Date>();
    
	/**
    * Check if the command is valid, and returns the corresponding boolean value
    * @return the validation of command
    */
	public boolean getValid() {
		return valid;
	}
	/**
	 * Check which type of command it is, and returns the corresponding command value
	 * @return the command type
	 */

	public CMD getCommand() {
		return command;
	}
	/**
	 * Get the IDs of tasks to be deleted, and returns the result in a list
	 * @return the result of tasks in a list
	 */

	public List<Integer> getDeleteID() {
		return ID;
	}
	/**
	 * Get the IDs of tasks to be marked as done, and returns the result in a list
	 * @return the result of tasks in a list
	 */

	public List<Integer> getDoneID() {
		return ID;
	}
	/**
	 * Get the ID of the task to be edited
	 * @return the ID of task
	 */

	public int getEditID() {
		return callingID;
	}
	/**
	 * Get the ID of the task to add a tag
	 * @return the ID of task
	 */
	public int getTagID() {
		return callingID;
	}
	/**
	 * Get the dates to add or edit, and returns the result in a list
	 * @return the results in a list
	 */

	public List<Date> getDate() {
		return dates;
	}
	/**
	 * Get the command for editing
	 * @return edit command
	 */

	public String getEditCommand() {
		return afterCommand;
	}
	/**
	 * Get the command for showing
	 * @return show command
	 */

	public String getShowCommand() {
		return afterCommand;
	}
	/**
	 * Get the description of task
	 * @return description of task
	 */

	public String getDescription() {
		return description;
	}
	/**
	 * Get the period to repeat for repeated tasks
	 * @return the repeat date
	 */

	public RepeatDate repeatDate() {
		return repeatDate;
	}
	/**
	 * Check whether the task to add(or edit) is a floating task, and returns the corresponding boolean value
	 * @return if the task is floating
	 */

	public boolean isFloat() {
		return floating;
	}
	/**
	 * Check whether the task to add(or edit) is a repeated task, and returns the corresponding boolean value
	 * @return if the task is repeated
	 */

	public boolean isRepeated() {
		return repeated;
	}
	/**
	 * Check whether the task to add(or edit) is a deadline task, and returns the corresponding boolean value
	 * @return if the task is deadline
	 */

	public boolean isDeadline() {
		return deadline;
	}
	/**
	 * Mark the command as invalid
	 */

	public void unvalidation() {
		valid = false;
	}
	/**
	 * Add the type of command
	 * @param userCommand given by Parser
	 */
	
	public void addCommand(CMD userCommand) {
		command = userCommand;		
	}
	/**
	 * Add the description of task
	 * @param userDescription given by Parser
	 */

	public void add(String userDescription) {
		description = userDescription;
	}
	/**
	 * Mark task as floating
	 */
	
	public void beFloat() {
		floating = true;
	}
	/**
	 * Mark task as repeated
	 */

	public void beRepeated() {
		repeated = true;
	}
	/**
	 * Mark task as deadline
	 */

	public void beDeadline() {
		deadline = true;
	}
	/**
	 * Add the IDs of tasks to be deleted
	 * @param numbers given by Parser
	 */

	public void addDeleteID(List<Integer> numbers) {
		ID = numbers;
	}
	/**
	 * Add the IDs of tasks to be marked as done
	 * @param numbers given by Parser
	 */

	public void addDoneID(List<Integer> numbers) {
		ID = numbers;
	}
	/**
	 * Add the ID of the task to be edited
	 * @param number given by Parser
	 */

	public void addEditID(int number) {
		callingID = number;
	}
	/**
	 * Add the ID of the task to add tag
	 * @param number given by Parser
	 */
	
	public void addTagID(int number) {
		callingID = number;
	}
	/**
	 * Add the specific editing command
	 * @param command given by Parser
	 */
	
	public void addEditCommand(String command) {
		afterCommand = command;
	}
	/**
	 * Add the specific showing command
	 * @param command given by Parser
	 */

	public void addShow(String command) {
		afterCommand = command;
	}
	/**
	 * Add the period to repeat for repeated tasks
	 * @param content given by Parser
	 */

	public void addRepeatDate(RepeatDate content) {
		repeatDate = content;
	}
	/**
	 * Add the dates for add(or edit) command
	 * @param date given by Parser
	 */

	public void addDate(List<Date> date) {
		dates = date;
	}

}
