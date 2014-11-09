package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import log.ULogger;
import controller.UserInput.CMD;
import controller.UserInput.RepeatDate;

public class Parser {
	enum TaskType {
		FLOAT, REPEAT, FIX, DEADLINE
	};

	private static final String CMD_ADD = "add";
	private static final String CMD_DELETE = "delete";
	private static final String CMD_CLEAR = "clear";
	private static final String CMD_SEARCH = "search";
	private static final String CMD_EDIT = "edit";
	private static final String CMD_DONE = "done";
	private static final String SHOW_NEXT_WEEK = "next week";
	private static final String CMD_COMPLETE = "complete";
	private static final String CMD_FINISH = "finish";
	private static final String CMD_SHOW = "show";
	private static final String CMD_HELP = "help";
	private static final String CMD_EXIT = "exit";
	private static final String CMD_TAG = "tag";
	private static final String CMD_UNTAG = "untag";
	private static final String CMD_UNDO = "undo";
	private static final String CMD_REDO = "redo";
	private static final String CMD_EXPORT = "export";

	private static final String EDIT_NOTIME = "no-time";
	private static final String EDIT_NOREPEAT = "no-repeat";
	private static final String DEADLINE_ONETIME = "00000";
	private static final String END_OF_DAY_TIME = "235959999";
	private static final String BEGIN_OF_DAY_TIME = "000000000";
	private static final String SHOW_THIS_WEEK = "this week";
	private static ULogger log = ULogger.getLogger();
	
	//@author A0119387U
	/**
	 * this differs different kinds of command
	 * @param input String
	 * @return UserInput after parsing
	 *
	 **/

	public UserInput parse(String input) {

		String[] inputSplit = input.split(" ", 2);
		String command = inputSplit[0];
		String content = null;
		if (inputSplit.length == 2) {
			content = inputSplit[1].trim();
		}
		log.info("parse main function");
		switch (parseCommand(command.toLowerCase())) {
		  case CMD_EXIT:
			return parseExit(content);
		  case CMD_HELP:
			return parseHelp(content);
		  case CMD_ADD:
			return parseAdd(content);
		  case CMD_DELETE:
			return parseDelete(content);
		  case CMD_CLEAR:
			return parseClear(content);
		  case CMD_SEARCH:
			return parseSearch(content);
		  case CMD_EDIT:
			return parseEdit(content);
		  case CMD_DONE:
			return parseDone(content);
		  case CMD_COMPLETE:
		 	return parseDone(content);
		  case CMD_FINISH:
			return parseDone(content);
		  case CMD_SHOW:
			return parseShow(content);
	      case CMD_TAG:
			return parseTag(content);
		  case CMD_UNDO:
			return parseUndo(content);
		  case CMD_REDO:
			return parseRedo(content);
		  case CMD_UNTAG:
			return parseUntag(content);
		  case CMD_EXPORT:
			return parseExport(content);
		default:
			return errorCommand();
		}
	}
	//@author A0119387U
	/**
	 * give flexiable commands
	 * @param command String
	 * @return parsed command
	 */
	private String parseCommand(String command) {
		if (command.equals(CMD_EXIT) || command.equals("e"))
			return CMD_EXIT;
		if (command.equals(CMD_HELP) || command.equals("h"))
			return CMD_HELP;
		if (command.equals(CMD_ADD) || command.equals("+"))
			return CMD_ADD;
		if (command.equals(CMD_DELETE) || command.equals("-"))
			return CMD_DELETE;
		if (command.equals(CMD_CLEAR) || command.equals("--")
				|| command.equals("cl"))
			return CMD_CLEAR;
		if (command.equals(CMD_SEARCH) || command.equals("?")
				|| command.equals("s"))
			return CMD_SEARCH;
		if (command.equals(CMD_EDIT) || command.equals("<")
				|| command.equals("ed"))
			return CMD_EDIT;
		if (command.equals(CMD_DONE) || command.equals("d"))
			return CMD_DONE;
		if (command.equals(CMD_SHOW) || command.equals("sh"))
			return CMD_SHOW;
		if (command.equals(CMD_TAG) || command.equals("t"))
			return CMD_TAG;
		if (command.equals(CMD_UNTAG) || command.equals("ut"))
			return CMD_UNTAG;
		if (command.equals(CMD_UNDO) || command.equals("b"))
			return CMD_UNDO;
		if (command.equals(CMD_REDO) || command.equals("r"))
			return CMD_REDO;
		if (command.equals(CMD_EXPORT) || command.equals("ex"))
			return CMD_EXPORT;
		return command;
	}
	//@author A0119387U
	/**
	 * redo command parsing
	 * 
	 * @param content String
	 * @return input UserInput
	 */

