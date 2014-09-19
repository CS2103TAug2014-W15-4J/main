package controller;

import java.text.*;
import java.util.*;


public class userInput {
	
	private static String command=null;
	private static String event=null;
	private static boolean floating=true;
	private static Date beginTime;
	private static Date endTime;
	private static SimpleDateFormat timeFormat=new SimpleDateFormat ("yyyy-MM-dd HH:mm"); 
	
	public static String getCommand(){
		return command;
	}
	public static String getEvent(){
		return event;
	}
	public static boolean isFloat(){
		return floating;
	}
	public static void add(String userCommand,String userEvent,boolean userFloat){
		command=userCommand;
		event=userEvent;
		floating=userFloat;
	}
	public static void addBeginDate(String beginDate) throws ParseException{
		beginTime=timeFormat.parse(beginDate);
	}
	public static void addEndDate(String endDate) throws ParseException{
		endTime=timeFormat.parse(endDate);
	}

	public static Date getBeginTime(){
		return beginTime;
	}
	public static Date getEndTime(){
		return endTime;
	}
}
