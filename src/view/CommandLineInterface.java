package view;

import java.util.Scanner;

//import controller.Parser;
//import controller.UserInput;

public class CommandLineInterface {
	
	private final static String MESSAGE_WELCOME = "Hi, weclome to uClear!";
	private final static String MESSAGE_COMMAND = "Command: ";
	
	//private static Parser parser = new Parser();
	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		
		showToScreen(MESSAGE_WELCOME);
		executeCommand();
		scanner.close();

	}
	
	private static void showToScreen(String message) {
		
		System.out.println(message);
		
	}
	
	private static void executeCommand() {

		showToScreen(MESSAGE_COMMAND);
		while (true) {
			String input = scanner.nextLine();
			if (isExitCommand(input)) {
				break;
			}
			//UserInput output = parser.parse(input);
			showToScreen("Done!");
		}
		
	}
	
	private static boolean isExitCommand(String command) {
		
		if (command.trim().toLowerCase().equals("exit")) {
			return true;
		}
		return false;
		
	}

}
