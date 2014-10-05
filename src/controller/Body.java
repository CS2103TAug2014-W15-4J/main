package controller;

/**
 * 
 * This is to be used before the formal GUI
 *
 */
public class Body {
	public static void main(String[] args) {
		Storage storage = new Storage(); 
		// get existing tasks from storage
    	Logic.listOfTasks = storage.load();
    	
        // get and execute new tasks
        while (true) {
        	String userInput = Logic.getUserInput();

        	if (userInput.equalsIgnoreCase("help")) {
        		System.out.println(Logic.help);
        	} else {
	        	// parse and execute command
	        	Parser parser = new Parser();
	        	UserInput userCommand = parser.parse(userInput);
	        	Logic.executeCommand(userCommand);
	
	        	// update the history and storage file
	        	storage.save(Logic.listOfTasks);
        	}

        }
	}
}
