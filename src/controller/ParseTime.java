package controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
/**
 * parse the times in the command
 * 
 * @author Lu Yuehan
 *
 */
public class ParseTime {
	private List<Date> dateList = new ArrayList<Date>();
    private	boolean isRecurreing = false;
	private Date recursUntil = null;
	private String time = null;
	private String text = null;
	
	public List<Date> getDates(){
		return dateList;
	}
	
	public boolean isRepeated(){
		return isRecurreing;
	}
	
	public Date recursUntil(){
		return recursUntil;
	}
	public boolean timeNull(){
		if(time == null)
			return true;
		return false;
	}
	public String getText(){
		return text.replaceAll(time, "").trim();
	}
	
	public void parseTime(String input){
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(input);
		for(DateGroup group:groups) {
		  List<Date> dates = group.getDates();
		  isRecurreing = group.isRecurring();
		  recursUntil = group.getRecursUntil();
		  dateList.addAll(dates);
		  time = group.getText();
		  text=input;
		}
	}

}
