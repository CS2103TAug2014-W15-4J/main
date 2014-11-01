package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private static final String SHOW_NEXT_WEEK = "next week";
	private static Logger log = Logger.getLogger("controller.Parser");

	/**
	 * @param input
	 * @return UserInput after parsing
	 *
	 *         this differs different kinds of command
	 **/

	public UserInput parse(String input) {
		// let the logger only display warning log message.
		log.setLevel(Level.WARNING);

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

	/**
	 * redo command parsing
	 * 
	 * @param content
	 * @return UserInput
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

	/**
	 * undo command parsing
	 * 
	 * @param content
	 * @return UserInput
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

	/**
	 * @param content
	 * @return UserInput
	 *
	 *         this is for entering exit command
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

	/**
	 *
	 * @param content
	 * @return UserInput
	 *
	 *         for help command
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

	/**
	 * @param content
	 * @return UserInput
	 *
	 *         this is for entering add command(temporally floating only)
	 */

	private UserInput parseAdd(String content) {
		log.info("entering add command");
		if (content == null || content.equals("")) {
			return errorCommand();
		}
		ParseTime times = new ParseTime();
		times.parseTime(content);
		UserInput input = new UserInput();
		input.addCommand(CMD.ADD);
		switch (taskType(content, times)) {
		case DEADLINE:
			return parseDeadline(input, content, times);
		case REPEAT:
			return parseRepeated(input, content);
		case FLOAT:
			return parseFloat(input, content);
		default:
			return parseFixed(input, content, times);
		}
	}

	/**
	 *
	 * @param input
	 * @param content
	 * @param times
	 * @return UserInput for parseAdd or parseEdit
	 *
	 *         this is for repeated task for add command or edit command
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
		times.parseTime(tempContent);
		try {
			description = times.getText().replaceAll(" (?i)every", "").trim();
		} catch (NullPointerException e2) {
			return parseFloat(input, content);
		}
		if (input.getCommand() == CMD.ADD) {
			if (description.equals("")) {
				return parseFloat(input, content);
			}
		}
		List<Date> dates = times.getDates();
		if (!times.isRepeated()) {
			return parseFloat(input, content);
		}
		if (dates.size() != 1) {
			return parseFloat(input, content);
		} else {
			input.add(description);
			input.beRepeated();
			input.addDate(dates);
			return input;
		}
	}

	/**
	 *
	 * @param input
	 * @param content
	 * @param times
	 * @return UserInput for parseAdd or parseEdit
	 *
	 *         this is for deadline task for add command or edit command
	 * @throws ParseException
	 */

	private UserInput parseDeadline(UserInput input, String content,
			ParseTime times) {
		log.info("dealine task found");
		String description = null;
		try {
			description = times.getText().replaceAll(" (?i)by", "").trim();
		} catch (NullPointerException e) {
			return parseFloat(input, content);
		}
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

	/**
	 *
	 * @param input
	 * @param content
	 * @param times
	 * @param taskType
	 * @return UserInput for parseAdd or parseEdit
	 *
	 *         this is for fixed tasks for parseAdd and parseEdit
	 */

	private UserInput parseFixed(UserInput input, String content,
			ParseTime times) {
		log.info("fixed task found");
		String description = null;
		if (times.getDates().size() == 1) {
			return parseDeadline(input, description, times);
		}
		try {
			description = times.getText();
		} catch (NullPointerException e) {
			return parseFloat(input, content);
		}
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

	/**
	 *
	 * @param input
	 * @param content
	 * @return UserInput for parseAdd or parseEdit
	 *
	 *         this is for floating task for parseAdd or parseEdit
	 */

	private UserInput parseFloat(UserInput input, String content) {
		log.info("floating task found");
		if (content != null && !content.equals("")) {
			input.add(content);
		}
		input.beFloat();
		return input;
	}

	/**
	 *
	 * @param content
	 * @return TaskType
	 *
	 *         this is for check what kind of command by keywords
	 */

	private TaskType taskType(String content, ParseTime times) {
		Pattern pattern = Pattern.compile(".+ (?i)by(?-i) .+");
		Matcher matcher = pattern.matcher(content);
		if (matcher.matches()) {
			return TaskType.DEADLINE;
		}
		pattern = Pattern.compile(".+ (?i)every .+ daily(?-i)");
		matcher = pattern.matcher(content);
		if (matcher.matches()) {
			return TaskType.REPEAT;
		}
		pattern = Pattern.compile(".+ (?i)every .+ weekly(?-i)");
		matcher = pattern.matcher(content);
		if (matcher.matches()) {
			return TaskType.REPEAT;
		}
		pattern = Pattern.compile(".+ (?i)every .+ monthly(?-i)");
		matcher = pattern.matcher(content);
		if (matcher.matches()) {
			return TaskType.REPEAT;
		}
		if (times.getDates().size() == 0) {
			return TaskType.FLOAT;
		} else {
			return TaskType.FIX;
		}
	}

	/**
	 * @param content
	 * @return UserInput
	 *
	 *         this is for entering delete command
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

	/**
	 * @param content
	 * @return UserInput
	 *
	 *         this is for entering clearing command
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

	/**
	 * @param content
	 * @return UserInput
	 *
	 *         this is for entering edit command(temporally floating only)
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

	private UserInput parseEditTaskAndTime(UserInput input, String content) {
		if (content == null || content.equals("")) {
			return input;
		}
		ParseTime times = new ParseTime();
		times.parseTime(content);
		switch (taskType(content, times)) {
		case DEADLINE:
			return parseDeadline(input, content, times);
		case REPEAT:
			return parseRepeated(input, content);
		case FLOAT:
			return parseFloat(input, content);
		default:
			return parseFixed(input, content, times);
		}
	}

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

	/**
	 * @param content
	 * @return UserInput
	 *
	 *         this is for entering show command(temporally show all only)
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
		SimpleDateFormat weekFormat = new SimpleDateFormat("EEE");
		Date today = new Date();
		if (content.toLowerCase().contains(SHOW_THIS_WEEK)) {
			if (!content.toLowerCase().replaceAll(SHOW_THIS_WEEK, "").trim()
					.equals("")) {
				input.add(content);
				log.info("exit command");
				return input;
			} else {
				Calendar beginDate = Calendar.getInstance();
				Calendar endDate = Calendar.getInstance();
				if (weekFormat.format(today).equals("Sun")) {
					beginDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
					endDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				} else {
					beginDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
					endDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
					for (int i = 0; i < Calendar.DAY_OF_WEEK; i++)
						endDate.roll(Calendar.DATE, true);
				}
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
				Calendar beginDate = Calendar.getInstance();
				Calendar endDate = Calendar.getInstance();
				if (weekFormat.format(today).equals("Sun")) {
					beginDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
					for (int i = 0; i < Calendar.DAY_OF_WEEK; i++)
						beginDate.roll(Calendar.DATE, true);
					endDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
					for (int i = 0; i < Calendar.DAY_OF_WEEK; i++)
						endDate.roll(Calendar.DATE, true);

				} else {
					beginDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
					for (int i = 0; i < Calendar.DAY_OF_WEEK; i++)
						beginDate.roll(Calendar.DATE, true);
					endDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
					for (int i = 0; i < 2*Calendar.DAY_OF_WEEK; i++)
						endDate.roll(Calendar.DATE, true);
				}
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
		input.addDate(dates);
		log.info("exit command");
		return input;
	}

	/**
	 * @param content
	 * @return UserInput
	 *
	 *         this is for entering search command(not begin yet)
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

	/**
	 * @param content
	 * @return UserInput
	 *
	 *         this is for entering mark done command
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

	/**
	 * Untag command parsing
	 * 
	 * @param content
	 * @return
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

	/**
	 * @param content
	 * @return whether the content contents numbers only
	 *
	 *         this is for checking if the contents are only numbers in done and
	 *         delete command
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

	/**
	 *
	 * @return UserInput
	 *
	 *         give the error command
	 */

	private UserInput errorCommand() {
		UserInput input = new UserInput();
		input.unvalidation();
		log.info("error command");
		return input;
	}

}