	private UserInput parseRedo(String content) {
		log.info("entering redo command");
		if (content != null && !content.equals("")) {

			return errorCommand();
		}
		UserInput input = new UserInput();
		input.addCommand(CMD.REDO);
		log.info("exit redo command");
		return input;
	}
	//@author A0119387U
	/**
	 * undo command parsing
	 * 
	 * @param content String
	 * @return input UserInput
	 */

	private UserInput parseUndo(String content) {
		log.info("entering undo command");
		if (content != null && !content.equals("")) {

			return errorCommand();
		}
		UserInput input = new UserInput();
		input.addCommand(CMD.UNDO);
		log.info("exit undo command");
		return input;
	}
	//@author A0119387U
	/**
	 * 
	 *   this is for entering exit command
	 * @param content String
	 * @return input UserInput
	 *
	 */

	private UserInput parseExit(String content) {
		log.info("entering exit command");
		if (content != null && !content.equals("")) {

			return errorCommand();
		}
		UserInput input = new UserInput();
		input.addCommand(CMD.EXIT);
		log.info("exit exit command");
		return input;
	}
	//@author A0119387U
	/**
	 *   for help command
	 *
	 * @param content String
	 * @return input UserInput
	 *
	 *       
	 *
	 *
	 */

	private UserInput parseHelp(String content) {
		log.info("entering help command");
		if (content != null && !content.equals("")) {

			return errorCommand();
		}
		UserInput input = new UserInput();
		input.addCommand(CMD.HELP);
		log.info("exit help command");
		return input;
	}
	//@author A0119387U
	/**
	 *  this is for entering add command
	 * @param content String
	 * @return input UserInput
	 *
	 */

	private UserInput parseAdd(String content) {
		log.info("entering add command");
		if (content == null || content.equals("")) {
			return errorCommand();
		}
		UserInput input = new UserInput();
		input.addCommand(CMD.ADD);
		switch (taskType(content)) {
		case DEADLINE:
			return parseDeadline(input, content);
		case REPEAT:
			return parseRepeated(input, content);
		case FLOAT:
			return parseFloat(input, content);
		default:
			return parseFixed(input, content);
		}
	}
	//@author A0119387U
	/**
	 * 
	 *  this is for repeated task for add command or edit command
	 *
	 * @param input UserInput
	 * @param content String
	 * @return input UserInput
	 *
	 */

	private UserInput parseRepeated(UserInput input, String content) {
		log.info("repeated task found");
		ParseTime times = new ParseTime();
		String description = null;
		String repeatDate = null;
		String tempContent = null;
		try {
			String[] contentSplit = content.split(" ");
			repeatDate = contentSplit[contentSplit.length - 1];
		} catch (NullPointerException e1) {
			return parseFloat(input, content);
		}
		switch (repeatDate.trim().toLowerCase()) {
		case "daily": {
			input.addRepeatDate(RepeatDate.DAILY);
			tempContent = content.replaceAll("(?i)daily", "").trim();
			break;
		}
		case "weekly": {
			input.addRepeatDate(RepeatDate.WEEKLY);
			tempContent = content.replaceAll("(?i)weekly", "").trim();
			break;
		}
		case "monthly": {
			input.addRepeatDate(RepeatDate.MONTHLY);
			tempContent = content.replaceAll("(?i)monthly", "").trim();
			break;
		}
		default:
			return parseFloat(input, content);
		}
		System.out.println(tempContent);
		String[] contents = tempContent.split("(?i)every ",2);
		description = contents[0].trim();
		System.out.println(contents[1]);
		times.parseTime(contents[1]);
		if (description.equals("")&&!input.getCommand().equals(CMD.EDIT)) {
				return parseFloat(input, content);
		}
		List<Date> dates = times.getDates();
		if (dates.size() != 1) {
			return parseFloat(input, content);
		} else {
			input.add(description);
			input.beRepeated();
			input.addDate(dates);
			return input;
		}
	}
	//@author A0119387U
	/**
	 * 
	 *  this is for deadline task for add command or edit command
	 *
	 * @param input UserInput
	 * @param content String
	 * @return input UserInput
	 * @throws ParseException
	 */

