package controller;

import java.util.*;

public class parser {
	private static final String[] CMD_ARRAY = { "add", "delete", "search",
			"edit", "display", "redo", "undo", "clear", "tag", "done",
			"complete", "finish", "show" };
	private static final String CMD_ADD = "add";
	private static final String CMD_DELETE = "delete";
	private static final String CMD_SEARCH = "search";
	private static final String CMD_EDIT = "edit";
	private static final String CMD_DISPLAY = "display";
	private static final String CMD_DONE = "done";
	private static final String CMD_COMPLETE = "complete";
	private static final String CMD_FINISH =  "finish";
	private static final String CMD_SHOW = "show";

	public static userInput parse(String input) {
		String command = input.split(" ", 2)[0];
		String content = input.split(" ", 2)[1];
		switch (command) {
		case CMD_ADD:
			return parseAdd(content);
		case CMD_DELETE:
			return parseDelete(content);
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
			return errorCommand(content);
		}
	}

	private static userInput parseAdd(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	private static userInput parseDelete(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	private static userInput parseSearch(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	private static userInput parseEdit(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	private static userInput parseShow(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	private static userInput parseDone(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	private static userInput parseDisplay(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	private static userInput errorCommand(String content) {
		return null;
	}

}
