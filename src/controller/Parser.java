package controller;

import java.util.ArrayList;
import java.util.List;


public class Parser {
	private static final String CMD_ADD = "add";
	private static final String CMD_DELETE = "delete";
	private static final String CMD_CLEAR = "clear";
	private static final String CMD_SEARCH = "search";
	private static final String CMD_EDIT = "edit";
	private static final String CMD_DONE = "done";
	private static final String CMD_COMPLETE = "complete";
	private static final String CMD_FINISH = "finish";
	private static final String CMD_SHOW = "show";
	private static final String CMD_EXIT = "exit";
	
/**
 * @param input
 * @return UserInput after parsing
 * 
 * this differs different kinds of command
 **/
	
	public UserInput parse(String input) {
		String[] inputSplit = input.split(" ", 2);
		String command = inputSplit[0];
		String content = null;
		if (inputSplit.length == 2) {
			content = inputSplit[1].trim();
		}
		switch (command.toLowerCase()) {
		case CMD_EXIT:
			return parseExit(content);
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
		default:
			return errorCommand();
		}
	}
	
/**
 * @param content
 * @return UserInput
 * 
 * this is for entering exit command
 */
	
	private UserInput parseExit(String content) {
		UserInput input = new UserInput();
		input.beExit();
		return input;
	}
	
	/**
	 * @param content
	 * @return UserInput
	 * 
	 * this is for entering add command(temporally floating only)
	 */
	
	private UserInput parseAdd(String content) {
		if (content == null || content.equals(""))
			return errorCommand();
			UserInput input = new UserInput();
			input.beAdd();
			input.add(CMD_ADD, content, true);
			return input;
	}
	
	/**
	 * @param content
	 * @return UserInput
	 * 
	 * this is for entering delete command
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
		input.beDelete();
		input.add(CMD_DELETE, "", true);
		input.addDeleteID(number);
		return input;
	}
	
	/**
	 * @param content
	 * @return UserInput
	 * 
	 * this is for entering clearing command
	 */

	private UserInput parseClear(String content) {
		if (content != null && !content.equals("")) {
			return errorCommand();
		}
		UserInput input = new UserInput();
		input.beClear();
		input.add(CMD_CLEAR, "", true);
		return input;
	}
	
	/**
	 * @param content
	 * @return UserInput
	 * 
	 * this is for entering edit command(temporally floating only)
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
			input.beEdit();
			input.addEdit(Integer.valueOf(IDString), null);
			input.add(CMD_EDIT, contentString, true);
		}
		return input;
	}
	
	/**
	 * @param content
	 * @return UserInput
	 * 
	 * this is for entering show command(temporally show all only)
	 */

	private UserInput parseShow(String content) {
		if (content != null && !content.equals("")) {
			return errorCommand();
		}
		UserInput input = new UserInput();
		input.beShow();
		input.add(CMD_SHOW, null, true);
		return input;
	}
	
	/**
	 * @param content
	 * @return UserInput
	 * 
	 * this is for entering search command(not begin yet)
	 */

	private UserInput parseSearch(String content) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @param content
	 * @return UserInput
	 * 
	 * this is for entering mark done command
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
		input.beDone();
		input.add(CMD_DONE, "", true);
		input.addDoneID(number);
		return input;
	}
/**
 * @param content
 * @return whether the content contents numbers only
 * 
 * this is for checking if the contents are only numbers in done and delete command
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
	 * give the error command
	 */

	private UserInput errorCommand() {
		UserInput input = new UserInput();
		input.unvalidation();
		return input;
	}

}