	private UserInput parseDeadline(UserInput input, String content) {
		log.info("dealine task found");
		String description = null;
		ParseTime times = new ParseTime();
		String[] contents = content.split("(?i)by ",2);
		description = contents[0].trim();
		times.parseTime(contents[1]);
		if (input.getCommand() == CMD.ADD) {
			if (description.equals("")) {
				return parseFloat(input, content);
			}
		}
		List<Date> dates = times.getDates();
		if (dates.size() != 1) {
			return parseFloat(input, content);
		} else {
			SimpleDateFormat timeRestFormat = new SimpleDateFormat("ssSSS");
			String timeRest = timeRestFormat.format(dates.get(0));
			// System.out.println(timeRest);
			if (!timeRest.equals(DEADLINE_ONETIME)) {
				SimpleDateFormat timeFormat1 = new SimpleDateFormat("yyyyMMdd");
				String dateTime = timeFormat1.format(dates.get(0));
				// System.out.println(dateTime);
				SimpleDateFormat timeFormat2 = new SimpleDateFormat(
						"yyyyMMddHHmmssSSS");
				Date realDate = null;
				try {
					realDate = timeFormat2.parse(dateTime + END_OF_DAY_TIME);
				} catch (ParseException e) {
					realDate = dates.get(0);
				}
				dates.clear();
				dates.add(realDate);
			}
			input.add(description);
			input.beDeadline();
			input.addDate(dates);
			return input;
		}
	}
	//@author A0119387U
	/**
	 *
	 *  this is for fixed tasks for parseAdd and parseEdit
	 * @param input UserInput
	 * @param content String
	 * @return input UserInput
	 *  
	 */

	private UserInput parseFixed(UserInput input, String content) {
		log.info("fixed task found");
		String description = null;
		ParseTime times = new ParseTime();
		String[] contents = content.split("(?i)from ",2);
		description = contents[0].trim();
		times.parseTime(contents[1]);
		System.out.println(description+" "+contents[1]);
		if (input.getCommand() == CMD.ADD) {
			if (description.equals("")) {
				return parseFloat(input, content);
			}
		}
		List<Date> dates = times.getDates();
		if (dates.size() != 2) {
			return parseFloat(input, content);
		}
		input.add(description);
		input.addDate(dates);
		return input;
	}
	//@author A0119387U
	/**
	 *
	 *  this is for floating task for parseAdd or parseEdit
	 * @param input UserInput
	 * @param content String
	 * @return input UserInput
	 *
	 */

	private UserInput parseFloat(UserInput input, String content) {
		log.info("floating task found");
		if (content != null && !content.equals("")) {
			input.add(content);
		}
		input.beFloat();
		return input;
	}
	//@author A0119387U
	/**
	 * 
	 *   this is for check what kind of command by keywords
	 *
	 * @param content String
	 * @return TaskType
	 */

