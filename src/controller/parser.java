package controller;

import java.util.*;

public class parser {
	
	static Scanner scanner=new Scanner(System.in);
	private static final String[] CMD_ARRAY={"add","delete","search","edit","display",
		"redo","undo","clear","tag","done","complete","finish","show",};
	
	
	public static String readLine(){
		return scanner.nextLine().trim();
	}
	
	public static userInput parse(String input){
		String command=input.split(" ",2)[0];
		if(trueCommand(command)){
			
		}
	}

	private static boolean trueCommand(String command) {
		for(String i:CMD_ARRAY)
		if(i.equalsIgnoreCase(command))
			return true;
		return false;
	}

}
