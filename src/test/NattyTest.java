package test;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class NattyTest {
	public static void main(String[] args) {

        List<Date> dateList = new ArrayList<Date>();
        Parser parser = new Parser();
        String aa = "asdfghjkl";
		  
        System.out.println(aa.replaceAll("(?i)" + "A", "q"));
        System.out.println("Enter something with a date/time:");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        List<DateGroup> groups = parser.parse(input);
        for (DateGroup group : groups) {
            List<Date> dates = group.getDates();
//		    int line = group.getLine();
//		    System.out.println(line);
//		    int column = group.getPosition();
//		    System.out.println(column);
            String matchingValue = group.getText();
//		    String syntaxTree = group.getSyntaxTree().toStringTree();
//		    System.out.println(syntaxTree);
//		    Map<String, List<ParseLocation>> parseMap = group.getParseLocations();
//		    System.out.println(parseMap);
            boolean isRecurreing = group.isRecurring();
            System.out.println(isRecurreing);
            Date recursUntil = group.getRecursUntil();
            System.out.println(recursUntil);
            dateList.addAll(dates);
            System.out.println(input.replaceAll(matchingValue,""));
		}
		System.out.println(dateList);
	}

}