	private TaskType taskType(String content) {
		Pattern pattern = Pattern.compile(".*(?i)by(?-i) .+");
		Matcher matcher = pattern.matcher(content);
		if (matcher.matches()) {
			return TaskType.DEADLINE;
		}
		pattern = Pattern.compile(".*(?i)from(?-i) .+");
		matcher = pattern.matcher(content);
		if (matcher.matches()) {
			return TaskType.FIX;
		}
		pattern = Pattern.compile(".*(?i)every .+ daily(?-i)");
		matcher = pattern.matcher(content);
		if (matcher.matches()) {
			return TaskType.REPEAT;
		}
		pattern = Pattern.compile(".*(?i)every .+ weekly(?-i)");
		matcher = pattern.matcher(content);
		if (matcher.matches()) {
			return TaskType.REPEAT;
		}
		pattern = Pattern.compile(".*(?i)every .+ monthly(?-i)");
		matcher = pattern.matcher(content);
		if (matcher.matches()) {
			return TaskType.REPEAT;
		}
		return TaskType.FLOAT;
	}
	//@author A0119387U
	/**
	 * this is for entering delete command
	 * @param content String
	 * @return input UserInput
	 *
	 */

	private UserInput parseDelete(String content) {
		log.info("entering delete command");
		if (!trueNumberFormat(content)) {

			return errorCommand();
		}
		UserInput input = new UserInput();
		String[] numberInString = content.split(" ");
		int length = numberInString.length;
		List<Integer> number = new ArrayList<Integer>();
		for (int i = 0; i < length; i++) {
			if (!numberInString[i].equals("")) {
				number.add(Integer.valueOf(numberInString[i].trim()));
			}
		}
		input.addCommand(CMD.DELETE);
		input.add(null);
		input.addDeleteID(number);
		log.info("exit delete command");
		return input;
	}
	//@author A0119387U
	/**
	 * this is for entering clearing command	
	 * @param content String
	 * @return input UserInput         
	 */

	private UserInput parseClear(String content) {
		log.info("entering clear command");
		if (content != null && !content.equals("")) {

			return errorCommand();
		}
		UserInput input = new UserInput();
		input.addCommand(CMD.CLEAR);
		input.add(null);
		log.info("exit command");
		return input;
	}
	//@author A0119387U
	/**
	 * 	this is for entering edit command
	 * @param content String
	 * @return input UserInput
	 *
	 */

	private UserInput parseEdit(String content) {
		log.info("entering edit command");
		if (content == null || content.equals("")) {

			return errorCommand();
		}
		String[] contentSplit = content.split(" ", 2);
		if (contentSplit.length != 2) {
			return errorCommand();
		}
		String IDString = contentSplit[0].trim();
		String contentString = contentSplit[1].trim();
		UserInput input = new UserInput();
		if (!trueNumberFormat(IDString)) {
			return errorCommand();
		} else {
			input.addEditID(Integer.valueOf(IDString));
			input.addCommand(CMD.EDIT);
			input = parseEditCommand(input, contentString);
			content = input.getDescription();
			input.add("");
			input = parseEditTaskAndTime(input, content);
		}
		log.info("exit command");
		return input;
	}
	//@author A0119387U
	/**
	 * this is work for parsing edit
	 * @param input UserInput
	 * @param content String
	 * @return input UserInput
	 */
	private UserInput parseEditTaskAndTime(UserInput input, String content) {
		if (content == null || content.equals("")) {
			return input;
		}
		switch (taskType(content)) {
		case DEADLINE:
			return parseDeadline(input, content);
		case REPEAT:
			return parseRepeated(input, content);
		case FLOAT:
			return parseFloat(input, content);
		default:
			return parseFixed(input, content);
		}
	}
	//@author A0119387U
	/**
	 * for parsing special commands in edit command
	 * @param input UserInput
	 * @param content String
	 * @return input UserInput
	 */
	private UserInput parseEditCommand(UserInput input, String content) {
		input.add(content);
		if (content.toLowerCase().contains(EDIT_NOTIME)) {
			input.addEditCommand(EDIT_NOTIME);
			content = content.replaceAll("(?i)" + EDIT_NOTIME, "").trim();
			input.add(content);
		} else if (content.toLowerCase().contains(EDIT_NOREPEAT)) {
			input.addEditCommand(EDIT_NOREPEAT);
			content = content.replaceAll("(?i)" + EDIT_NOREPEAT, "").trim();
			input.add(content);
		}
		return input;
	}
	//@author A0119387U
	/**
	 *    this is for entering show command
	 * @param content String
	 * @return input UserInput
	 */

