package controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import model.TaskList;
import model.Task;
import model.DeadlineTask;
import model.FloatingTask;
import model.RepeatedTask;
import model.FixedTask;


/**
 *  main class that manages the TaskList
 */


public class Logic {
    static Scanner scanner = new Scanner(System.in);
    static Stack<UserInput> undoStack = new Stack<UserInput>();
    static Stack<UserInput> redoStack = new Stack<UserInput>();
    static TaskList listOfTasks;
    
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
    static String MESSAGE_INVALID_ADD_DATE_ERROR = "Error adding task: date error";
    static String MESSAGE_INVALID_EDIT = "Error editing task.";
    static String MESSAGE_INVALID_EDIT_DATE_ERROR = "Error editing task: date error";
    static String MESSAGE_INVALID_EDIT_EMPTY_DESCRIPTION = "Error editing task: empty description";
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
    public static void main(String[] args) {
        // get existing tasks from storage
    	listOfTasks = storage.load();
    	
        // get and execute new tasks
        while (true) {
        	String userInput = getUserInput();

	        	// parse and execute command
	        	System.out.println(readAndExecuteCommands(userInput));
	        	// update the history and storage file
	        	storage.save(listOfTasks);


        }        
    }
	
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
    		
    		if ((desc == null) || (desc.isEmpty())) {
    			return MESSAGE_INVALID_ADD_EMPTY_DESCRIPTION;
    			
    		} else {
    			if (userCommand.isFloat()) {
    				return addTask(desc);
    				
    			} else if (userCommand.isDeadline()) {
    				if (dateList.size() == 1) {
    					Date date = dateList.get(0);
    					return addTask(desc, date);
    					
    				} else {
    					return MESSAGE_INVALID_ADD_DATE_ERROR;
    				}
    			} else if (userCommand.isRepeated()) {
    				
    				if (dateList.size() == 1) {
    					Date date = dateList.get(0);
    					String repeatDate = userCommand.repeatDate();
    					return addTask(desc, date, repeatDate);
    				
    				} else {
    					return MESSAGE_INVALID_EDIT_DATE_ERROR;
    				}
    				
    			// is fixed task
    			} else {
    				
    				return null;
    				
    			}
    		}

    		
    	} else if (userCommand.getCommand() == UserInput.CMD.EDIT) {
    		
    		int editID = userCommand.getEditID();
    		String desc = userCommand.getDescription();
    		String editCommand = userCommand.getEditCommand();
    		List<Date> dateList = userCommand.getDate();
    		
    		
    		if (editCommand != null) {
    			// additional functions
    			// editCommand --> no-repeat / no-time
    			return null;
    			
    		} else if (desc == null) {
    			return MESSAGE_INVALID_EDIT;
    			
    		} else if (userCommand.isFloat()) {
    			if (desc.isEmpty()) {
    				return MESSAGE_INVALID_EDIT_EMPTY_DESCRIPTION;

    			} else {
    				return editTask(editID, desc);
    			}
    			
    		} else if (userCommand.isDeadline()) {
				Date date = dateList.get(0);
				
    			if ((dateList.size() == 1) && (!desc.isEmpty())) {
					return editTask(editID, desc, date);
					
				} else if (!desc.isEmpty()) {
					return editTask(editID, desc);
					
				} else if (dateList.size() == 1) {
					return editTask(editID, date);
					
				} else {
					return MESSAGE_INVALID_EDIT_DATE_ERROR;
				}
    			
    		} else if (userCommand.isRepeated()) {
    			Date date = dateList.get(0);
    			
    			if ((dateList.size() == 1) && (!desc.isEmpty())) {
    				return editTask(editID, desc, date);
    				
    			} else if (!desc.isEmpty()) {
    				return editTask(editID, desc);
    				
    			} else if (dateList.size() == 1) {
    				return editTask(editID, date);
    				
    			} else {
    				return MESSAGE_INVALID_EDIT_DATE_ERROR;
				}
    			
    		} else {
    			// other types of edits here
    			return MESSAGE_INVALID_EDIT;
    		}
    		
    		
    	} else if (userCommand.getCommand() == UserInput.CMD.DELETE) {
    		return deleteTask(userCommand.getDeleteID());
    		
    	} else if (userCommand.getCommand() == UserInput.CMD.SHOW) {
    		// call the UI to display the corresponding tasks here eventually
    		return display();
    		
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
     *  (floating, deadline, repeated, fixed tasks respectively)
     */
    private static String addTask(String description) {
    	Task newTask = new FloatingTask(description);
    	listOfTasks.addToList(newTask);
    	return MESSAGE_TASK_ADDED;
    }
    
    
    private static String addTask(String description, Date time) {
    	Task newTask = new DeadlineTask(description, time);
    	listOfTasks.addToList(newTask);
    	return MESSAGE_TASK_ADDED;
    }
    
    private static String addTask(String description, Date time, String repeatDate) {
    	Task newTask = new RepeatedTask(description, time, repeatDate);
    	listOfTasks.addToList(newTask);
    	return MESSAGE_TASK_ADDED;
        
    }
    
    
    /** 
     *  @param taskIndex
     *  @param description
     *  @param time (for deadline/timed tasks)
     *  @return feedback string
     *  
     *  the following methods will edit a task in the file,
     *  with the specified parameters
     */
    private static String editTask(int taskIndex, String description) {
    	try {
	        listOfTasks.editTaskDescription(taskIndex, description);
	        return MESSAGE_TASK_EDITED;
	        
        } catch (Exception e) {
	        return MESSAGE_INVALID_EDIT;
        }
    }
    
    private static String editTask(int taskIndex, Date time) {
    	try {
	    	listOfTasks.editTaskDeadline(taskIndex, time);
	    	return MESSAGE_TASK_EDITED;
	    	
        } catch (Exception e) {
	        return MESSAGE_INVALID_EDIT;
        }

    }
    
    private static String editTask(int taskIndex, String desc, Date time) {
    	try {
    		listOfTasks.editTaskDescription(taskIndex,desc);
    		listOfTasks.editTaskDeadline(taskIndex, time);
    		return MESSAGE_TASK_EDITED;
    		
    	} catch (Exception e) {
    		return MESSAGE_INVALID_EDIT;
    	}

    }

    /*
    private static String editTask(int taskIndex, String desc, Date time, String repeatPeriod) {
    	listOfTasks.editTaskDescription(taskIndex,desc);
    	listOfTasks.editTaskDeadline(taskIndex, time);
    	listOfTasks.editTaskRepeatPeriod(taskIndex, repeatPeriod);
    	return MESSAGE_TASK_EDITED;
    
    }
    */

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
     * This method will display user's tasks information.
     * 
     */
    private static String display() {
    	StringBuilder taskDisplay = new StringBuilder();
    	taskDisplay.append("Current tasks:\n");
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	for (int i=0; i<listOfTasks.getNumberOfTasks(); i++) {
    		Task task = listOfTasks.get(i);
    		taskDisplay.append((i+1) + ". " + task.getDescription()+"\n");
    		if (task.getType() == Task.Type.Deadline) {
    			DeadlineTask deadlineTask = (DeadlineTask) task;
    			taskDisplay.append("Due: "+dateFormat.format(deadlineTask.getDeadline())+"\n");
    		}
    		if (task.getType() == Task.Type.Fixed) {
    			FixedTask fixedTask = (FixedTask) task;
    			taskDisplay.append("Start: "+dateFormat.format(fixedTask.getStartTime()));
    			taskDisplay.append("\nDue: "+dateFormat.format(fixedTask.getDeadline()+"\n"));
    		}
    		if (task.getType() == Task.Type.Repeated) {
    			RepeatedTask repeatedTask = (RepeatedTask) task;
    			taskDisplay.append("Due: "+dateFormat.format(repeatedTask.getDeadline()));
    			taskDisplay.append("\nRepeat: "+repeatedTask.getRepeatPeriod()+"\n");
    		}
    		if (task.getIsDone()) {
    			taskDisplay.append("Status: Done");
    		} else {
    			taskDisplay.append("Status: Ongoing");
    		}
    		
    		if (i!=listOfTasks.getNumberOfTasks() - 1) {
    			taskDisplay.append("\n\n");
    		} else {
    			taskDisplay.append("\n");
    		}
    	}
    	
    	return taskDisplay.toString();
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
