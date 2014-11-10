package controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
//@author A0119387U
/**
 * parse the times in the command
 *
 */
public class ParseTime {
	private List<Date> dateList = new ArrayList<Date>();
    private	boolean isRecurreing = false;
	private Date recursUntil = null;
	private String time = null;

    /**	
     * Get the dates parsed by class, and returns the result in a list
     * @return List of parsed dates
     */
	public List<Date> getDates() {
		return dateList;
	}
	/**
	 * Check if the task is repeated, and returns the corresponding boolean value
	 * @return if it is repeated
	 */
	public boolean isRepeated() {
		return isRecurreing;
	}
	/**
	 * Check if the task contains time object, and returns the corresponding boolean value
	 * @return the recursive until date
	 */
	public Date recursUntil() {
		return recursUntil;
	}
	/**
	 * Get the description of the task
	 * @return the time in format of string
	 */
	public String getTime() {
		return time;
	}
	
	//@author A0119387U
	/**
	 * this function is the main function to parse the time
	 * @param input The time string given by parser
	 */
	public void parseTime(String input) {
		input = input.replaceAll("(?i) tmr ", " tomorrow ").trim();
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(input);
		for (DateGroup group : groups) {
            List<Date> dates = group.getDates();
            isRecurreing = group.isRecurring();
            recursUntil = group.getRecursUntil();
            dateList.addAll(dates);
            time = group.getText();
		}
	}

}
