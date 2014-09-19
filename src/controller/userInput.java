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
public class userInput {
	private static boolean valid = true;
	private static String command = null;
	private static List<Integer> deleteNum = new ArrayList<Integer>();
	private static int editNum = 0;
	private static String event = null;
	private static boolean floating = true;
	private static Date beginTime;
	private static Date endTime;
	private static SimpleDateFormat timeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	public boolean getValid() {
		return valid;
	}

	public String getCommand() {
		return command;
	}

	public List<Integer> getDeleteNumber() {
		return deleteNum;
	}
	
	public int getEditNumber() {
		return editNum;
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

	public void addDeleteNumber(List<Integer> numbers) {
		deleteNum = numbers;
	}

	public void addEditNumber(int number) {
		editNum = number;
	}

	public void addBeginDate(String beginDate) throws ParseException {
		beginTime = timeFormat.parse(beginDate);
	}

	public void addEndDate(String endDate) throws ParseException {
		endTime = timeFormat.parse(endDate);
	}

}