	private UserInput parseShow(String content) {
		log.info("entering show command");
		UserInput input = new UserInput();
		input.addCommand(CMD.SHOW);
		input.add(null);
		if (content == null || content.trim().equals("")) {
			input.addShow("all");
			log.info("exit command");
			return input;
		}
		input.addShow(content);
		List<Date> dates = new ArrayList<Date>();
		SimpleDateFormat timeFormat1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormat2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(today);
		
		if (content.toLowerCase().contains(SHOW_THIS_WEEK)) {
			if (!content.toLowerCase().replaceAll(SHOW_THIS_WEEK, "").trim()
					.equals("")) {
				input.add(content);
				log.info("exit command");
				return input;
			} else {
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				Date beginDate = cal.getTime();
				cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				Date endDate = cal.getTime();
				
				try {
					dates.add(timeFormat2.parse(timeFormat1.format(beginDate
							.getTime()) + BEGIN_OF_DAY_TIME));
				} catch (ParseException e) {
				}
				try {
					dates.add(timeFormat2.parse(timeFormat1.format(endDate
							.getTime()) + END_OF_DAY_TIME));
				} catch (ParseException e) {
				}
			}
			input.addDate(dates);
			log.info("exit command");
			return input;
		}
		if (content.toLowerCase().contains(SHOW_NEXT_WEEK)) {
			if (!content.toLowerCase().replaceAll(SHOW_NEXT_WEEK, "").trim()
					.equals("")) {
				input.add(content);
				return input;
			} else {
				cal.add(Calendar.DATE, 7);
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				Date beginDate = cal.getTime();
				cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				Date endDate = cal.getTime();
				
				try {
					dates.add(timeFormat2.parse(timeFormat1.format(beginDate
							.getTime()) + BEGIN_OF_DAY_TIME));
				} catch (ParseException e) {
				}
				try {
					dates.add(timeFormat2.parse(timeFormat1.format(endDate
							.getTime()) + END_OF_DAY_TIME));
				} catch (ParseException e) {
				}
			}
			input.addDate(dates);
			log.info("exit command");
			return input;
		}
		ParseTime times = new ParseTime();
		times.parseTime(content);
		dates = times.getDates();
		if (dates.size() > 2)
			return errorCommand();
		if (dates.size() == 1) {
			SimpleDateFormat timeRestFormat = new SimpleDateFormat("ssSSS");
			String timeRest = timeRestFormat.format(dates.get(0));
			// System.out.println(timeRest);
			if (!timeRest.equals(DEADLINE_ONETIME)) {
				String dateTime = timeFormat1.format(dates.get(0));
				// System.out.println(dateTime);
				Date realDate = null;
				try {
					realDate = timeFormat2.parse(dateTime + BEGIN_OF_DAY_TIME);
				} catch (ParseException e) {
					realDate = dates.get(0);
				}
				dates.clear();
				dates.add(realDate);
				try {
					realDate = timeFormat2.parse(dateTime + END_OF_DAY_TIME);
				} catch (ParseException e) {
					realDate = dates.get(0);
				}
				dates.add(realDate);
			}
		}
		if(dates.size()==2){
			SimpleDateFormat timeRestFormat = new SimpleDateFormat("ssSSS");
			// System.out.println(timeRest);
			if (!(timeRestFormat.format(dates.get(0)).equals(DEADLINE_ONETIME)||
					timeRestFormat.format(dates.get(1)).equals(DEADLINE_ONETIME))) {
				String dateBeginTime = timeFormat1.format(dates.get(0));
				String dateEndTime = timeFormat1.format(dates.get(1));
				Date realDate = null;
				dates.clear();
				try {
					realDate = timeFormat2.parse(dateBeginTime + BEGIN_OF_DAY_TIME);
				} catch (ParseException e) {
					realDate = dates.get(0);
				}
				dates.add(realDate);
				try {
					realDate = timeFormat2.parse(dateEndTime + END_OF_DAY_TIME);
				} catch (ParseException e) {
					realDate = dates.get(0);
				}
				dates.add(realDate);
			}
		}
		input.addDate(dates);
		log.info("exit command");
		return input;
	}
	//@author A0119387U
	/**
	 *   this is for entering search command
	 * @param content String
	 * @return input UserInput
	 */

