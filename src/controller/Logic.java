package controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import model.TaskList;
import model.Task;
import model.DeadlineTask;
import model.FloatingTask;
import model.RepeatedTask;
import model.TimedTask;



/**
 *  main class that manages the TaskList
 */


public class Logic {
    static Scanner scanner = new Scanner(System.in);
    static Stack<UserInput> undoStack = new Stack<UserInput>();
    static Stack<UserInput> redoStack = new Stack<UserInput>();
    static TaskList listOfTasks ;
    
    static String MESSAGE_TASK_ADDED = "Task added successfully.";
    static String MESSAGE_TASK_EDITED = "Task edited successfully.";
    static String MESSAGE_TASK_DELETED = "Task(s) deleted successfully.";
    static String MESSAGE_TASK_CLEARED = "Task list cleared successfully.";
    static String MESSAGE_TASK_MARKED_DONE = "Task(s) marked done successfully.";
    
    static String MESSAGE_PROGRAM_REDO = "redo successful.";
    static String MESSAGE_PROGRAM_UNDO = "undo successful.";
    static String MESSAGE_PROGRAM_EXIT = "Program terminated successfully.";
    
    static String MESSAGE_INVALID_ADD_ = "";
    static String MESSAGE_INVALID_ADD_EMPTY_DESCRIPTION = "Error adding task: no description entered";
    static String MESSAGE_INVALID_EDIT_EMPTY_DESCRIPTION = "Error editing task: no description entered";
    static String MESSAGE_INVALID_DELETE = "Error deleting task(s).";
    static String MESSAGE_INVALID_MARKED_DONE = "Error marking task(s) done.";
    static String MESSAGE_INVALID_UNDO = "No previous operation to undo.";
    static String MESSAGE_INVALID_REDO = "No next operation to redo.";
    static String MESSAGE_INVALID_COMMAND = "Invalid command. Type 'help' to see the list of available commands.";
    
    
	static String MESSAGE_HELP = "Current list of available commands: \n" + 
                                 "- add a task              : add <description>\n" +
			                     "- edit a task description : edit <taskID> <description>\n" +
                                 "- delete task(s)          : delete <taskID> [<taskID> <taskID> ...]\n" + 
			                     "- clear all tasks         : clear\n" + 
                                 "- mark task(s) done       : done <taskID> [<taskID> <taskID> ...]\n" + 
			                     "- display all tasks       : show\n" + 
                                 "- exit the program        : exit";
    
	static Storage storage = new Storage(); 
//    public static void main(String[] args) {
//        // get existing tasks from storage
//    	listOfTasks = storage.load();
//    	
//        // get and execute new tasks
//        while (true) {
//        	String userInput = getUserInput();
//
//        	if (userInput.equalsIgnoreCase("help")) {
//        		System.out.println(help);
//        	} else {
//	        	// parse and execute command
//	        	Parser parser = new Parser();
//	        	UserInput userCommand = parser.parse(userInput);
//	        	executeCommand(userCommand);
//	
//	        	// update the history and storage file
//	        	storage.save(listOfTasks);
//        	}
//
//        }        
//    }
	
	/** 
	 *  @param userInput
	 *  @return feedback string
	 *  
	 *  this method gets the userInput from the UI, calls the parser for processing,
	 *  and executes the command given, returning the feedback string at the end.  
	 */
	public static String readAndExecuteCommands(String userInput) {
		// parse and execute command
		Parser parser = new Parser();
		UserInput userCommand = parser.parse(userInput);
		return executeCommand(userCommand);
	}
    
