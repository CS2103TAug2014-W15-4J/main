package controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * All write/read option in userInput:
 * validation of cmd: 
 *                    unvalidation()/getValid();
 * allCommands: 
 *                    add(CMD,String,boolean)/--;
 * addCommand:
 *                    addDate(List<Date>)/getDescription();getDate();
 * editCommad;
 *                    addDate(List<Date>);addEdit(int,String)/getEditID();getDescription();getDate();
 *                                                   (the description and date need to be changed)
 * deleteCommand:
 *                    addDeleteID(List<Integer>)/getDeleteID();
 * doneCommand:
 *                    addDoneID(List<Integer>)/getDoneID();
 * showCommand:
 *                    addShow(string)/getShowCommand();
 *                 
 * @author Lu Yuehan
 *
 */
public class UserInput {
	public enum CMD {
		ADD,DELETE,EDIT,CLEAR,DONE,SEARCH,EXIT,SHOW
	};
	
	private boolean valid = true;
	private CMD command = null;
	
	private List<Integer> ID = new ArrayList<Integer>();

	private int editID = 0;
	private String afterCommand = null;

	private String description = null;

	private boolean floating = false;

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
		return editID;
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

	public boolean isFloat() {
		return floating;
	}

	public void unvalidation() {
		valid = false;
	}

	public void add(CMD userCommand, String userDescription,
			boolean userFloat) {
		command = userCommand;
		description = userDescription;
		floating = userFloat;
	}

	public void addDeleteID(List<Integer> numbers) {
		ID = numbers;
	}

	public void addDoneID(List<Integer> numbers) {
		ID = numbers;
	}

	public void addEdit(int number, String command) {
		editID = number;
		afterCommand = command;
	}
	
	public void addShow(String command) {
		afterCommand = command;
	}
	
	public void addDate(List<Date> date){
		dates = date;
	}



}
