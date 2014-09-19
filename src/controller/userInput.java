package controller;

import java.text.*;
import java.util.*;

/**
 * 
 * Object passing from parser to logic The format of time is "yyyy-MM-dd HH:mm"
 * (For command with only one time, default we use addBeginDate and
 * getBeginDate)
 * 
 * @author Lu Yuehan
 *
 */
public class UserInput {
	private boolean valid = true;
	private String command = null;
	private List<Integer> deleteID = new ArrayList<Integer>();
	private int editID = 0;
	private String editCommand = null;
	private String event = null;
	private boolean floating = true;
	private Date beginTime;
	private Date endTime;
	private static SimpleDateFormat timeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	public boolean getValid() {
		return valid;
	}

	public String getCommand() {
		return command;
	}

	public List<Integer> getDeleteID() {
		return deleteID;
	}

	public int getEditID() {
		return editID;
	}

	public String getEditCommand() {
		return editCommand;
	}

	public String getEvent() {
		return event;
	}

	public boolean isFloat() {
		return floating;
	}

	public Date getBeginDate() {
		return beginTime;
	}

	public Date getEndDate() {
		return endTime;
	}

	public void unvalidation() {
		valid = false;
	}

	public void add(String userCommand, String userEvent, boolean userFloat) {
		command = userCommand;
		event = userEvent;
		floating = userFloat;
	}

	public void addDeleteID(List<Integer> numbers) {
		deleteID = numbers;
	}

	public void addEdit(int number, String command) {
		editID = number;
		editCommand = command;
	}

	public void addBeginDate(String beginDate) throws ParseException {
		beginTime = timeFormat.parse(beginDate);
	}

	public void addEndDate(String endDate) throws ParseException {
		endTime = timeFormat.parse(endDate);
	}

}
