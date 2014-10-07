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
    	// get and execute new tasks
        while (true) {
        	String userInput = Logic.getUserInput();
        	// parse and execute command
        	System.out.println(Logic.readAndExecuteCommands(userInput));
        	// update the history and storage file
        	storage.save(Logic.listOfTasks);
        }        
	}
}
