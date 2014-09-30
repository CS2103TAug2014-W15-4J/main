package controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import model.Task;
import model.TaskList;

/**
 *  main class that manages the TaskList
 */


public class Logic {
    static Scanner scanner = new Scanner(System.in);
    static Stack<UserInput> undoStack = new Stack<UserInput>();
    static Stack<UserInput> redoStack = new Stack<UserInput>();
    static TaskList listOfTasks ;
	static String help = "current list of available commands: \n" + 
                         "-to add a task              : add <description>\n" +
			             "-to edit a task description : edit <taskID> <description>\n" +
                         "-to delete task(s)          : delete <taskID> [<taskID> <taskID> ...]\n" + 
			             "-to clear all tasks         : clear\n" + 
                         "-to mark task(s) done       : done <taskID> [<taskID> <taskID> ...]\n" + 
			             "-to display all tasks       : show\n" + 
                         "-to exit the program        : exit";
    
	static Storage storage = new Storage(); 
    public static void main(String[] args) {
        // get existing tasks from storage
    	listOfTasks = storage.load();
    	
        // get and execute new tasks
        while (true) {
        	String userInput = getUserInput();

        	if (userInput.equalsIgnoreCase("help")) {
        		System.out.println(help);
        	} else {
	        	// parse and execute command
	        	Parser parser = new Parser();
	        	UserInput userCommand = parser.parse(userInput);
	        	executeCommand(userCommand);
	
	        	// update the history and storage file
	        	storage.save(listOfTasks);
        	}

        }        
    }
    
    /** 
     *  @param userCommand
     * 
     *  this method reads the UserInput object and executes the command given
     *  the commands will include to add/ edit/ delete a task, to undo/ redo 
     *  an operation, and more.
     */
    private static void executeCommand(UserInput userCommand) {
        
    	if (userCommand.isAdd()) {
    		String desc = userCommand.getDescription();
    		
    		if (desc == null) {
    			System.out.println("no event entered");
    			
    		} else {
    			addTask(desc);
    			
    			// add other types of add tasks here after parameter checking.
    		}

    		
    	} else if (userCommand.isEdit()) {
    		
    		int editID = userCommand.getEditID();
    		String desc = userCommand.getDescription();
    		String editCommand = userCommand.getEditCommand();
    		
    		if ((editCommand == null) && (desc != null)) {
    			editTask(editID, desc);
    			
    		} else {
    			System.out.println("nothing to edit");
    			
    			// other types of edits here
    		}
    		
    		
    	} else if (userCommand.isDelete()) {
    		deleteTask(userCommand.getDeleteID());
    		
    	} else if (userCommand.isShow()) {
    		// call the UI to display the corresponding tasks here eventually
    		display();
    		
    		
    	} else if (userCommand.isClear()) {
    		clearTaskList();
    		System.out.println("list cleared");
    		
    		
    	} else if (userCommand.isDone()) {
    		markDone(userCommand.getDoneID());
    		
    	} else if (userCommand.isExit()) {
    		storage.close();
    		System.exit(0);
    		
    	} else {
    		// other functions here
    	
    		System.out.println("invalid.");
    		System.out.println("type 'help' to see the list of available commands");
    	}
    }
    
    


	/**
     *  @param description 
     *  @param time 
     *  @param period
     *  
     *  the following methods will add a task to the file, 
     *  with the specified parameters
     */
    private static void addTask(String description) {
    	Task newTask = new Task(description);
    	listOfTasks.addToList(newTask);
    }
    
    
    private static void addTask(String event, Date time) {
    	
    }
    
    private static void addTask(String event, Date time, Date Period) {
        
    }
    
    
    /** 
     *  @param taskIndex
     *  @param description
     *  @param time (for deadline/timed tasks)
     *  
     *  the following methods will edit a task in the file,
     *  with the specified parameters
     */
    private static void editTask(int taskIndex, String description) {
    	listOfTasks.editTaskDescription(taskIndex, description);
    }
    
    private static void editTask(int taskIndex, Date time) {
    	
    }
    
    private static void editTask(int taskIndex, String description, Date time) {
    	
    }
    
    /**
     *  @param taskIndexList
     *  
     *  this method will delete the specified task(s) from the file
     */
    private static void deleteTask(List<Integer> taskIndexList) {
    	if (taskIndexList.size() == 0) {
    		System.out.println("error, nothing stated to delete");
    		
    	} else {
    		listOfTasks.deleteFromList(taskIndexList);
    	}
    }
    
    private static void clearTaskList() {
    	listOfTasks.clearList();
    }
    
    /** 
     *  this (temporary) method displays the current list of tasks to the console.
     */
    private static void display() {
    	String taskDisplay = "Current tasks:\n";
    	ArrayList<Task> taskList = listOfTasks.getList();
	    
    	for (int i=0; i<listOfTasks.getNumberOfTasks(); i++) {
    		Task task = taskList.get(i);
    		taskDisplay += (i+1) + ". " + task.getDescription();
    		 
    		if (task.getIsDone()) {
    			taskDisplay += " (done)";
    		}
    		
    		taskDisplay += "\n";
    	}
    	
    	System.out.println(taskDisplay);
	    
    }
    
    /** 
     *  @param taskIndexList
     *  
     *  this method marks that a specified task has been done
     */
    private static void markDone(List<Integer> taskIndexList) {
    	if (taskIndexList.size() == 0) {
    		System.out.println("error, nothing to mark done");
    	
    	} else {
    		listOfTasks.markTaskDone(taskIndexList);
    	}
    }
    
    
    /**
     *  this method calls the last operation and reverts any changes.
     *  this change can be re-obtained by using the redo method.
     */
    private static void undo() {
        if (undoStack.isEmpty()) {
            System.out.println("nothing to undo");
            
        } else {
            UserInput lastInput = undoStack.pop();
            redoStack.push(lastInput);
            
            // undo the step here
        }
    }
    
    /** 
     *  this method re-does the last operation undo-ed.
     */
    private static void redo() {
        if (redoStack.isEmpty()) {
            System.out.println("nothing to redo");
            
        } else {
            UserInput lastInput = redoStack.pop();
            undoStack.push(lastInput);
            
            // redo the step here
        }
    }

    /**  
     *  this method reads the user input from the command line 
     *  and returns it as a string.
     */
    private static String getUserInput() {
        System.out.print("Enter command: ");
        String userInput = scanner.nextLine();
        return userInput;
    }
    
}
