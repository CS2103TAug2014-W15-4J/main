package controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * All write/read option in userInput: 
 * validation of cmd:
 *               unvalidation()/getValid(); 
 * allCommands: 
 *               addCommand(CMD);
 *               add(String)/--;
 * addCommand: 
 *               addDate(List<Date>);beRepeated();beDeadline();beFloat();
 *                        /getDescription();getDate();isFloat() or isRepeated or isDeadline();
 * editCommad;
 *          addDate(List<Date>);addEdit(int,String)
 *                      /getEditID();getDescription();getDate();getEditCommandÅiÅjÅG (the description and date need to be changed) 
 * deleteCommand:
 *          addDeleteID(List<Integer>)/getDeleteID(); 
 * doneCommand:
 *          addDoneID(List<Integer>)/getDoneID(); 
 * showCommand:
 *          addShow(string)/getShowCommand();
 * 
 * @author Lu Yuehan
 *
 */
public class UserInput {
	public enum CMD {
		ADD, DELETE, EDIT, CLEAR, DONE, SEARCH, EXIT, SHOW, HELP
	};

	private boolean valid = true;
	private CMD command = null;

	private List<Integer> ID = new ArrayList<Integer>();

	private int editID = 0;
	private String afterCommand = null;
	private String repeatDate = null;

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

	public String repeatDate() {
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
	
	public void addCommand(CMD userCommand){
		command = userCommand;		
	}

	public void add(String userDescription) {
		description = userDescription;
	}
	
	public void beFloat(){
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
		editID = number;
	}
	
	public void addEditCommand(String command) {
		afterCommand = command;
	}

	public void addShow(String command) {
		afterCommand = command;
	}

	public void addRepeatDate(String content) {
		repeatDate = content;
	}

	public void addDate(List<Date> date) {
		dates = date;
	}

}
