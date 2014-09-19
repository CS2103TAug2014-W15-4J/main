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
	private static int number = 0;
	private static String event = null;
	private static boolean floating = true;
	private static Date beginTime;
	private static Date endTime;
	private static SimpleDateFormat timeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	public static boolean getValid() {
		return valid;
	}

	public static String getCommand() {
		return command;
	}
	
	public static int getNumber(){
		return number;
	}

	public static String getEvent() {
		return event;
	}

	public static boolean isFloat() {
		return floating;
	}

	public static void unvalidation() {
		valid = false;
	}

	public static void add(String userCommand, String userEvent,
			boolean userFloat,int num) {
		command = userCommand;
		event = userEvent;
		floating = userFloat;
		number=num;
	}

	public static void addBeginDate(String beginDate) throws ParseException {
		beginTime = timeFormat.parse(beginDate);
	}

	public static void addEndDate(String endDate) throws ParseException {
		endTime = timeFormat.parse(endDate);
	}

	public static Date getBeginDate() {
		return beginTime;
	}

	public static Date getEndDate() {
		return endTime;
	}
}