	private UserInput parseSearch(String content) {
		log.info("entering search command");
		if (content == null || content.equals("")) {
			return errorCommand();
		}
		UserInput input = new UserInput();
		input.addCommand(CMD.SEARCH);
		input.add(content);
		log.info("exit command");
		return input;
	}
	//@author A0119387U
	/**
	 *  this is for entering mark done command	 
	 * @param content String
	 * @return input UserInput
	 */

	private UserInput parseDone(String content) {
		log.info("entering done command");
		if (!trueNumberFormat(content)) {

			return errorCommand();
		}
		UserInput input = new UserInput();
		String[] numberInString = content.split(" ");
		int length = numberInString.length;
		List<Integer> number = new ArrayList<Integer>();
		for (int i = 0; i < length; i++) {
			if (!numberInString[i].equals("")) {
				number.add(Integer.valueOf(numberInString[i].trim()));
			}
		}
		input.addCommand(CMD.DONE);
		input.add(null);
		input.addDoneID(number);
		log.info("exit command");
		return input;
	}
	//@author A0119387U
	/**
	 * tag command parsing
	 * 
	 * @param content String
	 * @return input UserInput
	 */
	private UserInput parseTag(String content) {
		log.info("entering tag command");
		if (content == null || content.equals("")) {

			return errorCommand();
		}
		String[] contentSplit = content.split(" ", 2);
		if (contentSplit.length != 2) {
			return errorCommand();
		}
		String IDString = contentSplit[0].trim();
		String tag = contentSplit[1].trim();
		UserInput input = new UserInput();
		if (!trueNumberFormat(IDString)) {
			return errorCommand();
		} else {
			input.addTagID(Integer.valueOf(IDString));
			input.addCommand(CMD.TAG);
			input.add(tag);
		}
		log.info("exit command");
		return input;
	}
	//@author A0119387U
	/**
	 * Untag command parsing
	 * 
	 * @param content String
	 * @return input UserInput
	 */

	private UserInput parseUntag(String content) {
		log.info("entering delete command");
		if (content == null || content.equals("")) {

			return errorCommand();
		}
		String[] contentSplit = content.split(" ", 2);
		if (contentSplit.length != 2) {
			return errorCommand();
		}
		String IDString = contentSplit[0].trim();
		String contentString = contentSplit[1].trim();
		UserInput input = new UserInput();
		if (!trueNumberFormat(IDString)) {
			return errorCommand();
		} else {
			input.addEditID(Integer.valueOf(IDString));
			input.addCommand(CMD.UNTAG);
			input.add(contentString);
		}
		log.info("exit command");
		return input;
	}

	private UserInput parseExport(String content) {
		log.info("entering export command");
		UserInput input = new UserInput();
		input.addCommand(CMD.EXPORT);
		return input;
	}
	//@author A0119387U
	/**
	 * 	 this is for checking if the contents are only numbers in done and
	 *  delete command
	 * @param content String
	 * @return boolean whether the content contents numbers only
	 *
	 */

	private boolean trueNumberFormat(String content) {
		if (content == null || content.equals("")) {
			return false;
		}
		for (char i : content.toCharArray()) {
			if (!Character.isDigit(i) && i != ' ') {
				return false;
			}
		}
		return true;
	}
	//@author A0119387U
	/**
	 *give the error command
	 * @return input UserInput
	 */

	private UserInput errorCommand() {
		UserInput input = new UserInput();
		input.unvalidation();
		log.info("error command");
		return input;
	}

}
