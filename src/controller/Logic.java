package controller;

/**
 *  main class that manages the TaskList
 */

import java.util.Scanner;

public class Logic {
	static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
    	// get existing tasks from storage
    	
    	// get and execute new tasks
    	while (true) {
    		String userInput = getUserInput();
    		
    	}
            
    }
    
    private static String getUserInput() {
    	System.out.print("Enter command: ");
    	String userInput = scanner.nextLine();
    	return userInput;
    }
    
}