    /** 
     *  @param userCommand
     *  @return feedback string
     * 
     *  this method reads the UserInput object and executes the command given
     *  the commands will include to add/ edit/ delete a task, to undo/ redo 
     *  an operation, and more.
     */
    private static String executeCommand(UserInput userCommand) {
        
    	if (userCommand.getCommand() == UserInput.CMD.HELP) {
    		return MESSAGE_HELP;
    		
    	} else if (userCommand.getCommand() == UserInput.CMD.ADD) {
    		String desc = userCommand.getDescription();
    		List<Date> dateList = userCommand.getDate();
    		
    		if (desc == null) {
    			return MESSAGE_INVALID_ADD_EMPTY_DESCRIPTION;
    			
    		} else {
    			if (userCommand.isFloat()) {
    				return addTask(desc);
    				
    			} else if (userCommand.isDeadline()) {
    				
    				
    			} else if (userCommand.isRepeated()) {
    				
    				//userCommand.repeatDate()
    				//userCommand.getDate
    				
    			
    			// is fixed task
    			} else {
    				
    				
    			}
    			
    			return null;
    		}

    		
    	} else if (userCommand.getCommand() == UserInput.CMD.EDIT) {
    		
    		int editID = userCommand.getEditID();
    		String desc = userCommand.getDescription();
    		String editCommand = userCommand.getEditCommand();
    		
    		if ((editCommand == null) && (desc != null)) {
    			editTask(editID, desc);
    			return null;
    			
    		} else {
    			// other types of edits here
    			return MESSAGE_INVALID_EDIT_EMPTY_DESCRIPTION;
    			

    		}
    		
    		
    	} else if (userCommand.getCommand() == UserInput.CMD.DELETE) {
    		return deleteTask(userCommand.getDeleteID());
    		
    	} else if (userCommand.getCommand() == UserInput.CMD.SHOW) {
    		// call the UI to display the corresponding tasks here eventually
    		display();
    		return null;
    		
    		
    	} else if (userCommand.getCommand() == UserInput.CMD.CLEAR) {
    		return clearTaskList();

    	} else if (userCommand.getCommand() == UserInput.CMD.DONE) {
    		return markDone(userCommand.getDoneID());
    		
    	} else if (userCommand.getCommand() == UserInput.CMD.SEARCH) {
    		// search function here.
    		return null;
    		
    	} else if (userCommand.getCommand() == UserInput.CMD.EXIT) {
    		storage.close();
    		System.exit(0);
    		return MESSAGE_PROGRAM_EXIT;
    		
    	} else {
    		// other functions here
    		
    		return MESSAGE_INVALID_COMMAND;
    	}
    	
    }

	/**
     *  @param description 
     *  @param time 
     *  @param period
     *  @return feedback string
     *  
     *  the following methods will add a task to the file, 
     *  with the specified parameters
     */
    private static String addTask(String description) {
    	Task newTask = new FloatingTask(description);
    	listOfTasks.addToList(newTask);
    	return MESSAGE_TASK_ADDED;
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
     *  @return feedback string whether tasks are deleted successfully
     *  
     *  this method will delete the specified task(s) from the file
     */
    private static String deleteTask(List<Integer> taskIndexList) {
    	if (taskIndexList.size() == 0) {
    		return MESSAGE_INVALID_DELETE;
    		
    	} else {
    		try { 
    			listOfTasks.deleteFromList(taskIndexList);
    			return MESSAGE_TASK_DELETED;
    			
    		} catch (Exception e) {
    			return MESSAGE_INVALID_DELETE;
    			
    			
    			
    		}
    	}
    }
    
    /**
     *  @return feedback that task list is cleared
     *  
     *  this method clears the task list of all tasks
     */
    private static String clearTaskList() {
    	listOfTasks.clearList();
    	return MESSAGE_TASK_CLEARED;
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
     *  @return feedback whether strings are marked done
     *  
     *  this method marks that specified task(s) has been done
     */
    private static String markDone(List<Integer> taskIndexList) {
    	if (taskIndexList.size() == 0) {
    		return MESSAGE_INVALID_MARKED_DONE;
    	
    	} else {
    		try {
    			listOfTasks.markTaskDone(taskIndexList);
    			return MESSAGE_TASK_MARKED_DONE;
    			
    		} catch (Exception e) {
    			return MESSAGE_INVALID_MARKED_DONE;
    		}
    	}
    }
    
    
    /**
     *  @return feedback string
     *  
     *  this method calls the last operation and reverts any changes.
     *  this change can be re-obtained by using the redo method.
     */
    private static String undo() {
        if (undoStack.isEmpty()) {
            System.out.println("nothing to undo");
            return MESSAGE_INVALID_UNDO;
            
        } else {
            UserInput lastInput = undoStack.pop();
            redoStack.push(lastInput);
            
            // undo the step here
            
            return MESSAGE_PROGRAM_UNDO;
        }
    }
    
    /** 
     *  @return feedback string
     *  
     *  this method re-does the last operation undo-ed.
     */
    private static String redo() {
        if (redoStack.isEmpty()) {
            return MESSAGE_INVALID_REDO;
            
        } else {
            UserInput lastInput = redoStack.pop();
            undoStack.push(lastInput);
            // redo the step here
            
            return MESSAGE_PROGRAM_REDO;
        }
    }

    /**  
     *  this method reads the user input from the command line 
     *  and returns it as a string.
     */
    public static String getUserInput() {
        System.out.print("Enter command: ");
        String userInput = scanner.nextLine();
        return userInput;
    }
    
}
