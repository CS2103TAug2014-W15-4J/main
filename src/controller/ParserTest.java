package controller;

public class ParserTest {
	public static void main(String[] args) {
        while (true) {
        	String userInput = Logic.getUserInput();
	        	Parser parser = new Parser();
	        	UserInput userCommand = parser.parse(userInput);
	        	System.out.println(userCommand.getCommand());
	        	System.out.println(userCommand.getDate());
	        	System.out.println(userCommand.getDescription());

        	}

        }
	}
