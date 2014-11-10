package controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is an object of parsed command for Logic
 * 
 *
 */
//@author A0119387U
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

	public boolean getValid() {
		return valid;
	}

	public CMD getCommand() {
		return command;
	}

	public List<Integer> getDeleteID() {
		return ID;
	}

	public List<Integer> getDoneID() {
		return ID;
	}

	public int getEditID() {
		return callingID;
	}
	
	public int getTagID() {
		return callingID;
	}

	public List<Date> getDate() {
		return dates;
	}

	public String getEditCommand() {
		return afterCommand;
	}

	public String getShowCommand() {
		return afterCommand;
	}

	public String getDescription() {
		return description;
	}

	public RepeatDate repeatDate() {
		return repeatDate;
	}

	public boolean isFloat() {
		return floating;
	}

	public boolean isRepeated() {
		return repeated;
	}

	public boolean isDeadline() {
		return deadline;
	}

	public void unvalidation() {
		valid = false;
	}
	
	public void addCommand(CMD userCommand) {
		command = userCommand;		
	}

	public void add(String userDescription) {
		description = userDescription;
	}
	
	public void beFloat() {
		floating = true;
	}

	public void beRepeated() {
		repeated = true;
	}

	public void beDeadline() {
		deadline = true;
	}

	public void addDeleteID(List<Integer> numbers) {
		ID = numbers;
	}

	public void addDoneID(List<Integer> numbers) {
		ID = numbers;
	}

	public void addEditID(int number) {
		callingID = number;
	}
	
	public void addTagID(int number) {
		callingID = number;
	}
	
	public void addEditCommand(String command) {
		afterCommand = command;
	}

	public void addShow(String command) {
		afterCommand = command;
	}

	public void addRepeatDate(RepeatDate content) {
		repeatDate = content;
	}

	public void addDate(List<Date> date) {
		dates = date;
	}

}
