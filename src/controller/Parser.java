package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import controller.UserInput.CMD;

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
	private static final String EDIT_NOTIME = "no-time";
	private static final String EDIT_NOREPEAT = "no-repeat";

	/**
	 * @param input
	 * @return UserInput after parsing
	 *
	 *         this differs different kinds of command
	 **/

	public UserInput parse(String input) {
		String[] inputSplit = input.split(" ", 2);
		String command = inputSplit[0];
		String content = null;
		if (inputSplit.length == 2) {
			content = inputSplit[1].trim();
		}
		switch (command.toLowerCase()) {
			case CMD_EXIT :
				return parseExit(content);
			case CMD_HELP :
				return parseHelp(content);
			case CMD_ADD :
				return parseAdd(content);
			case CMD_DELETE :
				return parseDelete(content);
			case CMD_CLEAR :
				return parseClear(content);
			case CMD_SEARCH :
				return parseSearch(content);
			case CMD_EDIT :
				return parseEdit(content);
			case CMD_DONE :
				return parseDone(content);
			case CMD_COMPLETE :
				return parseDone(content);
			case CMD_FINISH :
				return parseDone(content);
			case CMD_SHOW :
				return parseShow(content);
			case CMD_TAG :
				return parseTag(content);
			default :
				return errorCommand();
		}
	}

	/**
	 * @param content
	 * @return UserInput
	 *
	 *         this is for entering exit command
	 */

	private UserInput parseExit(String content) {
		if (content != null && !content.equals("")) {
			return errorCommand();
		}
		UserInput input = new UserInput();
		input.addCommand(CMD.EXIT);
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
		if (content != null && !content.equals("")) {
			return errorCommand();
		}
		UserInput input = new UserInput();
		input.addCommand(CMD.HELP);
		return input;
	}

	/**
	 * @param content
	 * @return UserInput
	 *
	 *         this is for entering add command(temporally floating only)
	 */

	private UserInput parseAdd(String content) {
		if (content == null || content.equals("")) {
			return errorCommand();
		}
		ParseTime times = new ParseTime();
		times.parseTime(content);
		UserInput input = new UserInput();
		UserInput temp = null;
		input.addCommand(CMD.ADD);
		int timeSize = times.getDates().size();
		switch (taskType(content, times)) {
			case DEADLINE :
				return parseDeadline(input, content, times);
			case REPEAT :
				return parseRepeated(input, content, times);
			case FLOAT :
				return parseFloat(input, content);
			default : {
				if (timeSize == 1 || timeSize == 2) {
					temp = parseFixed(input, content, times);
					if (temp.getValid()) {
						return temp;
					} else {
						return parseFloat(input, content);
					}
				}
				return parseFloat(input, content);
			}
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

	private UserInput parseRepeated(UserInput input, String content,
	                                ParseTime times) {
		if (times.timeNull()) {
			return errorCommand();
		}
		String description = times.getText().replaceAll(" (?i)every", "")
		                          .trim();
		if (input.getCommand() == CMD.ADD) {
			if (description == null || description.equals("")) {
				return errorCommand();
			}
		}
		List<Date> dates = times.getDates();
		if (!times.isRepeated()) {
			return errorCommand();
		}
		if (dates.size() != 1) {
			return errorCommand();
		} else {
			input.add(description);
			input.beRepeated();

			input.addDate(dates);
			input.addRepeatDate(times.getText().trim());
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
	 */

	private UserInput parseDeadline(UserInput input, String content,
	                                ParseTime times) {
		if (times.timeNull()) {
			return errorCommand();
		}
		String description = times.getText().replaceAll(" (?i)by", "").trim();
		if (input.getCommand() == CMD.ADD) {
			if (description == null || description.equals("")) {
				return errorCommand();
			}
		}
		List<Date> dates = times.getDates();
		if (dates.size() != 1) {
			return errorCommand();
		} else {
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
		if (times.timeNull()) {
			return errorCommand();
		}
		String description = times.getText();
		if (input.getCommand() == CMD.ADD) {
			if (description == null || description.equals("")) {
				return errorCommand();
			}
		}
		List<Date> dates = times.getDates();
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
		pattern = Pattern.compile(".+ (?i)every(?-i) .+");
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
		return input;
	}

	/**
	 * @param content
	 * @return UserInput
	 *
	 *         this is for entering clearing command
	 */

	private UserInput parseClear(String content) {
		if (content != null && !content.equals("")) {
			return errorCommand();
		}
		UserInput input = new UserInput();
		input.addCommand(CMD.CLEAR);
		input.add(null);
		return input;
	}

	/**
	 * @param content
	 * @return UserInput
	 *
	 *         this is for entering edit command(temporally floating only)
	 */

	private UserInput parseEdit(String content) {
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
		return input;
	}

	private UserInput parseEditTaskAndTime(UserInput input, String content) {
		if (content == null || content.equals("")) {
			return input;
		}
		ParseTime times = new ParseTime();
		times.parseTime(content);
		UserInput temp = null;
		int timeSize = times.getDates().size();
		switch (taskType(content, times)) {
			case DEADLINE :
				return parseDeadline(input, content, times);
			case REPEAT :
				return parseRepeated(input, content, times);
			case FLOAT :
				return parseFloat(input, content);
			default : {
				if (timeSize == 1 || timeSize == 2) {
					temp = parseFixed(input, content, times);
					if (temp.getValid()) {
						return temp;
					} else {
						return parseFloat(input, content);
					}
				}
				return parseFloat(input, content);
			}
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
		if (content != null && !content.equals("")) {
			return errorCommand();
		}
		UserInput input = new UserInput();
		input.addCommand(CMD.SHOW);
		input.add(null);
		return input;
	}

	/**
	 * @param content
	 * @return UserInput
	 *
	 *         this is for entering search command(not begin yet)
	 */

	private UserInput parseSearch(String content) {
		if (content == null || content.equals("")) {
			return errorCommand();
		}
		UserInput input = new UserInput();
		input.addCommand(CMD.SEARCH);
		input.add(content);
		return input;
	}

	/**
	 * @param content
	 * @return UserInput
	 *
	 *         this is for entering mark done command
	 */

	private UserInput parseDone(String content) {
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
		return input;
	}

	private UserInput parseTag(String content) {
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
		return input;
	}

}
