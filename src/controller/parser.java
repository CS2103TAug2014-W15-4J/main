package controller;

import java.util.*;

public class parser {
	private static final String CMD_ADD = "add";
	private static final String CMD_DELETE = "delete";
	private static final String CMD_CLEAR = "clear";
	private static final String CMD_SEARCH = "search";
	private static final String CMD_EDIT = "edit";
	private static final String CMD_DISPLAY = "display";
	private static final String CMD_DONE = "done";
	private static final String CMD_COMPLETE = "complete";
	private static final String CMD_FINISH = "finish";
	private static final String CMD_SHOW = "show";

	public static userInput parse(String input) {
		String[] inputSplit = input.split(" ", 2);
		String command = inputSplit[0];
		String content = null;
		if (inputSplit.length == 2)
			content = inputSplit[1].trim();
		switch (command) {
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
		case CMD_DISPLAY:
			return parseDisplay(content);
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

	private static userInput parseAdd(String content) {
		if (content == null || content == "")
			return errorCommand();
		else {
			userInput input = new userInput();
			input.add(CMD_ADD, content.trim(), true);
			return input;
		}
	}

	private static userInput parseDelete(String content) {
		userInput input = new userInput();
		int number = 0;
		Integer.valueOf(content.trim());
		input.add(CMD_DELETE, "", true);
		return input;
	}

	private static boolean trueDeleteFormat(String content) {

	}

	private static userInput parseClear(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	private static userInput parseSearch(String content) {
		// TODO Auto-generated method stub
		return input;
	}

	private static userInput parseEdit(String content) {
		// TODO Auto-generated method stub
		return input;
	}

	private static userInput parseShow(String content) {
		// TODO Auto-generated method stub
		return input;
	}

	private static userInput parseDone(String content) {
		// TODO Auto-generated method stub
		return input;
	}

	private static userInput parseDisplay(String content) {
		// TODO Auto-generated method stub
		return input;
	}

	private static userInput errorCommand() {
		userInput input = new userInput();
		input.unvalidation();
		return input;
	}

}
