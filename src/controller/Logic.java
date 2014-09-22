package controller;

import java.util.Scanner;
import java.util.Stack;

import model.Task;

/**
 *  main class that manages the TaskList
 */


public class Logic {
    static Scanner scanner = new Scanner(System.in);
    static Stack<UserInput> undoStack = new Stack<UserInput>();
    static Stack<UserInput> redoStack = new Stack<UserInput>();

    public static void main(String[] args) {
        
        // get existing tasks from storage
        Storage storage = new Storage(); 
//        TaskList tasks = storage.load();
        
        // get and execute new tasks
        while (true) {
            String userInput = getUserInput();
            if (userInput.equals("exit")) {
                System.exit(0);
                
            } else {
                // parse and execute command
//                UserInput userCommand = Parser.parse(userInput);
//                executeCommand(userCommand);
            
                
                // update the history and storage file
                
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
        // TODO Auto-generated method stub
    }
    
    
    /**
     *  @param task
     *  
     *  this method will add a task to the file
     */
    private static void addTask(Task task) {
        
    }
    

    /**
     *  @param task
     *  
     *  this method will delete the specified task from the file
     */
    private static void deleteTask(Task task) {
        
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
